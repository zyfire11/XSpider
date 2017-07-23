package com.zy.tool.selenium;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouyi on 2017/7/22.
 */
public class HelloWorld {

    public static void main(String[] args) {

        String url = "http://openlaw.cn/search/judgement/type?causeId=a3ea79cf193f4e07a27a900e29585dbb";
//        url = "http://news.163.com/17/0722/13/CPV1OO6K0001875N.html";
//        url = "http://wenshu.court.gov.cn/list/list/?sorttype=1&conditions=searchWord+001008001+AY++%E6%A1%88%E7%94%B1:%E8%B4%AA%E6%B1%A1";
//        testPhantomjs(url);
//        testHtmlUnit(url);
        testChrome(url);
//        testFireFox(url);
    }

    public static void testChrome(String url){
        System.setProperty("webdriver.chrome.driver", "E:/soft/web_driver/chrome/2.21/chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get(url);
        System.out.println(driver.getPageSource());
//        driver.quit();
    }

    public static void testFireFox(String url){
        System.setProperty("webdriver.firefox.bin", "E:/soft/web_driver/firefox/32/geckodriver.exe");
        FirefoxDriver driver = new FirefoxDriver();
        driver.get(url);
        System.out.println(driver.getPageSource());
        driver.quit();
    }

    public static void testHtmlUnit(String url){
        HtmlUnitDriver driver = new HtmlUnitDriver(true);
        driver.get(url);
        System.out.println(driver.getPageSource());
        Set<Cookie> cookies = driver.manage().getCookies();
        for(Cookie cookie :cookies){
            System.out.println(cookie.getName() + "--" + cookie.getValue());
        }
        driver.quit();
    }

    public static void testPhantomjs(String url){
        System.setProperty("webdriver.phantomjs.bin", "E:/soft/phantomjs-2.1.1-windows/bin/phantomjs.exe");
        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        dcaps.setCapability("loadImages", false);
        //驱动支持
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "E:/soft/phantomjs-2.1.1-windows/bin/phantomjs.exe");

        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.navigate().to(url);
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        System.out.println(driver.getPageSource());
        driver.quit();


    }
}
