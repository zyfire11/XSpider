package com.zy.model.entity;

/**
 * Created by zhouyi on 2017/6/4.
 */
public class StateCode {

    //注册失败
    public static final Integer ERROR = 0;
    //注册成功
    public static final Integer SUCCESS = 1;
    //邮箱错误
    public static final Integer EMAIL_ERROR = 2;
    //邮箱已被注册
    public static final Integer EMAIL_REGISTERED = 3;
    //手机已被绑定
    public static final Integer PHONE_REGISTERED = 4;
    //手机验证太频繁
    public static final Integer PHONE_REGISTER_FREQUENTLY = 5;
    //uu打码出错
    public static final Integer UUDM_ERROR = 6;
    //uu打码欠费
    public static final Integer UUDM_ARREARS = 7;
    //登录错误
    public static final Integer LOGIN_ERROR = 8;
    //注册信息错误
    public static final Integer LOGIN_INFO_ERROR = 9;


}
