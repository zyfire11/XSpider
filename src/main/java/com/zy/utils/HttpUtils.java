package com.zy.utils;

import java.io.*;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.zy.config.ProxyConfig;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;

/**
 * @author zc
 * http操作相关的类
 */
public class HttpUtils {
	/*
	 * params :
	 * url:  地址
	 * headers：请求头部信息
	 * return : httpresponse响应
	 */
	public static HttpResponse doGet(String url,Map<String,String> headers){
		HttpClient client=createHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}	
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;		
	}

	public static HttpResponse doGetByProxy(String url,Map<String,String> headers){
		return  doGetByProxy(url, headers, true);
	}

	public static HttpResponse doGetByProxy(String url,Map<String,String> headers, boolean isSwitch){
		HttpClient client=getAbuyunHttpClient(isSwitch);
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;
	}

	public static HttpResponse doGetTest(String url,Map<String,String> headers){
		HttpClient client=createHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpHost proxy = new HttpHost("115.228.60.173", 3128);
		client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}	
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;		
	}

	public static HttpResponse doGetNoDirectByProxy(String url,Map<String,String> headers, boolean isSwitch){
		HttpClient client=getAbuyunHttpClient(isSwitch);
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;
	}
	
	public static HttpResponse doGetNoDirect(String url,Map<String,String> headers){
		HttpClient client=createHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
//		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}	
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;		
	}
	
	public static HttpResponse doGet302(String url,Map<String,String> headers){
		HttpClient client=createHttpClient();
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}
			HttpParams httpParams = client.getParams();
			httpParams.setParameter(ClientPNames.HANDLE_REDIRECTS, true);
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;		
	}
	
	//需ssl证书认证的方法
	public static HttpResponse doGetForSSL(String url,Map<String,String> headers){
		HttpClient client=createHttpClient();
		client = wrapClient(client);
		HttpGet getMethod=new HttpGet(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					getMethod.addHeader(key, headers.get(key));
				}
			}	
			response=client.execute(getMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			String msg=e.getMessage();
			if(msg.contains("Truncated chunk")){
				System.out.println(e.getMessage() +" 数据获取不完整。需要重新获取。");
			}else{
				System.out.println(e.getMessage() +" 连接 被拒绝。需要降低爬取频率。");
			}
		} catch(Exception e){
		}
		return response;		
	}
	
	//以下是wrapClient方法
	/**
	* 获取可信任https链接，以避免不受信任证书出现peer not authenticated异常
	*
	* @param base
	* @return
	*/
	public static DefaultHttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) {
				}
				public void checkServerTrusted(X509Certificate[] xcs, String string) {
				}
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static HttpResponse doPostNoDirect(String url,Map<String,String> headers,Map<String,String> params){
		HttpClient client=createHttpClient();
		HttpPost postMethod=new HttpPost(url);
		client.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}	
			List<NameValuePair> p=null;
			if(params!=null && params.keySet().size()>0){
				p=new ArrayList<NameValuePair>();
				for(String key:params.keySet()){
					p.add(new BasicNameValuePair(key,params.get(key)));
				}
			}
			if(p!=null)
				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}

	public static HttpResponse doPostByProxy(String url,Map<String,String> headers,Map<String,String> params){
		return doPostByProxy(url, headers, params, true);
	}

	//使用阿布云代理
	public static HttpResponse doPostByProxy(String url,Map<String,String> headers,Map<String,String> params, boolean isSwitch){
		HttpClient client = getAbuyunHttpClient(isSwitch);
		AuthCache authCache = new BasicAuthCache();
		HttpHost target = new HttpHost(ProxyConfig.proxyHost, ProxyConfig.proxyPort, "http");
		// Generate BASIC scheme object and add it to the local
		// auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(target, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		HttpPost postMethod=new HttpPost(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}
			List<NameValuePair> p=null;
			if(params!=null && params.keySet().size()>0){
				p=new ArrayList<NameValuePair>();
				for(String key:params.keySet()){
					p.add(new BasicNameValuePair(key,params.get(key)));
				}
			}
			if(p!=null)
				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
//				postMethod.setEntity(new UrlEncodedFormEntity(p,"gb2312"));
			response=client.execute(target, postMethod, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}


	/*
	 * params :
	 * url:  地址
	 * headers：请求头部信息
	 * params：post的请求数据
	 * return : httpresponse响应
	 */
	
	public static HttpResponse doPost(String url,Map<String,String> headers,Map<String,String> params){
		HttpClient client=createHttpClient();
		HttpPost postMethod=new HttpPost(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}	
			List<NameValuePair> p=null;
			if(params!=null && params.keySet().size()>0){
				p=new ArrayList<NameValuePair>();
				for(String key:params.keySet()){
					p.add(new BasicNameValuePair(key,params.get(key)));
				}
			}
			if(p!=null)
				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}
	//上传附件
	public static HttpResponse doPostWithImg(String url,Map<String,String> headers,Map<String,Object> params){
		HttpClient client=createHttpClient();
		HttpPost postMethod=new HttpPost(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}	
			List<NameValuePair> p=null;
//			Part[] parts = new Part[10];
			MultipartEntity entity = new MultipartEntity();
//			InputStream in = new ByteArrayInputStream(new byte[2]);
//			postMethod.setRequestBody(in);
			if(params!=null && params.keySet().size()>0){
				for(String key:params.keySet()){
					if(key.equals("IMG")){
						FileBody fileBody = new FileBody(new File(params.get(key).toString()));
						entity.addPart("IMG", fileBody);
					}else{
						StringBody stringBody = new StringBody(params.get(key).toString());
						entity.addPart(key, stringBody);
					}
				}
			}
//			if(p!=null)
//				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
				postMethod.setEntity(entity);
			
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}
	
	public static HttpResponse doPost2(String url,Map<String,String> headers,Map<String,Object> params){
		HttpClient client=createHttpClient();
		HttpPost postMethod=new HttpPost(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}	
			List<NameValuePair> p=null;
			if(params!=null && params.keySet().size()>0){
				p=new ArrayList<NameValuePair>();
				for(String key:params.keySet()){
					p.add(new BasicNameValuePair(key,params.get(key).toString()));
				}
			}
			if(p!=null)
				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}
	
	public static HttpResponse doPost(String url,Map<String,String> headers,Map<String,String> params, String charset){
		HttpClient client=createHttpClient();
		HttpPost postMethod=new HttpPost(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}	
			List<NameValuePair> p=null;
			if(params!=null && params.keySet().size()>0){
				p=new ArrayList<NameValuePair>();
				for(String key:params.keySet()){
					p.add(new BasicNameValuePair(key,params.get(key)));
				}
			}
			if(p!=null)
//				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
				postMethod.setEntity(new UrlEncodedFormEntity(p,charset));
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}
	
	public static HttpResponse doPostForSSL(String url,Map<String,String> headers,Map<String,String> params){
		HttpClient client=createHttpClient();
		client = wrapClient(client);
		HttpPost postMethod=new HttpPost(url);
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
				}
			}	
			List<NameValuePair> p=null;
			if(params!=null && params.keySet().size()>0){
				p=new ArrayList<NameValuePair>();
				for(String key:params.keySet()){
					p.add(new BasicNameValuePair(key,params.get(key)));
				}
			}
