package com.zy.sina;

import com.zy.tianma.TianMaAPI;
import com.zy.utils.*;
import com.zy.uudama.UUHttpAPI;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import org.apache.struts2.components.Head;
import wuhao.tools.elasticsearch.bean.Http;
import wuhao.tools.reader.PathUtil;
import wuhao.tools.utils.UUIDUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.InputStream;
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
    private static String nickName;
    private static String nonce;
    private static String rsakv;
    private static String servertime;
    private static String pubkey;
    private static String pcid;
    private static List<Cookie> cookies;
    private static String door;
    private static Map<String, String> headers = new HashMap<>();
    private static String locationUrl;
    private static String cookieStr;
    private static Logger log = Logger.getLogger(LoginSina.class);


    public LoginSina(String username, String password, String nickName){
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        init();
    }

    //初始化得到服务区的时间和一次性字符串
    public void init(){
        String url=compositeUrl();
//        Map<String,String> headers=new HashMap<String,String>();
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
        String responseText=HttpUtils.getStringFromResponseByCharset(response, "gbk");
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
            this.cookieStr = cookieStr;
            this.cookies = HttpUtils.getResponseCookies(response);
            response = HttpUtils.doGet("http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com", headers);

            responseText = HttpUtils.getStringFromResponseByCharset(response, "utf8");
            System.out.println(responseText);
            register(responseText, nickName);
        }

        this.cookieStr = HttpUtils.setCookie2String(this.cookies);
        return HttpUtils.setCookie2String(this.cookies);
    }

    private void register(String html, String nickName){


        Map<String, String> params = new HashMap<>();
        //校对用户名
        nickName = repeatName(nickName);
        params.put("nickname", nickName);

        String birthday = "1990-1-1";
        String gender = "1";   //1为男
        Map<String, String> inputMap = new HashMap<>();
        List<String> inputList = RegexUtil.getAllMatcherListRegex(html, "<input type=\"hidden\" name=\"[\\da-z]{32}\" value=\"[\\da-z]{32}\">");
        for(String input : inputList){
            String key = RegexUtil.getMatchGroupRegex(input, "name=\"([\\da-z]{32})\" value=\"([\\da-z]{32})\">", 1);
            String value = RegexUtil.getMatchGroupRegex(input, "name=\"([\\da-z]{32})\" value=\"([\\da-z]{32})\">", 2);
            inputMap.put(key, value);
        }
        String regtime = RegexUtil.getMatchGroupRegex(html, "name=\"regtime\" value=\"(.*?)\"");
        String salttime = RegexUtil.getMatchGroupRegex(html, "name=\"salttime\" value=\"(.*?)\"");
        String sinaid = RegexUtil.getMatchGroupRegex(html, "name=\"sinaid\" value=\"(.*?)\"");
        String page = RegexUtil.getMatchGroupRegex(html, "name=\"page\" value=\"(.*?)\"");
        String lang = RegexUtil.getMatchGroupRegex(html, "name=\"lang\" value=\"(.*?)\"");
        String province = "32";
        String city = "1";
        String rejectFake = "clickCount%3D10%26subBtnClick%3D0%26keyPress%3D14%26menuClick%3D0%26mouseMove%3D2407%26checkcode%3D0%26subBtnPosx%3D792%26subBtnPosy%3D521%26subBtnDelay%3D167%26keycode%3D97%2C115%2C100%2C115%2C115%2C97%2C100%2C97%2C115%2C100%2C109%2C107%2C119%2C55%26winWidth%3D1920%26winHeight%3D955%26userAgent%3DMozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F47.0.2526.106%20Safari%2F537.36";
        String replaceurl = "http://weibo.com/signup/v5/ajaxreg";

        params.put("birthday", birthday);
        params.put("gender", gender);
        for(Map.Entry<String, String> entry : inputMap.entrySet()){
            params.put(entry.getKey(), entry.getValue());
        }
        params.put("regtime", regtime);
        params.put("salttime", salttime);
        params.put("sinaid", sinaid);
        params.put("page", page);
        params.put("lang", lang);
        params.put("province", province);
        params.put("city", city);
        params.put("rejectFake", rejectFake);
        params.put("replaceurl", replaceurl);

        //获取验证码
        String path = downloadCheckImage(sinaid, regtime);
        try {
            UUHttpAPI api = new UUHttpAPI();
            api.setIMGPATH(path);
            String pincode = api.getImageValue("1004");
            System.out.println("验证码：" + pincode);


            params.put("pincode", pincode);
        }catch (Exception e){
            e.printStackTrace();
        }


        String url = "http://weibo.com/signup/v5/fullinfo";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Referer", "http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com");
        headers.put("Accept-Language", "zh-cn");
        headers.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN");
        headers.put("Host", "weibo.com");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Cookie", cookieStr);

        HttpResponse response = HttpUtils.doPost(url, headers, params);
        String data = HttpUtils.getStringFromResponse(response);
        System.out.println("提交注册表单得到：" + data);

        String smsUrl = RegexUtil.getMatchGroupRegex(data, "location.replace\\(\"(.*?)\"\\)");
        if(!smsUrl.equals("")){
            log.info("注册信息提交无误");
            //调用天码获取电话号码
            TianMaAPI tianma = new TianMaAPI();
            List<String> phoneList = tianma.getPhone(2);
            String realPhone = "";
            while(true){
                for(String phone : phoneList){
                    //获取到号码其他号码就释放掉
                    if(!realPhone.equals("")){
                        tianma.releasePhone(phone);
                    }
                    boolean flag = sendMesage(phone);
                    if(flag){
                        realPhone = phone;
                    }else {
                        //不能注册就拉黑
                        tianma.blackPhone(phone);
                    }
                }
                if(!realPhone.equals("")){
                    break;
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                phoneList = tianma.getPhone(2);
            }
            log.info("账号：" + username + ",绑定到：" + realPhone);

        }else{
            log.info("注册信息提交出现问题");
        }

    }

    // 下载验证码
    private String downloadCheckImage(String sinaId, String regtime) {

        String cookieValue = HttpUtils.setCookie2String(this.cookies);
        this.headers.put("Cookie", cookieValue);
        this.headers.put("Host", "weibo.com");
//        headers.put("Referer", "http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com");
        headers.put("Referer", "http://weibo.com/signup/full_info.php");
        String url = "http://weibo.com/signup/v5/pincode/pincode.php?lang=zh&sinaId="+ sinaId +"&r=" + regtime;
        HttpResponse response = HttpUtils.doGet(url, headers);
        InputStream in = HttpUtils.getInputStreamFromResponse(response);
        String path = PathUtil.getProjectRoot() + "/web/image/validate_code/" + UUIDUtil.getUUID(false) + ".jpeg";
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Utils.writeFileFromStream(path, in);
        return path;
    }

    private String repeatName(String name){
        String url = "http://weibo.com/signup/v5/formcheck?type=nickname&value=" + name +"&__rnd=" + System.currentTimeMillis();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Referer", "http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com");
        headers.put("Accept-Language", "zh-cn");
        headers.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN");
        headers.put("Host", "weibo.com");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Cookie", cookieStr);
        HttpResponse response = HttpUtils.doGet(url, headers);
        String data = HttpUtils.getStringFromResponse(response);
        System.out.println(data);
        if(data.contains("\"code\":\"100000\"")){
            System.out.println("昵称是新的");
            return name;
        }else {
            //拿推荐昵称中长度最短的
            String message = EncodeUtils.unicdoeToGB2312(RegexUtil.getMatchGroupRegex(data, "\"msg\":\"(.*?)\""));
            String iodata = EncodeUtils.unicdoeToGB2312(RegexUtil.getMatchGroupRegex(data, "\"iodata\":\\[(.*?)\\]\\}"));
            System.out.println("昵称重合提示信息：" + message + ",推荐下列未使用过的昵称：" + iodata);
            List<String> nameList = RegexUtil.getAllMatcherListRegexGroup(iodata, "\"(.*?)\"");
            name = nameList.get(0);
            for(String nickName : nameList){
                if(name.length() > nickName.length()){
                    name = nickName;
                }
            }
            log.info("获取推断昵称：" + name);
            return name;
        }


    }

    //手机号码是否已被注册
    private boolean sendMesage(String phone){
        String url = "http://weibo.com/signup/v5/formcheck?type=verifybind&zone=0086&value=" + phone + "&_t=0&__rnd=" + System.currentTimeMillis();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Referer", "http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com");
        headers.put("Accept-Language", "zh-cn");
        headers.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN");
        headers.put("Host", "weibo.com");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Cookie", cookieStr);
        HttpResponse response = HttpUtils.doGet(url, headers);
        String data = HttpUtils.getStringFromResponseByCharset(response, "utf8");
        System.out.println(data);
        if(data.contains("\"type\":\"err\"")){
            String message = EncodeUtils.unicdoeToGB2312(RegexUtil.getMatchGroupRegex(data, "\"msg\":\"(.*?)<a"));
            log.info("手机号码：" + phone + "，不能绑定：" + message);
            return false;
        }else{
            return true;
        }
    }

    private void getAuthCode(){

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
//        String nickName = "asdssadasd";
        String nickName = "asdf";
        LoginSina login = new LoginSina(username, password, nickName);
        login.dologinSina();

    }
}
