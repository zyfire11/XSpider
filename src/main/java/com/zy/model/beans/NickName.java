package com.zy.model.beans;

import javax.persistence.*;

/**
 * Created by zhouyi on 2017/6/4.
 */
@Entity
@Table(name="nickName")
public class NickName {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String  name;
    private Integer num;

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

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
