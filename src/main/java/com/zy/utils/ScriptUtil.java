/**
 * 
 */
package com.zy.utils;

import com.zy.logic.component.loader.SimpleLoader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 * @author zhouyi
 * @date 2015-12-30 上午10:01:45
 * javaapi执行js代码
 */
public class ScriptUtil {

	/**
	 * @param args
	 */
	public static String excuteJs(String js, String functionName, Object jsForm){
		
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		try {
			engine.eval(js);
			Invocable jsInvoke = (Invocable)engine;
			Object object = jsInvoke.invokeFunction(functionName, jsForm);
			System.out.println(object);
			return object.toString();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String js = SimpleLoader.readHtml("D:\\test\\cpws.js", "utf-8");
		js = js.replaceAll("<script>", "").replaceAll("</script>", "");
//		js = js.replaceAll("document\\.cookie\\s*=\\s*dc;", "return dc;");
		
		js = "function ccc(){" + js + "}";
		ScriptUtil.excuteJs(js, "ccc", "");
	}

}
