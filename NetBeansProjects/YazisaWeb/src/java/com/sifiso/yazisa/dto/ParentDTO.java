/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sifiso.yazisa.dto;

import com.sifiso.yazisa.data.Parent;
import java.util.List;

/**
 *
 * @author CodeTribe1
 */
public class ParentDTO {
private Integer parentID;
    
    private String parentName;
    private String parentSurname;
    private String parentIdNo;
    private String email;
    private String cell;
    private String password;
       private String username;
    private List<LearnersDTO> learnersList;

   public ParentDTO(Parent p) {
   parentID = p.getParentID();
   parentIdNo = p.getParentIdNo();
   parentName = p.getParentName();
   parentSurname = p.getParentSurname();
   email = p.getEmail();
   cell=p.getCell();
   username = p.getUsername();
   password = p.getPassword();
   
   }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentSurname() {
        return parentSurname;
    }

    public void setParentSurname(String parentSurname) {
        this.parentSurname = parentSurname;
    }

    public String getParentIdNo() {
        return parentIdNo;
    }

    public void setParentIdNo(String parentIdNo) {
        this.parentIdNo = parentIdNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<LearnersDTO> getLearnersList() {
        return learnersList;
    }

    public void setLearnersList(List<LearnersDTO> learnersList) {
        this.learnersList = learnersList;
    }
    
}
