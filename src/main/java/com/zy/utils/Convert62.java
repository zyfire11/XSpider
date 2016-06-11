package com.zy.utils;

/**
 * 微博id 62进制转码
 */
public class Convert62 {

	public static String convertTo10(String url){
		long temp=0l;
		for(int i=0;i<url.length();i++){
			long a=(long)Math.pow(62,url.length()-i-1);
			char t=url.charAt(i);
			temp+=a*str62keys(t+"");
		}
		String result=temp+"";
		if(url.length()==4&&result.length()<7){  //可能有的字段是以0开头的，要补齐
			for(int i=0;i<=7-result.length();i++){
				result="0"+result;
			}
		}
		return result;
	}
	
	public static String convertTo62(String id){		
		long temp=Long.parseLong(id);
		String result="";
		int site=0;
		while(temp>=62){
			site=(int)temp%62;
			result=keys62str(site)+result;
			temp-=site;
			temp/=62;
		}
		result=keys62str((int)temp)+result;
		if(id.length()==7&&result.length()<4){
			for(int i=0;i<=4-result.length();i++){
				result="0"+result;
			}
		}
//		System.out.println("返回的结果："+result);
		return result;
	}
	
	public static String keys62str(int site){   //已知大小，求62进制中的字符
		String[] keys ={ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
		         "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
		         "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
		         "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
		         "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
		         "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
		         "Y", "Z" };
		String res=keys[site];
		return res;
	}
	public static int str62keys(String ks)//62进制字典,已知字符，求大小
	{
	    String[] keys ={ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", 
	    		         "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
	    		         "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
	    		         "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
	    		         "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
	    		         "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
	    		         "Y", "Z" };
	    int i = 0;
	    for(String k:keys)
	    {
	        if (ks.equals(k)) { 
//	        	System.out.println(ks+"'s index is: "+i);
	        	return i; 
	        	}
	        i++;
	    }
	    return 0;
	}
	/**
	 * 获取新浪微博url（62位加密，剩余/7位/7位;解码方式是：剩余,4,4位）
	 * @param userid
	 * @param weiboid
	 * @return
	 */
	public static String convertToUrl(long userid,long weiboid){
		String result="http://weibo.com/";
		result=result+userid+"/";
		String id=weiboid+"";
		int length=id.length();
		String first=id.substring(length-7, length);
		String second=id.substring(length-14, length-7);
		String third=id.substring(0, length-14);
		String res1=convertTo62(first);
		String res2=convertTo62(second);
		String res3=convertTo62(third);
		result=result+res3+res2+res1;
		return result;
	}
	/**
	 * 根据62进制码得到微博ID
	 * @param args
	 */
	public static String getWeiboId(String code){
		String first=code.substring(0, code.length()-8);
		String second=code.substring(code.length()-8, code.length()-4);
		String third=code.substring(code.length()-4, code.length());
		String result=convertTo10(first)+convertTo10(second)+convertTo10(third);
		return result;
	}
	public static void main(String args[]){
//		long id=348884342214423960l;  //微博ID
//		String result=convertToUrl(1748950984l,id);  //1748950984是用户ID ，结果是微博URL
		//下面是获取微博ID
		String url="zfL0thIAr";
		String first=url.substring(0, url.length()-8);
		String second=url.substring(url.length()-8, url.length()-4);
		String third=url.substring(url.length()-4, url.length());
		String result=convertTo10(first)+convertTo10(second)+convertTo10(third);
		System.out.println(first+" "+convertTo10(first));
		System.out.println(second+" "+convertTo10(second));
		System.out.println(third+" "+convertTo10(third));
		System.out.println(result);
	}
}
