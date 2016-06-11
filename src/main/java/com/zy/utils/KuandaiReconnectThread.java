package com.zy.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class KuandaiReconnectThread extends Thread {

	private String	result;

	@Override
	public void run() {
		System.out.println("宽带断线重连");
		long time = 60;
		Runtime rn = Runtime.getRuntime();
		String basePath = new File("").getAbsolutePath();
		String appPath = basePath + "/app/kuandai.exe";
		String resultPath = basePath + "/app/kuandai.txt";
		File file = new File(resultPath);
		String[] params = new String[]{
			appPath, resultPath, "3", "宽带连接", "02589633022", "00003256"
		};
		try {
			rn.exec(params);
			while (time > 0) {
				if (file.exists()) {
					this.result = readFile(file);
					return;
				} else {
					time--;
					sleep(1000);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readFile(File file) {
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), "gbk");
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			String str = "";
			while ((lineTxt = bufferedReader.readLine()) != null) {
				str += lineTxt;
			}
			read.close();
			file.delete();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getResult() {
		return result;
	}

	public static void main(String[] args) {
		// KuandaiReconnectThread thread = new KuandaiReconnectThread();
		// thread.start();
		// try {
		// thread.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.println(thread.result);
		System.out.println(new File("").getAbsolutePath());
	}
}
