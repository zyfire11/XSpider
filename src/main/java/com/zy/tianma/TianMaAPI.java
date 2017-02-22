package com.zy.tianma;

import com.zy.utils.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhouyi on 2017/2/21.
 * 天码api文档详见：http://personal.tianma168.com/api1.html
 */
public class TianMaAPI {

    private static String username="zyfire11";
    private static String password="";
    private static String token;
    private static Double balance;
    private static String itemId="171";
    private static Logger log = Logger.getLogger(TianMaAPI.class);

    public TianMaAPI(){
        doLogin();
    }
    public TianMaAPI(String username, String password){
        this.username = username;
        this.password = password;
        doLogin();
    }

    //登录：http://api.tianma168.com/tm/Login?uName=用户名&pWord=密码&Developer=开发者参数
    public void doLogin(){
        String url = "http://api.tianma168.com/tm/Login";
        url += "?uName="+ username + "&pWord=" + password + "&Developer=";
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println(data);
        if(!data.contains("False")){
            log.info("登陆成功");
            token = data.split("&")[0];
            balance = Double.parseDouble(data.split("&")[1]);
            if(balance < 0.0){
                log.info("账号余额不足");
            }
        }
    }

    //获取项目：http://api.tianma168.com/TM/GetItems?token=token&tp=ut
    private void getItem(){
        String url = "http://api.tianma168.com/TM/GetItems?token=" + token + "&tp=ut";
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println(data);

    }

    //获取号码：http://api.tianma168.com/tm/getPhone?ItemId=项目ID&token=登陆token
    public List<String> getPhone(int num){
        List<String> numberList = new ArrayList<>();
        String url = "http://api.tianma168.com/tm/getPhone?ItemId=" + itemId + "&token=" + token
                + "&Count=" + num + "&Area=&PhoneType=0";
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println("获取到的号码：" + data);
        if(!data.contains("False")){
            String[] arr = data.replaceAll("\\n", "").split(";");
            numberList.addAll(Arrays.asList(arr));
        }
        return numberList;
    }
    //获取短信：http://api.tianma168.com/tm/getMessage?token=登陆token&itemId=项目ID&phone=手机号码
    public String getSMS(String phone){
        int num = 15;
        while (num > 0) {
            String url = "http://api.tianma168.com/tm/getMessage?token=" + token + "&itemId=" + itemId + "&phone=" + phone;
            HttpResponse response = HttpUtils.doGet(url, null);
            String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
            System.out.println("获取到的短信：" + data);
            if (!data.contains("False") && data.contains("MSG&")) {
                String message = data.split("MSG&")[1];
                message = message.split("@")[0];
                return message;
            }else{
                num--;
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //释放手机号：http://api.tianma168.com/tm/releasePhone?token=登陆token&phoneList=phone-itemId;phone-itemId;
    public boolean releasePhone(String phone){
        String list = phone + "-" + itemId + ";";
        String url = "http://api.tianma168.com/tm/releasePhone?token="+ token +"&phoneList=" + list;
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println("释放号码的状态：" + data);
        if(data.contains("OK") || data.contains("RES&")){
            return  true;
        }else{
            return false;
        }
    }

    //发送短信：http://api.tianma168.com/tm/sendMessage?token=登陆token&Phone=手机号&ItemId=项目ID&Msg=短信内容
    public boolean sendMessage(String message, String phone){
        String url = "http://api.tianma168.com/tm/sendMessage?token=" + token + "&Phone=" + phone + "&ItemId="+ itemId +"&Msg=" + message;
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println("发送短信的状态：" + data);
        if(data.contains("OK")){
            return true;
        }else {
            return false;
        }
    }

    //加黑号码
    public boolean blackPhone(String phone){
        String list = phone + "-" + itemId + ";";
        String url = "http://api.tianma168.com/tm/addBlack?token=" + token + "&phoneList=" + list;
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println("加黑号码的状态：" + data);
        //随带释放号码
        releasePhone(phone);
        if(data.contains("Ok") || data.contains("OK") || data.contains("ok")){
            return true;
        }else {
            return false;
        }

    }
    //退出：http://api.tianma168.com/tm/Exit?token=登陆token
    public boolean exit(){
        String url = "http://api.tianma168.com/tm/Exit?token=" + token;
        HttpResponse response = HttpUtils.doGet(url, null);
        String data = HttpUtils.getStringFromResponseByCharset(response, "GB2312");
        System.out.println("退出的状态：" + data);
        if(data.contains("OK")){
            return true;
        }else {
            return false;
        }
    }



    public static void main(String[] args) {
        String username = "zyfire11";
        String password = "";
        TianMaAPI api = new TianMaAPI(username, password);
//        api.doLogin();
        token = "BWjh7ATcE1pNbNBETiui1fZEtkF5i8969";
        api.getItem();
        List<String> numberList  = api.getPhone(2);
        for(String phone : numberList){
            api.releasePhone(phone);
        }
    }
}
