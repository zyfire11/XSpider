/**
 * 
 */
package com.zy.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * @author zhouyi
 * @date 2016-1-27 上午10:37:20
 * @description 文件操作类
 */
public class FileUtil {	
	/**
	 * @param filePath : 文件路径
	 * @param text ：文本
	 * @param append ： 是否写到文件最后
	 * @param charset : 文本编码
	 * @return 写入是否成功
	 */
	public static boolean writeFile(String filePath, String text, boolean append, String charset) {
		boolean flag = false;
		File file = new File(filePath);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		OutputStreamWriter ow = null;
		BufferedWriter writer = null;
		try {
			ow = new OutputStreamWriter(new FileOutputStream(file, append), charset);
			writer = new BufferedWriter(ow);
			writer.write(text);
			writer.flush();
			flag = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				writer.close();
				ow.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return flag;
	}
	/**
	 * 
	 * @param filePath : 文件路径
	 * @param charset ： 文本编码
	 * @return 文本
	 */
	public static String readFile(String filePath, String charset) {
		File file = new File(filePath);
		StringBuilder sb = new StringBuilder();
		String s = "";
		if (!file.exists()) {
			return null;
		}
		InputStreamReader input = null;
		BufferedReader br = null;
		try {
			input = new InputStreamReader(new FileInputStream(filePath), charset);
			br = new BufferedReader(input);
			while ((s = br.readLine()) != null) {
				sb.append(s + "\n");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
