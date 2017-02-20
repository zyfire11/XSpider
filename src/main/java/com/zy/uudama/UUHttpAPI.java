package com.zy.uudama;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.zy.utils.HttpUtils;
import org.apache.http.HttpResponse;



import wuhao.tools.reader.PathUtil;




public class UUHttpAPI {
	private static String 	USERNAME = "xoknight";
	private static String 	PASSWORD = "";
	private static String   MAC;
	private static int 		userId = 100;
	private static String   userKey;
	private static String   IMGPATH;
	public static int		SOFTID		= 94833;									//软件ID 获取方式：http://dll.uuwise.com/index.php?n=ApiDoc.GetSoftIDandKEY
	public static String	SOFTKEY		= "576fab9470ab4985ad265be03b0e6ce2";	//软件KEY 获取方式：http://dll.uuwise.com/index.php?n=ApiDoc.GetSoftIDandKEY
	public static String	DLLVerifyKey="32F1C86B-E64C-4EAF-8BC1-C142570008BC";	//校验API文件是否被篡改，实际上此值不参与传输，关系软件安全，高手请实现复杂的方法来隐藏此值，防止反编译,获取方式也是在后台获取软件ID和KEY一个地方
	public static Map<String, String>  headers = new HashMap<String, String>();
	
	static {
		
//		MAC = getLocalMac();
		//处理养马甲的电脑获取不到mac
		MAC = "94-DE-80-58-1D-E2";
		headers.put("SID", SOFTID+"");                                            
		headers.put("HASH",md5(SOFTID+SOFTKEY.toUpperCase()));					//32位MD5加密小写
		headers.put("UUVersion","1.0.0.1");                    
		headers.put("UID",userId+"");											//没有登录之前，UserID就用100。登录成功后，服务器会返回UserID，之后的请求就用服务器返回的UserID		      
		headers.put("User-Agent", md5(SOFTKEY.toUpperCase() + userId));			//没有登录之前，UserID就用100。登录成功后，服务器会返回UserID，之后的请求就用服务器返回的UserID
	}
	//1.获取(刷新)服务器列表
	public void refreshService(){
		HttpResponse response = HttpUtils.doGet("http://common.taskok.com:9000/Service/ServerConfig.aspx", headers);
		String returnStr = HttpUtils.getStringFromResponse(response);
		if(returnStr.endsWith("\n")){
			returnStr = returnStr.substring(0, returnStr.length()-1);
		}
		System.out.println(returnStr);
	}
	
//	2.登录(http://www.uuwise.com/allErrorCode.html)
	public int login(){
		int userId = -1;
		String url = "http://login.uudama.com/Upload/UULogin.aspx?U="+USERNAME+"&p=" + md5(PASSWORD);
		headers.put("KEY",md5(SOFTKEY.toUpperCase()+USERNAME.toUpperCase())+MAC);    				//MAC把特殊符号去掉，纯粹字母数字
		headers.put("UUKEY", md5(USERNAME.toUpperCase() + MAC + SOFTKEY.toUpperCase()));		 	//MAC把特殊符号去掉，纯粹字母数字
		
		HttpResponse response = HttpUtils.doGet(url, headers);
		String resStr = HttpUtils.getStringFromResponse(response);
		if(resStr.endsWith("\n")){
			resStr = resStr.substring(0, resStr.length()-1);
		}
		userId = Integer.parseInt(resStr.split("_")[0]);
//		System.out.println("userId：" + userId + ", userkey:" + resStr );
		this.userId = userId;
		this.userKey = resStr;
		headers.remove("KEY");
		headers.remove("UUKEY");
		return userId;
	}
	//3.查分
	public int getScore(){
		
		
		String url = "http://login.uudama.com/Upload/GetPoint.aspx?U="+USERNAME+"&p=" + md5(PASSWORD);
		String uuAgent = md5(userKey.toUpperCase() + userId + SOFTKEY);
		headers.put("UUAgent", uuAgent);
		headers.put("KEY", userKey);
		HttpResponse response = HttpUtils.doGet(url, headers);
		String resStr = HttpUtils.getStringFromResponse(response);
		if(resStr.endsWith("\n")){
			resStr = resStr.substring(0, resStr.length()-1);
		}
//		System.out.println("当前积分：" +resStr);
		headers.remove("UUAgent");
		headers.remove("KEY");
		
		return Integer.parseInt(resStr);
		
	}
	