//			postMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
			if(p!=null)
				postMethod.setEntity(new UrlEncodedFormEntity(p,HTTP.UTF_8));
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}
	//上传一个文件
	public static HttpResponse doPost(String url,Map<String,String> headers,String fileName){
		HttpClient client=createHttpClient();
		HttpPost postMethod=new HttpPost(url);
		String boundary = "";
		HttpResponse response=null;
		try {
			if(headers!=null && headers.keySet().size()>0){
				for(String key:headers.keySet()){
					postMethod.addHeader(key, headers.get(key));
					if(key.equals("Content-Type")){
						String tmp=headers.get(key);
						boundary=tmp.substring(tmp.indexOf("=")+1);
					}
				}
			}	
			File file=new File(fileName);
			InputStream in=new FileInputStream(file);
			
			StringBuffer buffer=new StringBuffer();
			buffer.append(boundary).append("\n")
				  .append("Content-Disposition: form-data; name=\"pic1\"; filename=\""+file.getName()).append("\"\n")
				  .append("Content-Type: image/pjpeg").append("\n")
				  .append("\n");
			
			System.out.println(buffer.toString());
			
//			String tmpstr=Utils.getStringFromStream(in);
			String tmpstr = getStringFromStream(in, "utf-8");
			tmpstr=Base64Encoder.encode(tmpstr.getBytes());
			buffer.append(tmpstr).append("\n");
			buffer.append(boundary+"--").append("\n");
			
			System.out.println(buffer.toString());
			
			in=new ByteArrayInputStream(buffer.toString().getBytes());
			
			InputStreamEntity ise=new InputStreamEntity(in,buffer.toString().getBytes().length);  
			
		    postMethod.setEntity(ise);  
		    
			response=client.execute(postMethod);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;			
	}

	public static String getStringFromStream(InputStream in, String format) {
		BufferedReader reader=null;
		try {
			reader = new BufferedReader(new InputStreamReader(in,format));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		StringBuffer buffer=new StringBuffer();
		String str=null;
		try{
			while((str=reader.readLine())!=null){
				buffer.append(str+"\n");
			}
			reader.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "error:"+e.getMessage();
		}
	}
	/*
	 * params :
	 * httpresponse
	 * return : 响应的头部信息
	 */
	
	public static List<Header> getReponseHeaders(HttpResponse response){
		List<Header> headers=null;
		Header[] hds=response.getAllHeaders();
		if(hds!=null && hds.length>0){
			headers=new ArrayList<Header>();
			for(int i=0;i<hds.length;i++){
				headers.add(hds[i]);
			}
		}		
		return headers;
	}
	
	/*
	  * params :
	  * headers:头部信息 
	  * request：请求
	 */
	public static void setHeaders(Map<String,String> headers,HttpUriRequest request){
		if(headers!=null && headers.keySet().size()>0){
			for(String key:headers.keySet()){
				request.addHeader(key, headers.get(key));			}
		}
	}
	
	/*
	 * params :
	 * httpresponse
	 * return : 响应的cookies值
	 */
	
	public static List<Cookie> getResponseCookies(HttpResponse response){
		List<Cookie> cookies=null;
		Header[] hds=response.getAllHeaders();
		if(hds!=null && hds.length>0){
			for(int i=0;i<hds.length;i++){
				if(hds[i].getName().equalsIgnoreCase("Set-Cookie")){
					if(cookies==null){
						cookies=new ArrayList<Cookie>();
					}					 
					String cookiestring[]=hds[i].getValue().split(";");
					String ss[]=cookiestring[0].split("=",2);
					String cookiename=ss[0];
					String cookievalue=ss[1];
					Cookie cookie=new BasicClientCookie(cookiename,cookievalue);
					cookies.add(cookie);
				}
			}
		}		
		return cookies;
	}
	/*
	 * params :
	 * cookies数组
	 * return : cookies数组组成的字符串
	 */
	public static String setCookie2String(List<Cookie> cookies){
		StringBuilder builder=null; 
		if(cookies!=null && cookies.size()>0){
			builder=new StringBuilder();
			for(int j=0;j<cookies.size();j++){
				Cookie c=cookies.get(j);
				builder.append(c.getName()+"="+c.getValue());
				if(j!=cookies.size()-1)
					builder.append("; ");
			 }
			return builder.toString();
		}		
		return null;
	}
	
	/*
	 * 从响应中得到输入流
	 */
	public static InputStream getInputStreamFromResponse(HttpResponse response){
		if(response==null){
			return null;
		}
		HttpEntity entity=response.getEntity();
		InputStream in=null;
		try {
			in = entity.getContent();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  in;
	}
	
	/*
	 * 从响应中得到字符串
	 */
	public static String getStringFromResponse(HttpResponse response){
		if(response==null){
			return null;
		}
		InputStream in=getInputStreamFromResponse(response);
		String responseText="";
		if(in!=null){
			responseText=getStringFromStream(in, "utf-8");
		}
		return responseText;
	}
	
	public static String getStringFromResponseByCharset(HttpResponse response, String format){
		if(response==null){
			return null;
		}
		InputStream in=getInputStreamFromResponse(response);
		String responseText="";
		if(in!=null){
			responseText=getStringFromStream(in, format);
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return responseText;
	}
	
	/**
	 * 创建支持多线程并发连接的HTTPCLIENT
	 */
	private final static HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");

		ThreadSafeClientConnManager clientmanager = new ThreadSafeClientConnManager();
		clientmanager.setMaxTotal(20);
		HttpClient client = new DefaultHttpClient(clientmanager, params);
		return client;
	}
	
	/**
	 * 加入代理的功能
	 * @return HttpClient 对象
	 */
	public static HttpClient getDefaultHttpClientByProxy() {
		HttpClient httpclient =createHttpClient();
		String filePath = "proxy.properties";
		HttpHost proxy = null;
		Map<String, String> map = ReadIni.getDbini(filePath);
		if (map.size() == 0) {
			throw new RuntimeException("无可用代理");
		} else {
			Set<String> set = map.keySet();
			String[] array = (String[]) set.toArray(new String[set.size()]);
			Random r = new Random();
			int rnum = r.nextInt(array.length);
			String ip = array[rnum];
			String port = map.get(ip);
			proxy = new HttpHost(ip, Integer.parseInt(port));
		}
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		return httpclient;
	}

	//4.1
	private static HttpClient getAbuyunHttpClient(boolean isSwitch){
//		HttpClient httpClient = createHttpClient();
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		ThreadSafeClientConnManager clientmanager = new ThreadSafeClientConnManager();
		clientmanager.setMaxTotal(20);
		DefaultHttpClient client = new DefaultHttpClient(clientmanager, params);
		client = wrapClient(client);

		BasicHeader header = new BasicHeader("Proxy-Switch-Ip", "yes");
		List<Header> list = new ArrayList<Header>();
		list.add(header);
		if(isSwitch) {
//			client.getParams().setParameter("http.default-headers", list);
		}


		HttpHost target = new HttpHost(ProxyConfig.proxyHost, ProxyConfig.proxyPort, "http");

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				new AuthScope(target.getHostName(), target.getPort()),
				new UsernamePasswordCredentials(ProxyConfig.proxyUser, ProxyConfig.proxyPassword));


		client.setCredentialsProvider(credsProvider);
//		client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, target);


//		CloseableHttpClient httpClient = null;
//		if(isSwitch) {
//			httpClient = HttpClients.custom()
//					.setDefaultCredentialsProvider(credsProvider)
//					.setDefaultHeaders(list).build();
//		}else{
//			httpClient = HttpClients.custom()
//					.setDefaultCredentialsProvider(credsProvider).build();
//		}
		return client;

	}

	//测试代理ip
	public static String getIp(){
		String targetUrl = "https://test.abuyun.com/proxy.php";
		Map<String, String> headers = new HashMap<>();
		headers.put("Proxy-Switch-Ip", "yes");
		HttpResponse response = doGetByProxy(targetUrl, headers, true);
		String html = HttpUtils.getStringFromResponseByCharset(response, "gbk");
//		System.out.println(html);
		String ipAddress = RegexUtil.getMatchGroupRegex(html, "<th>client-ip</th><td>(.*?)</td>");

		return ipAddress;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 10; i ++){
			System.out.println(getIp());
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
