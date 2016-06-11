package com.zy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author zhouyi
 * 2014-12-1
 */
public class ChineseTimeUtil {

	/**
	 * @param args
	 */
	
	private static final Map<String, String> chineseMap = new HashMap<String, String>();  
    private static final String yearReg="[一|二|三|四|五|六|七|八|九|十|〇|○|零|０|0|O|Ｏ|Ο|О|o]{4}年";  
    private static final String monthReg="(([十][一|二])|([一|二|三|四|五|六|七|八|九|十|元]))月";  
    private static final String dayReg="(([十][一|二|三|四|五|六|七|八|九])|(一|二|三|四|五|六|七|八|九|十)|([一|二|三][十][一|二|三|四|五|六|七|八|九])|([二|三][十]))日";
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    static{  
        chineseMap.put("一", "1");  
        chineseMap.put("元", "1");  
        chineseMap.put("二", "2");  
        chineseMap.put("三", "3");  
        chineseMap.put("四", "4");  
        chineseMap.put("五", "5");  
        chineseMap.put("六", "6");  
        chineseMap.put("七", "7");  
        chineseMap.put("八", "8");  
        chineseMap.put("九", "9");  
        chineseMap.put("〇", "0");  
        chineseMap.put("○", "0");
        chineseMap.put("O", "0");
        chineseMap.put("Ο", "0");
        chineseMap.put("Ｏ", "0");
        chineseMap.put("О", "0");
        chineseMap.put("十", "10");  
        chineseMap.put("百", "100");
        chineseMap.put("零", "0");
        chineseMap.put("０", "0");
        chineseMap.put("0", "0");
        chineseMap.put("－", "1");
        chineseMap.put("o", "0");
    }  
    protected static String regMethod(Pattern pattern, String value) {  
        Matcher ma = pattern.matcher(value);  
        if (ma.find()) {  
            return ma.group();  
        }  
        return null;  
    }  
      
    private static int judgeChineseDate(String value){  
        int sumNum=0;  
        int unitValue=0;  
        for(int i=0;i<value.length()-1;i++){  
            char te=value.charAt(i);  
            int temp=Integer.parseInt(chineseMap.get(String.valueOf(te)));  
            switch (temp) {  
            case 100:  
                if(unitValue==0){  
                    unitValue=1;  
                }  
                sumNum+=unitValue*temp;  
                unitValue=0;  
                break;  
            case 10:  
                if(unitValue==0){  
                    unitValue=1;  
                }  
                sumNum+=unitValue*temp;  
                unitValue=0;  
                break;  
            default:  
                unitValue+=temp;  
                break;  
            }  
        }  
        sumNum+= unitValue;  
        return sumNum;  
    }  
    /**
     * 处理时间使其能格式化
     * @param value
     * @return
     */  
    public static String judgeTime(String value){  
        Pattern pattern=Pattern.compile(yearReg);  
        Pattern monthPattern=Pattern.compile(monthReg);  
        Pattern dayPattern=Pattern.compile(dayReg);  
        String iyear="";  
        String imonth="";  
        String iday="";  
        String year=regMethod(pattern, value);  
        if(year!=null){  
            for(char ch:year.toCharArray()){  
                String ivalue=chineseMap.get(String.valueOf(ch));  
                if(ivalue!=null){  
                    iyear+=ivalue;  
                }  
            }  
        }  
        String mon=regMethod(monthPattern,value);  
        if(mon!=null){  
            imonth=String.valueOf(judgeChineseDate(mon));  
        }else{
        	if(value.contains("×")){
        		mon = "一月";
        		imonth = "01";
        	}
        }
        String day=regMethod(dayPattern, value);  
        if(day!=null){  
            iday=String.valueOf(judgeChineseDate(day));  
        }else{
        	if(value.contains("×")){
        		day = "一日";
        		iday = "01";
        	}
        }
        if(year!=null&&mon!=null&&day!=null){  
            value=iyear+"-"+imonth+"-"+iday;
        }  
        return value;	
    }
        
    public static Date getDate(String value){
    	String dateStr = judgeTime(value);
    	System.out.println(dateStr);
    	Date date = new Date();
    	try {
			date = sf.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return date;
    }
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String chineseDate = "二o一一年十二月十六日";
		String date = ChineseTimeUtil.judgeTime(chineseDate);
		System.out.println(date);
		Date date2 = ChineseTimeUtil.getDate(chineseDate);
		System.out.println(ChineseTimeUtil.getDate(chineseDate));
	}

}
