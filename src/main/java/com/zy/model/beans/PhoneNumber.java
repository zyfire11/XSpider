package com.zy.model.beans;

import javax.persistence.*;

/**
 * Created by zhouyi on 2017/5/15.
 */

@Entity
@Table(name="phone_number")
public class PhoneNumber {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String  name;
    private Integer type;  //1、联通；2、移动；3、电信
    private boolean used = false;
    private boolean valid = true;
    //使用次数
    private Integer usedNum = 0;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Integer getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(Integer usedNum) {
        this.usedNum = usedNum;
    }
}
