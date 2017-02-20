package com.zy.sina;

import com.zy.utils.Base64Encoder;
import com.zy.utils.EncodeUtils;
import com.zy.utils.HttpUtils;
import com.zy.utils.RegexUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.struts2.components.Head;
import wuhao.tools.elasticsearch.bean.Http;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhouyi on 2017/2/20.
 */
public class LoginSina {

    private static String username;
    private static String password;
    private static String nonce;
    private static String rsakv;
    private static String servertime;
    private static String pubkey;
    private static String pcid;
    private static List<Cookie> cookies;
    private static String door;
    private static Map<String, String> headers;
    private static String locationUrl;


    public LoginSina(String username, String password){
        this.username = username;
        this.password = password;
        init();
    }

    //初始化得到服务区的时间和一次性字符串
    public void init(){
        String url=compositeUrl();
        Map<String,String> headers=new HashMap<String,String>();
        headers.put("Accept", "*/*");
        headers.put("Referer", "http://weibo.com/");
        headers.put("Accept-Language", "zh-cn");
        headers.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; QQDownload 691)");
        headers.put("Host", "login.sina.com.cn");
        headers.put("Connection", "Keep-Alive");

        System.out.println(url);
        HttpResponse response=HttpUtils.doGet(url, headers);
        String responseText=HttpUtils.getStringFromResponse(response);
        System.out.println(responseText);
        int begin=responseText.indexOf("{");
        int end=responseText.lastIndexOf("}");
        responseText=responseText.substring(begin,end+1);
        System.out.println(responseText);
        this.nonce= RegexUtil.getMatchGroupRegex(responseText, "\"nonce\":\"(.*?)\"");
        this.servertime=RegexUtil.getMatchGroupRegex(responseText, "\"servertime\":(.*?),");
        this.pubkey=RegexUtil.getMatchGroupRegex(responseText, "\"pubkey\":\"(.*?)\"");
        this.rsakv=RegexUtil.getMatchGroupRegex(responseText, "\"rsakv\":\"(.*?)\"");
        this.pcid=RegexUtil.getMatchGroupRegex(responseText, "\"pcid\":\"(.*?)\"");
    }

    //登录微博
    public String dologinSina(){
        System.out.println("---do login ");
        String url="http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)";//v1.3.17
        Map<String,String> headers=new HashMap<String,String>();
        Map<String,String> params=new HashMap<String,String>();

        headers.put("Accept", "text/html, application/xhtml+xml, */*");
        headers.put("Referer", "http://weibo.com/");
        headers.put("Accept-Language", "zh-cn");
        headers.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN");
        headers.put("Host", "login.sina.com.cn");
        headers.put("Connection", "Keep-Alive");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Cache-Control", "no-cache");

        params.put("encoding", "UTF-8");
        params.put("entry", "weibo");

//		params.put("prelt", "112");
        params.put("gateway", "1");
        params.put("from", "");
        params.put("savestate", "7");
        params.put("useticket", "1");
        params.put("pagerefer", "");
        params.put("vsnf", "1");

        params.put("su", getEncodedU());
        params.put("service", "miniblog");
        params.put("servertime", servertime);
        params.put("nonce", nonce);
        params.put("pwencode", "rsa2");//wsse
        params.put("rsakv", rsakv);
        params.put("sp", getEncryptedP());

        params.put("prelt", "149");

        params.put("url", "http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
        params.put("returntype", "META");
//        params.put("ssosimplelogin", "1");

        if(this.pcid!=null)
//            params.put("pcid", pcid);
        if(this.door!= null)
            params.put("door", this.door);

        HttpResponse response= HttpUtils.doPost(url, headers, params);
        this.cookies=HttpUtils.getResponseCookies(response);

        System.out.println(HttpUtils.setCookie2String(this.cookies));

        this.headers=headers;
        String responseText=HttpUtils.getStringFromResponse2(response, "gbk");
        try {
//			responseText=new String(responseText.getBytes(),"GBK");
            responseText= URLDecoder.decode(responseText, "GBK");
            if(!responseText.contains("retcode=0")){

//				this.door=s.next();
//				return dologinSina();
            }else{
                System.out.println("login success!");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

//		String locationRegex = "location.replace[\\s\\S]*\\\"(.*)\\\".;";
        String locationRegex = "location.replace\\(\\'(.*?)\\'\\);";

        Pattern pattern = Pattern.compile(locationRegex);
        Matcher matcher = pattern.matcher(responseText);
        if(matcher.find()){
            locationUrl = matcher.group(1);
            System.out.println(locationUrl);
        }
//
//		HttpResponse response2=HttpUtils.doGet(locationUrl, this.headers);
//		String text=HttpUtils.getStringFromResponse(response2);
//		System.out.println(text);

        //切换cookie
        headers.put("Host", "passport.weibo.com");
        headers.put("Referer", "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
        headers.put("Upgrade-Insecure-Requests", "1");
        String cookieStr = HttpUtils.setCookie2String(this.cookies);
        headers.put("Cookie", cookieStr);

        response = HttpUtils.doGetNoDirect(locationUrl, headers);
        List<Header> headers2 = HttpUtils.getReponseHeaders(response);
        String reUrl = "";
        for(Header header : headers2){
            if(header.getName().equals("Location")){
                reUrl = header.getValue();
            }
        }

        //进入基本信息设置页面
        if(!reUrl.equals("")){
            cookieStr = HttpUtils.setCookie2String(HttpUtils.getResponseCookies(response));
            headers.put("Host", "weibo.com");
            headers.put("Referer", "http://weibo.com/");
            headers.put("Cookie", cookieStr);
            response = HttpUtils.doGet("http://weibo.com/signup/full_info.php", headers);

            responseText = HttpUtils.getStringFromResponse2(response, "utf8");
            System.out.println(responseText);
        }


        return HttpUtils.setCookie2String(this.cookies);
    }


    //生成一次性的字符串 6位
    private String getnonce() {
        String x = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String str = "";
        for (int i = 0; i < 6; i++) {
            str += x.charAt((int)Math.ceil(Math.random() * 1000000) % x.length());
        }
        return str;
    }
    //组合预登录时的URL
    private String compositeUrl(){
        StringBuilder builder=new StringBuilder();
//		builder.append("http://login.sina.com.cn/sso/prelogin.php?")
//			   .append("entry=weibo&callback=sinaSSOController.preloginCallBack&")
//			   .append("su="+getEncodedU())
//			   .append("&client=ssologin.js(v1.3.17)&_="+System.currentTimeMillis());
        builder.append("http://login.sina.com.cn/sso/prelogin.php?")
                .append("entry=weibo&callback=sinaSSOController.preloginCallBack&")
                .append("su="+getEncodedU())
                .append("&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.5)&_="+System.currentTimeMillis());
        return builder.toString();
    }
    //对用户名进行编码
    private String getEncodedU() {
        if(username!=null && username.length()>0){
            return Base64Encoder.encode(EncodeUtils.encodeURL(username, "utf-8").getBytes());
        }
        return "";
    }
    //对密码进行编码
    private String getEncryptedP(){
//		return EncodeSuAndSp.getEncryptedP(password, servertime, nonce);
        String data=servertime+"\t"+nonce+"\n"+password;
        String spT=rsaCrypt(pubkey, "10001", data);
        return spT;
    }

    public static String rsaCrypt(String pubkey, String exponentHex, String pwd,String servertime,String nonce) {
        String data=servertime+"\t"+nonce+"\n"+pwd;
        return rsaCrypt(pubkey,exponentHex,data);
    }
    /**
     *e.rsakv = a.rsakv;
     var f = new sinaSSOEncoder.RSAKey;
     f.setPublic(a.rsaPubkey, "10001");
     c = f.encrypt([a.servertime, a.nonce].join("\t") + "\n" + c)
     */
    public static String rsaCrypt(String pubkey, String exponentHex, String messageg) {
        KeyFactory factory=null;
        try {
            factory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e1) {
            return "";
        }
        BigInteger publicExponent = new BigInteger(pubkey, 16); /* public exponent */
        BigInteger modulus = new BigInteger(exponentHex, 16); /* modulus */
        RSAPublicKeySpec spec = new RSAPublicKeySpec(publicExponent, modulus);
        RSAPublicKey pub=null;
        try {
            pub = (RSAPublicKey) factory.generatePublic(spec);
        } catch (InvalidKeySpecException e1) {
            return "";
        }
        Cipher enc=null;
        byte[] encryptedContentKey =null;
        try {
            enc = Cipher.getInstance("RSA");
            enc.init(Cipher.ENCRYPT_MODE, pub);
            encryptedContentKey = enc.doFinal(messageg.getBytes());
        } catch (NoSuchAlgorithmException e1) {
            System.out.println(e1.getMessage());
            return "";
        } catch (NoSuchPaddingException e1) {
            System.out.println(e1.getMessage());
            return "";
        } catch (InvalidKeyException e1) {
            System.out.println(e1.getMessage());
            return "";
        } catch (IllegalBlockSizeException e1) {
            System.out.println(e1.getMessage());
            return "";
        } catch (BadPaddingException e1) {
            System.out.println(e1.getMessage());
            return "";
        }
        return new String(Hex.encodeHex(encryptedContentKey));
    }

    public static void main(String[] args) {
        String username = "yizh199202@sina.com";
        String password = "asdf1234";
        LoginSina login = new LoginSina(username, password);
        login.dologinSina();
    }
}
