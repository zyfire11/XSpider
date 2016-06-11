package com.zy.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhouyi on 2016/4/19.
 */
public class PhantomJsUtil {

    private static String pjPath = "D:\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe";

    public static String executeJs(String jsPath) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        StringBuffer sbf = new StringBuffer();
        try {
            p = rt.exec(pjPath + " " + jsPath);
            //这里我的codes.js是保存在c盘下面的phantomjs目录
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String tmp = "";
            while((tmp = br.readLine())!=null){
                sbf.append(tmp);
            }
            System.out.println(sbf.toString());
            p.waitFor();
//	        p.destroy();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sbf.toString();
    }

    public static String getAjaxCotnent(String url) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        StringBuffer sbf = new StringBuffer();
        try {
			p = rt.exec("D:\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe D:\\phantomjs-2.0.0-windows\\test\\download.js "+url);
            p = rt.exec("D:\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe D:\\phantomjs-2.0.0-windows\\test\\eval.js");
            //这里我的codes.js是保存在c盘下面的phantomjs目录
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String tmp = "";
            while((tmp = br.readLine())!=null){
                sbf.append(tmp);
            }
            System.out.println(sbf.toString());
            p.waitFor();
//	        p.destroy();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sbf.toString();
    }

    public static String copyPicture(String url, String path) {
        Runtime rt = Runtime.getRuntime();
        Process p;
        StringBuffer sbf = new StringBuffer();
        try {
            p = rt.exec("D:\\phantomjs-2.0.0-windows\\bin\\phantomjs.exe D:\\phantomjs-2.0.0-windows\\test\\codes.js "+url + " " + path);

            p.waitFor();
//	        p.destroy();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sbf.toString();
    }

    public static String getPjPath() {
        return pjPath;
    }

    public static void setPjPath(String pjPath) {
        PhantomJsUtil.pjPath = pjPath;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String url = "http://www.court.gov.cn/zgcpwsw/List/List";
        getAjaxCotnent(url);

    }
}
