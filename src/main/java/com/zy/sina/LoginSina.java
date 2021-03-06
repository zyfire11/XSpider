package com.zy.sina;

import com.zy.model.entity.StateCode;
import com.zy.tianma.TianMaAPI;
import com.zy.utils.*;
import com.zy.uudama.UUHttpAPI;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;
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
import java.net.URLEncoder;
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
    private static String phoneNumber;
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
    private int                damaNum = 0;

    //手机号码rsa加密的公钥
    private static String configKey;
    private static String configKeyPlus;
    private static Logger log = Logger.getLogger(LoginSina.class);


    public LoginSina(String username, String password, String nickName){
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        init();
    }

    public LoginSina(String username, String password, String nickName, String phoneNumber){
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
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
    public Integer dologinSina(){

        int stateCode = 1;
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

        if(this.pcid!=null) {
            params.put("pcid", pcid);
        }
        if(this.door!= null) {
            params.put("door", this.door);
        }

//        HttpResponse response= HttpUtils.doPost(url, headers, params);
        HttpResponse response= HttpUtils.doPostByProxy(url, headers, params, true);
        this.cookies=HttpUtils.getResponseCookies(response);

//        System.out.println(HttpUtils.setCookie2String(this.cookies));

        this.headers=headers;
        String responseText=HttpUtils.getStringFromResponseByCharset(response, "gbk");
        try {
//			responseText=new String(responseText.getBytes(),"GBK");
            responseText= URLDecoder.decode(responseText, "GBK");
            if(!responseText.contains("retcode=0")){

                if(damaNum < 2){
                    damaNum ++;
                    String imagePath = downloadCheckImageOne();
                    this.nonce = getnonce();
                    // Scanner s=new Scanner(System.in);
                    if (responseText.contains("retcode=4049")) {
                        System.out.println("请输入验证码:");
                    } else if (responseText.contains("retcode=2070")) {
                        System.out.println("验证码不正确，请再次输入验证码:");
                    } else if (responseText.contains("retcode=101")) {
                        System.out.println("用户名或密码不正确");
                        stateCode = StateCode.EMAIL_ERROR;
                    }
                    if (responseText.contains("retcode=4049") || responseText.contains("retcode=2070")) {
                        try {
                            //调用http接口
                            UUHttpAPI uuApi = new UUHttpAPI();
                            uuApi.setIMGPATH(imagePath);
                            this.door = uuApi.getImageValue("1005");
                            if(this.door==null){
                                if(uuApi.getUserId() < 0) {
                                    stateCode = StateCode.UUDM_ARREARS;
                                }else {
                                    stateCode = StateCode.UUDM_ERROR;
                                }

//                                this.exMap.put("code", "8");
//                                this.exMap.put("message", "出现验证码但是验证码平台调用出错");
                            }
//							pauseLogin = true;
                            // this.door=s.next();
                            return dologinSina();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
//				this.door=s.next();
//				return dologinSina();
            }else{
                System.out.println("login success!");
            }


            String locationRegex = "location.replace\\(\\'(.*?)\\'\\);";

            Pattern pattern = Pattern.compile(locationRegex);
            Matcher matcher = pattern.matcher(responseText);
            if(matcher.find()){
                locationUrl = matcher.group(1);
                System.out.println(locationUrl);
            }


            //切换cookie
            headers.put("Host", "passport.weibo.com");
            headers.put("Referer", "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.18)");
            headers.put("Upgrade-Insecure-Requests", "1");
            String cookieStr = HttpUtils.setCookie2String(this.cookies);
            headers.put("Cookie", cookieStr);

//            response = HttpUtils.doGetNoDirect(locationUrl, headers);
            response = HttpUtils.doGetNoDirectByProxy(locationUrl, headers, true);



        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            stateCode = StateCode.LOGIN_ERROR;
        }

        if(response == null){
            return StateCode.EMAIL_ERROR;
        }
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
//            response = HttpUtils.doGet("http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com", headers);
            response = HttpUtils.doGetByProxy("http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com", headers, true);

            responseText = HttpUtils.getStringFromResponseByCharset(response, "utf8");
//            System.out.println(responseText);
            if(responseText.contains("File not found")){
                stateCode = StateCode.EMAIL_REGISTERED;
            }else {
                stateCode = register(responseText, nickName);
            }
        }



        return stateCode;
//        this.cookieStr = HttpUtils.setCookie2String(this.cookies);
//        return HttpUtils.setCookie2String(this.cookies);
    }

    // 下载验证码
    private String downloadCheckImageOne() {
        if (pcid == null) {
            return null;
        }
        this.headers.remove("Content-Type");
        if(this.cookies != null){
            this.cookies.clear();
        }
        String cookieValue = HttpUtils.setCookie2String(this.cookies);
        this.headers.put("Cookie", cookieValue);
        String url = "http://login.sina.com.cn/cgi/pin.php?r=" + (long) (Math.random() * 100000000) + "&s=0&p=" + this.pcid;
        HttpResponse response = HttpUtils.doGet(url, headers);
//		HttpResponse response = HttpUtils.doGetByProxy(url, headers, false);
        InputStream in = HttpUtils.getInputStreamFromResponse(response);
        String path = PathUtil.getProjectRoot() + "/image/validate_code/" + UUIDUtil.getUUID(false) + ".jpeg";
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        Utils.writeFileFromStream(path, in);
        return path;
    }


    private int register(String html, String nickName){

        int code = 1;

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
        String rejectFake = "clickCount%3D11%26subBtnClick%3D0%26keyPress%3D12%26menuClick%3D0%26mouseMove%3D2241%26checkcode%3D0%26subBtnPosx%3D734%26subBtnPosy%3D745%26subBtnDelay%3D124%26keycode%3D100%2C97%2C115%2C115%2C100%2C97%2C115%2C100%2C119%2C106%2C51%2C121%26winWidth%3D1920%26winHeight%3D955%26userAgent%3DMozilla%2F5.0%20(Windows%20NT%206.1%3B%20WOW64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F47.0.2526.106%20Safari%2F537.36";
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
        /**
        String path = downloadCheckImage(sinaid, regtime);
        try {
            UUHttpAPI api = new UUHttpAPI();
            api.setIMGPATH(path);
            String pincode = api.getImageValue("1004");
            System.out.println("验证码：" + pincode);


            params.put("pincode", pincode);
        }catch (Exception e){
            e.printStackTrace();
            return StateCode.UUDM_ERROR;
        }
         **/
        //此处验证码可以乱填
        params.put("pincode", "yyyy");


        String url = "http://weibo.com/signup/v5/fullinfo";
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        headers.put("Referer", "http://weibo.com/signup/full_info.php?nonick=1&lang=zh-cn&callback=http%3A%2F%2Fweibo.com");
        headers.put("Accept-Language", "zh-cn");
        headers.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; BOIE9;ZHCN");
        headers.put("Host", "weibo.com");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        headers.put("Cookie", cookieStr);

//        HttpResponse response = HttpUtils.doPost(url, headers, params);
        HttpResponse response = HttpUtils.doPostByProxy(url, headers, params);
        String data = HttpUtils.getStringFromResponse(response);
//        System.out.println("提交注册表单得到：" + data);

        String smsUrl = RegexUtil.getMatchGroupRegex(data, "location.replace\\(\"(.*?)\"\\)");
        if(!smsUrl.equals("")){
            log.info("注册信息提交无误");
            //调用天码获取电话号码
            String realPhone = phoneNumber;
//            TianMaAPI tianma = new TianMaAPI();
//            List<String> phoneList = tianma.getPhone(10);
//
//            while(true){
//                for(String phone : phoneList){
//                    //获取到号码其他号码就释放掉
//                    if(!realPhone.equals("")){
//                        tianma.releasePhone(phone);
//                    }
//                    boolean flag = sendMesage(phone);
//                    if(flag){
//                        realPhone = phone;
//                    }else {
//                        //不能注册就拉黑
//                        tianma.blackPhone(phone);
//                    }
//                }
//                if(!realPhone.equals("")){
//                    break;
//                }
//                try {
//                    Thread.sleep(1000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                phoneList = tianma.getPhone(10);
//            }
            log.info("账号：" + username + ",绑定到：" + realPhone);

            //获取mobile的加密代码
            configKey = RegexUtil.getMatchGroupRegex(html, "\\$CONFIG\\.key = '(.*?)'");
            configKeyPlus = RegexUtil.getMatchGroupRegex(html, "\\$CONFIG\\.key_plus = '(.*?)'");
            String rsaStr = rsaCrypt(configKey, configKeyPlus, realPhone);

            /*
            //发送验证短信
            url = "http://weibo.com/signup/v5/getpincode?entry=&mobile=" + rsaStr + "&_t=0&__rnd=" + System.currentTimeMillis();
            response = HttpUtils.doGet(url, headers);
            data = HttpUtils.getStringFromResponse(response);
            data = EncodeUtils.unicdoeToGB2312(data).replaceAll("\\\\", "");
            System.out.println(data);

            String code = RegexUtil.getMatchGroupRegex(data, "<span class=\"spc_txt\">(\\d{6})</span> 至 <span class=\"spc_txt\">(.*?)</span>", 1);
            String toPhone = RegexUtil.getMatchGroupRegex(data, "<span class=\"spc_txt\">(\\d{6})</span> 至 <span class=\"spc_txt\">(.*?)</span>", 2);
            toPhone = toPhone.replaceAll(" ", "");
            System.out.println("手机验证码：" + code + ",发送到：" + toPhone);
            **/

            //接收验证码（验证码可以为错误的）
            url = "http://weibo.com/signup/v5/registercheck?entry=&checktype=checkPinMessage&_t=1&callback=STK_"+ System.currentTimeMillis();
//            response = HttpUtils.doGet(url, headers);
            response = HttpUtils.doGetByProxy(url, headers);
            data = HttpUtils.getStringFromResponse(response);
            data = EncodeUtils.unicdoeToGB2312(data).replaceAll("\\\\", "");
//            System.out.println(data);

            url = "http://weibo.com/signup/v5/formcheck?page=email&type=sendsms&value=" + realPhone + "&zone=0086&callback=STK_" + System.currentTimeMillis();
//            response = HttpUtils.doGet(url, headers);
            response = HttpUtils.doGetByProxy(url, headers);
            data = HttpUtils.getStringFromResponse(response);
            data = EncodeUtils.unicdoeToGB2312(data).replaceAll("\\\\", "");
            System.out.println(data);




            if(data.contains("\"code\":\"100000") && data.contains("激活码发送成功")) {
                params.put("zone", "0086");
                //短信验证码啥都可以
                params.put("pincode", "596458");
                params.put("validateExtra", "1");
                params.put("moible", realPhone);
                url = "http://weibo.com/signup/v5/fullinfo";
//                response = HttpUtils.doPost(url, headers, params);
                response = HttpUtils.doPostByProxy(url, headers, params, true);
                data = HttpUtils.getStringFromResponse(response);
                try {
                    System.out.println("手机验证码提交后得到：<" + EncodeUtils.unicdoeToGB2312(URLDecoder.decode(RegexUtil.getMatchGroupRegex(data, "location.replace\\(\"(.*?)\"\\)"),"utf8")) + ">");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (data.contains("code=100000")) {
                    log.info("注册成功");
                    code = StateCode.SUCCESS;
                } else {
                    String codeStr = RegexUtil.getMatchGroupRegex(data, "location.replace\\(\"(.*?)\"\\)");
                    codeStr = EncodeUtils.unicdoeToGB2312(EncodeUtils.decodeURL(codeStr, "utf-8"));
                    log.info("注册失败,失败原因：" + codeStr );
                    code = StateCode.PHONE_REGISTER_FREQUENTLY;
                }
            }else{
                log.info("号码：<" + realPhone + ">不能注册");
                code = StateCode.PHONE_REGISTERED;
            }

        }else{
            log.info("注册信息提交出现问题");
            code = StateCode.LOGIN_INFO_ERROR;
        }

        //兴趣选择
        if(code == StateCode.SUCCESS){
            url = "http://weibo.com/nguide/aj/finish4";
            params = new HashMap<>();
            params.put("data", "{\"1042015:tagCategory_033\":[\"1992769421\",\"5337879011\",\"3487640507\",\"1769684987\",\"3945917804\"]}");
            headers = new HashMap<>();
            headers.put("Host", "weibo.com");
            headers.put("Origin", "http://weibo.com");
            headers.put("X-Requested-With", "XMLHttpRequest");
            headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            headers.put("Accept", "*/*");
            headers.put("Referer", "http://weibo.com/nguide/interests");
            headers.put("Accept-Language", "zh-CN,zh;q=0.8");
            headers.put("Cookie", cookieStr);

            response = HttpUtils.doPostByProxy(url, headers, params);
            data = HttpUtils.getStringFromResponse(response);
            System.out.println(data);
            if(data.contains("\"code\":\"100000\"")){
                System.out.println("兴趣关注成功");
            }else{
                System.out.println("兴趣关注失败");
            }

        }

        return code;
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
        String username = "yizh199205@sina.com";
        String password = "asdf1234";
//        String nickName = "asdssadasd";
        String nickName = "asdf";
        LoginSina login = new LoginSina(username, password, nickName);
        login.dologinSina();

        String key = "BD325CE52FC6BA090AC0C7A2039236587F99C30FA518F601F2AD33019514EE5A4340A964853E1BDF5374AB4AC22F5CFF3288E5DB94E6752B4999972DF4E23DACACAE4E4DCFB6CBAE256F1B19C4BA892D54C7A3E068F93AB47EC50635556FC223F02CB1F520631E2F03E5509B6C1E24DFB7962BCD6DC74159BF0E5AFC03D9A00D";
        String keyplus = "10001";
        String mobile = "17626042019";
        String str = LoginSina.rsaCrypt(key, keyplus, mobile);
        System.out.println(str);
    }
}
