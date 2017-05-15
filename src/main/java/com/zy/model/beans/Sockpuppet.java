package com.zy.model.beans;

import javax.persistence.*;

/**
 * Created by zhouyi on 2017/5/15.
 */
@Entity
@Table(name="sockpuppet")
public class Sockpuppet {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String  name;
    private String  password;
    private boolean isRegister = false;
    private Integer sourceId;
    private boolean valid = true;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean isRegister) {
        this.isRegister = isRegister;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