	//4，上传图片和获取结果
	/*
	 * codeType :http://uuwise.com/charges.bak.htm
	 */
	public String getImageValue(String codeType){
		int userId = login();
		if (userId > 0) {
			//重置那些userid设为100的headers
			headers.put("UID",userId+"");		      
			headers.put("User-Agent", md5(SOFTKEY.toUpperCase() + userId));	
			
			int score = getScore();
			System.out.println("userID is:" + userId);
			System.out.println("userScore is:"
					+ score);
		
			refreshService();
			String url = "http://upload.uuwise.com/Upload/Processing.aspx";
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("KEY", userKey.toUpperCase());
			params.put("SID", SOFTID+"");
			params.put("SKEY", md5(this.userKey.toLowerCase() + SOFTID+ SOFTKEY));
			params.put("Version", "100");
			params.put("TimeOut", "20000");
			params.put("Type", codeType); //验证码的CodeType，详见：http://www.uuwise.com/price.html
			
//			File f = new File(IMGPATH);
//			byte[] by = null;
//			try {
//				by = toByteArray(f);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			params.put("IMG", by);
			params.put("IMG", IMGPATH);
			params.put("GUID", GetFileMD5(IMGPATH)); //图片文件的Md5()
			
//			headers.put("Content-Type", "multipart/form-data; boundary=-------------aabbccddeeff007dc3d73a70130");
			
//			HttpResponse response = HttpUtils.doPost2(url, headers, params);
			HttpResponse response = HttpUtils.doPostWithImg(url, headers, params);
			String resStr = HttpUtils.getStringFromResponse(response);
			if(resStr.endsWith("\n")){
				resStr = resStr.substring(0, resStr.length()-1);
			}
			System.out.println("第一次识别返回信息：" +resStr);
			String[] arr = resStr.split("\\|");
			if(arr.length>1){
				return arr[1];
			}else{
				String codeId = arr[0];
				String result = getResult(codeId);
				int num = 0;
				for(int i = 0; i< 20; i++){
					if(result.equals("-3")){
						num ++;
						result = getResult(codeId);
					}else{
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//判断是否正确
				if(result.equals("-3")){
					System.out.println("20次等待还是没获取到验证识别码");
					reportError();
				}else {
					System.out.println("重新获取"+num+"次后得到识别结果：" + result);
				}
				return result;
//				String errorStr = reportError();
//				errorStr = reportError();
//				System.out.println(errorStr);
//				return errorStr;
				
			}
		}else{
			System.out.println("登录失败，错误代码为：" + userId); //错误代码请对应dll.uuwise.com各函数值查看
			return null;
		}
		
	}
//	5. 获取识别结果(正确返回验证码，错误返回错误代码（）)
	public String getResult(String codeId){
		String url = "http://upload.uuwise.com/Upload/GetResult.aspx?key="+userKey+"&ID="+codeId;
		HttpResponse response = HttpUtils.doGet(url, headers);
		String resStr = HttpUtils.getStringFromResponse(response);
		if(resStr.endsWith("\n")){
			resStr = resStr.substring(0, resStr.length()-1);
		}
		return resStr;
	}
	//6.报告错误代码，返还积分
	public String reportError(){
		String result = "";
		String url = "http://upload.uuwise.com/Upload/ReportError.aspx?KEY="+userKey+"&ID="+SOFTID+"&SID="+SOFTKEY+"&SKEY="+md5(this.userKey.toLowerCase() + SOFTID+ SOFTKEY);
		HttpResponse response = HttpUtils.doGet(url, headers);
		String resStr = HttpUtils.getStringFromResponse(response);
		if(resStr.endsWith("\n")){
			resStr = resStr.substring(0, resStr.length()-1);
		}
		if(resStr.equals("0")){
			System.out.println("上传打码失败错误，归回积分成功，返回码为 ： 0");
			
		}else {
			System.out.println("上传打码失败错误，归回积分失败，返回码为：" + resStr);
		}
		return result;  //返回错误代码
	}
	
	private static String getLocalMac()  {
		// TODO Auto-generated method stub
		
		try {
			InetAddress ia = InetAddress.getLocalHost();
		
			//获取网卡，获取地址
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
//			System.out.println("mac数组长度："+mac.length);
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				if(i!=0) {
					sb.append("-");
				}
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
//				System.out.println("每8位:"+str);
				if(str.length()==1) {
					sb.append("0"+str);
				}else {
					sb.append(str);
				}
			}
			System.out.println("本机MAC地址:"+sb.toString().toUpperCase());
			return sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String md5(String s){
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			return byteArrayToHex(md);
		}catch (Exception e) {
            e.printStackTrace();
            return null;
        }
		
	}
	
	public static String byteArrayToHex(byte[] byteArray) {
		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'a','b','c','d','e','f' };
		char[] resultCharArray =new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b& 0xf];
		}
		return new String(resultCharArray);
	}
	
	public static byte[] toByteArray(File imageFile) throws Exception
	{
		BufferedImage img = ImageIO.read(imageFile);
		ByteArrayOutputStream buf = new ByteArrayOutputStream((int) imageFile.length());
		try
		{
			ImageIO.write(img, "jpg", buf);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return buf.toByteArray();
	}
	
	//MD5校验函数开始
    /**
     * 获取指定文件的MD5值
     * 
     * @param inputFile
     *            文件的相对路径
     */
	public static String GetFileMD5(String inputFile) {
		int bufferSize = 256 * 1024;
		FileInputStream fileInputStream = null;
		DigestInputStream digestInputStream = null;
		try {
			MessageDigest messageDigest =MessageDigest.getInstance("MD5");
			fileInputStream = new FileInputStream(inputFile);
			digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
			byte[] buffer =new byte[bufferSize];
			while (digestInputStream.read(buffer) > 0);
			messageDigest= digestInputStream.getMessageDigest();
			byte[] resultByteArray = messageDigest.digest();
			return byteArrayToHex(resultByteArray);
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally {
			try {
				digestInputStream.close();
			}catch (Exception e) {
				
			}try {
				fileInputStream.close();
			}catch (Exception e) {
				
			}
		}
	}
	
	
	
	public static String getUSERNAME() {
		return USERNAME;
	}

	public static void setUSERNAME(String uSERNAME) {
		USERNAME = uSERNAME;
	}

	public static String getPASSWORD() {
		return PASSWORD;
	}

	public static void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public static String getMAC() {
		return MAC;
	}

	public static void setMAC(String mAC) {
		MAC = mAC;
	}

	public static int getUserId() {
		return userId;
	}

	public static void setUserId(int userId) {
		UUHttpAPI.userId = userId;
	}

	public static String getUserKey() {
		return userKey;
	}

	public static void setUserKey(String userKey) {
		UUHttpAPI.userKey = userKey;
	}

	public static String getIMGPATH() {
		return IMGPATH;
	}

	public static void setIMGPATH(String iMGPATH) {
		IMGPATH = iMGPATH;
	}

	public static int getSOFTID() {
		return SOFTID;
	}

	public static void setSOFTID(int sOFTID) {
		SOFTID = sOFTID;
	}

	public static String getSOFTKEY() {
		return SOFTKEY;
	}

	public static void setSOFTKEY(String sOFTKEY) {
		SOFTKEY = sOFTKEY;
	}

	public static String getDLLVerifyKey() {
		return DLLVerifyKey;
	}

	public static void setDLLVerifyKey(String dLLVerifyKey) {
		DLLVerifyKey = dLLVerifyKey;
	}

	public static Map<String, String> getHeaders() {
		return headers;
	}

	public static void setHeaders(Map<String, String> headers) {
		UUHttpAPI.headers = headers;
	}

	public static void main(String[] args){
		UUHttpAPI api = new UUHttpAPI();
		String imagePath = PathUtil.getProjectRoot() + "/image/validate_code/76c9bd3b20f44fb88b8e3f5a698b05b1.jpeg";
		System.out.println(imagePath);
		api.setIMGPATH(imagePath);
		api.getImageValue("1005");
//		String userkey = "206748_XOKNIGHT_27-9C-1B-90-ED-05-2C-48-D9-0B-FE-A8-BD-E9-C4-1D_C6-B8-98-C6-3C-F7-B3-75-B2-56-EA-65-84-7E-8D-91-5E-6A-B7-A1";
//		int userId = 206748;
//		String softkey = "576fab9470ab4985ad265be03b0e6ce2";
//		String string  = md5(userkey.toUpperCase() + userId + softkey);
//		System.out.println(string);
		
	}

}
