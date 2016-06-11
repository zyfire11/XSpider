package com.zy.logic.component.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;




public class SimpleLoader {

	/**
	 * @param args
	 * @throws java.io.IOException
	 */
	public static void main(String[] args) throws IOException 
	{
		//SimpleLoader.download("http://proxy.linktool.org/","utf-8");
		String url = "http://www.court.gov.cn/zgcpwsw/List/List";
		System.out.println(url);
		String html = SimpleLoader.download(url,"utf-8");
//		String html = SimpleLoader.readHtml(url, "utf-8");
		System.out.println(html);
//		html = html.replaceAll("\\t", "").replaceAll("\\n+", "\n").replaceAll(" ", "").replaceAll("　", "");
//		if(html.contains("签发：") && html.contains("抄送：")){
//			html = html.split("抄送：")[1];
//			if(html.startsWith("\n")){
//				html = html.replaceFirst("\\n", "");
//			}
//		}
//		if(html.contains("PAGE")){
//			html = html.split("PAGE")[0].replaceAll("�", "");
//			html = html.replaceAll("\\n+", "\n");
//			if(html.endsWith("\n")){
//				html = html.substring(0, html.length()-1);
//			}
//		}
//		System.out.println(html);
//		while(html.contains("reload")){
//			html = SimpleLoader.download(url,"gbk");
//		}
		
	}
	
	/**
	 * 访问网络资源
	 * */
	
	public static String download(String inputUrl,String charset){
		StringBuffer html= new StringBuffer();;
		try { 
			URL url=new URL(inputUrl); 
			//URLConnection uc=url.openConnection(); 
			BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream(),charset)); 
			String s=null;
			while((s=br.readLine())!=null){ 
				html.append(s + "\n");
			} 
			br.close(); 
			}  catch (Exception e) {
				e.printStackTrace(); 
			} 
//		 System.out.println(html);
		 return html.toString();
		
	}

	public static String downloadNew2(String inputUrl,String charset) throws IOException{
		StringBuffer html= new StringBuffer();;
		try {
			URL url=new URL(inputUrl);
			//URLConnection uc=url.openConnection();
			BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream(),charset));
			String s=null;
			while((s=br.readLine())!=null){
				html.append(s + "\n");
			}
			br.close();
		}  catch (MalformedURLException e) {
			e.printStackTrace();
		}
//		 System.out.println(html);
		return html.toString();

	}
	
	/**
	 * 读取本地磁盘html文件
	 * */
	public static String readHtml(String path,String charset){
		
		String html="";
		try{
			File file=new File(path);
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			String tempStr=null;
			while((tempStr=br.readLine())!=null){
				html+=tempStr+"\n";
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		System.out.println(html);
		return html;
	}
	
	/**
	 * 读取本地磁盘xml文件
	 * */
	public static String readXml(String path,String charset) throws IOException{
		StringBuffer html=new StringBuffer();
		File file=new File(path);
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		String tempStr=null;
		while((tempStr=br.readLine())!=null){
			html.append(tempStr);
			html.append("\n");
//			html+=tempStr+"\n";
		}
//		System.out.println(html);
		return html.toString();
	}
	
//	public static String doGet(String url){
//	}

}
