package com.zy.utils;


import java.util.HashMap;
import java.util.Map;

/**
 * a class to deal with Chinese numeric. <br />
 * 
 * @author chenxin
 * {@link http://www.webssky.com}
 */
public class CNNMFilter {
	/**
	 * chinese numeric chars. <br />
	 * i have put the chars into the lexicon file lex-cn-numeric.lex for the old version. <r />
	 * it's better to follow the current work.
	 */
	private static final Character[] CN_NUMERIC = {
			'一', '二', '三', '四', '五','六', '七', '八', '九',
			'壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌','玖',
			'十', '百', '千', '拾', '佰', '仟', '万', '亿', '○', 'Ｏ', '零'};
	
	private static Map<Character, Integer> cnNumeric = null;

	static String[] units = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿",
			"十亿", "百亿", "千亿", "万亿" };
	static char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };
	
	static {
		cnNumeric = new HashMap<Character, Integer>(40, 0.85f);
		for ( int j = 0; j < 9; j++ ) 
			cnNumeric.put(CN_NUMERIC[j], j + 1);
		for ( int j = 9; j < 18; j++ )
			cnNumeric.put(CN_NUMERIC[j], j - 8);
		cnNumeric.put('两', 2);
		cnNumeric.put('十', 10);
		cnNumeric.put('拾', 10);
		cnNumeric.put('百', 100);
		cnNumeric.put('佰', 100);
		cnNumeric.put('千', 1000);
		cnNumeric.put('仟', 1000);
		cnNumeric.put('万', 10000);
		cnNumeric.put('亿', 100000000);
	}


        	/**
	 * check the given char is chinese numeric or not. <br />
	 * 
	 * @param c <br />
	 * @return boolean true yes and false for not.
	 */
	public static int isCNNumeric( char c ) {
		Integer i = cnNumeric.get(c);
		if ( i == null ) return -1;
		return i.intValue();
	}
	
	
	/**
	 * a static method to turn the Chinese numeric to Arabic numbers.
	 * 
	 * @param cnn
	 * @param boolea flag
	 * @return int
	 */
	public static int cnNumericToArabic( String cnn, boolean flag ) {

		cnn = cnn.trim();
		if ( cnn.length() == 1 )
			return isCNNumeric(cnn.charAt(0));
		
		if ( flag ) cnn = cnn.replace("佰", "百").replace("仠", "千")
				.replace("仟", "千").replace("拾", "十").replace("零", " ");
		//System.out.println(cnn);
		int yi = -1, wan = -1, qian = -1, bai = -1, shi = -1;
		int val = 0;
		yi = cnn.lastIndexOf('亿');
		if ( yi > -1 ) {
			val += cnNumericToArabic( cnn.substring(0, yi), false ) * 100000000;
			if ( yi < cnn.length() - 1 )
				cnn = cnn.substring(yi + 1, cnn.length());
			else 
				cnn = "";
			
			if ( cnn.length() == 1 ) {
				int arbic = isCNNumeric(cnn.charAt(0));
				if ( arbic <= 10 )
					val += arbic * 10000000;
				cnn = "";
			}
		}
		
		wan = cnn.lastIndexOf('万');
		if ( wan > -1 ) {
			val += cnNumericToArabic( cnn.substring(0, wan), false ) * 10000;
			if ( wan < cnn.length() - 1 )
				cnn = cnn.substring(wan + 1, cnn.length());
			else 
				cnn = "";
			if ( cnn.length() == 1 ) {
				int arbic = isCNNumeric(cnn.charAt(0));
				if ( arbic <= 10 )
					val += arbic * 1000;
				cnn = "";
			}
		}
		
		qian = cnn.lastIndexOf('千'); 
		if ( qian > -1 ) {
			val +=  cnNumericToArabic( cnn.substring(0, qian), false ) * 1000;
			if ( qian < cnn.length() - 1 )
				cnn = cnn.substring(qian + 1, cnn.length());
			else 
				cnn = "";
			if ( cnn.length() == 1 ) {
				int arbic = isCNNumeric(cnn.charAt(0));
				if ( arbic <= 10 )
					val += arbic * 100;
				cnn = "";
			}
		}
		
		bai = cnn.lastIndexOf('百');
		if ( bai > -1 ) {
			val += cnNumericToArabic( cnn.substring(0, bai), false ) * 100;
			if ( bai < cnn.length() - 1 )
				cnn = cnn.substring(bai + 1, cnn.length());
			else 
				cnn = "";
			if ( cnn.length() == 1 ) {
				int arbic = isCNNumeric(cnn.charAt(0));
				if ( arbic <= 10 )
					val += arbic * 10;
				cnn = "";
			}
		}
		
		shi = cnn.lastIndexOf('十');
		if ( shi > -1 ) {
			if ( shi == 0 )
				val += 1 * 10;
			else 
				val += cnNumericToArabic( cnn.substring(0, shi), false ) * 10;
			if ( shi < cnn.length() - 1 )
				cnn = cnn.substring(shi + 1, cnn.length());
			else 
				cnn = "";
		}
		
		cnn = cnn.trim();
		for ( int j = 0; j < cnn.length(); j++ )
			val += isCNNumeric(cnn.charAt(j))
				* Math.pow(10, cnn.length() - j - 1);
			
		
		return val;
	}
	
	public static int qCNNumericToArabic( String cnn ) {
		int val = 0;
		cnn = cnn.trim();
		for ( int j = 0; j < cnn.length(); j++ )
			val += isCNNumeric(cnn.charAt(j))
				* Math.pow(10, cnn.length() - j - 1);
		return val;
	}
	//阿拉伯数字转为汉字数字
	public static String formatInteger(int num) {
		char[] val = String.valueOf(num).toCharArray();
		int len = val.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			String m = val[i] + "";
			int n = Integer.valueOf(m);
			boolean isZero = n == 0;
			String unit = units[(len - 1) - i];
			if (isZero) {
				if ('0' == val[i - 1]) {
					// not need process if the last digital bits is 0
					continue;
				} else {
					// no unit for 0
					sb.append(numArray[n]);
				}
			} else {
				sb.append(numArray[n]);
				sb.append(unit);
			}
		}
		String str = sb.toString();
		if(str.endsWith("零")){
			str = str.substring(0, str.length()-1);
		}
		if(str.startsWith("一十")){
			str = str.replace("一十", "十");
		}
		return str;
	}

	public static void main(String[] args) {
//		ADictionary.isCNNumeric('一');
		int val = 0;
		long s = System.nanoTime();
		val = cnNumericToArabic("三亿二千零六万七千五百六", true);
		//val = cnNumericToArabic("一九九八", true);
		long e = System.nanoTime();
		System.out.format("Done["+val+"], cost: %.5fsec\n", ((float)(e - s)) / 1E9);
	}
}


//该代码片段来自于: http://www.sharejs.com/codes/java/5577
