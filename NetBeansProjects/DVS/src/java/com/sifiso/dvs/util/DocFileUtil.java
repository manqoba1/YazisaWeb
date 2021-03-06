/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sifiso.dvs.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sifiso.dvs.dto.PatientfileDTO;
import com.sifiso.dvs.gate.dto.RequestDTO;
import com.sifiso.dvs.gate.dto.ResponseDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.joda.time.DateTime;

/**
 *
 * @author CodeTribe1
 */
@Stateless
public class DocFileUtil {

    public ResponseDTO downloadPDF(HttpServletRequest request, PlatformUtil platformUtil) throws FileUploadException {
        logger.log(Level.INFO, "######### starting PDF DOWNLOAD process\n\n");
        ResponseDTO resp = new ResponseDTO();
        InputStream stream = null;
        File rootDir;
        try {
            rootDir = dvsProperties.getDocumentDir();
            logger.log(Level.INFO, "rootDir - {0}", rootDir.getAbsolutePath());
            if (!rootDir.exists()) {
                rootDir.mkdir();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Properties file problem", ex);
            resp.setMessage("Server file unavailable. Please try later");
            resp.setStatusCode(114);

            return resp;
        }

        PatientfileDTO dto = null;
        Gson gson = new Gson();
        File clientDir = null, surgeryDir = null, doctorDir = null;
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String name = item.getFieldName();
                stream = item.openStream();
                if (item.isFormField()) {
                    if (name.equalsIgnoreCase("JSON")) {
                        String json = Streams.asString(stream);
                        if (json != null) {
                            logger.log(Level.INFO, "picture with associated json: {0}", json);
                            dto = gson.fromJson(json, PatientfileDTO.class);
                            if (dto != null) {
                                surgeryDir = createSurgeryFileDirectory(rootDir, surgeryDir, dto.getDoctor().getSurgeryID());
                                if (dto.getDoctorID() != null) {
                                    doctorDir = createDoctorDirectory(surgeryDir, doctorDir, dto.getDoctorID());
                                    if (dto.getClientID() != null) {
                                        clientDir = createClientDirectory(doctorDir, clientDir);
                                    }
                                }

                            }
                        } else {
                            logger.log(Level.WARNING, "JSON input seems pretty fucked up! is NULL..");
                        }
                    }
                } else {
                    File imageFile = null;
                    if (dto == null) {
                        continue;
                    }
                    DateTime dt = new DateTime();
                    String fileName = "";

                    if (dto.getClientID() != null) {
                        fileName = "client" + dto.getClientID() + ".pdf";
                    }

                    imageFile = new File(clientDir, fileName);

                    writeFile(stream, imageFile);
                    resp.setStatusCode(0);
                    resp.setMessage("Photo downloaded from mobile app ");
                    //add database
                    System.out.println("filepath: " + imageFile.getAbsolutePath());

                }
            }

        } catch (FileUploadException | IOException | JsonSyntaxException ex) {
            logger.log(Level.SEVERE, "Servlet failed on IOException, images NOT uploaded", ex);
            throw new FileUploadException();
        }

        return resp;
    }

    private File createSurgeryFileDirectory(File rootDir, File surgeryFile, int id) {
        logger.log(Level.INFO, "task photo to be downloaded");
        surgeryFile = new File(rootDir, RequestDTO.SURGERY_DIR + id);
        if (!surgeryFile.exists()) {
            surgeryFile.mkdir();
            logger.log(Level.INFO, "task  directory created - {0}",
                    surgeryFile.getAbsolutePath());

        }
        return surgeryFile;
    }

    private File createDoctorDirectory(File surgeryDir, File doctorDir, int id) {
        logger.log(Level.INFO, "client pdf to be downloaded");
        doctorDir = new File(surgeryDir, RequestDTO.DOCTOR_DIR + id);
        if (!doctorDir.exists()) {
            doctorDir.mkdir();
            logger.log(Level.INFO, "client pdf  directory created - {0}",
                    doctorDir.getAbsolutePath());

        }
        return doctorDir;
    }

    private File createClientDirectory(File doctorDir, File clientDir) {
        logger.log(Level.INFO, "client pdf to be downloaded");
        clientDir = new File(doctorDir, RequestDTO.CLIENTS_DIR);
        if (!clientDir.exists()) {
            clientDir.mkdir();
            logger.log(Level.INFO, "client pdf  directory created - {0}",
                    clientDir.getAbsolutePath());

        }
        return clientDir;
    }

    private void writeFile(InputStream stream, File imageFile) throws FileNotFoundException, IOException {

        if (imageFile == null) {
            throw new FileNotFoundException();
        }
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            int read;
            byte[] bytes = new byte[2048];
            while ((read = stream.read(bytes)) != -1) {
                fos.write(bytes, 0, read);
            }
            stream.close();
            fos.flush();
        }

        logger.log(Level.WARNING, "### File downloaded: {0} size: {1}",
                new Object[]{imageFile.getAbsolutePath(), imageFile.length()});
    }

    public static double getElapsed(long start, long end) {
        BigDecimal m = new BigDecimal(end - start).divide(new BigDecimal(1000));
        return m.doubleValue();
    }
    static final Logger logger = Logger.getLogger("DocFileUtil");
}
