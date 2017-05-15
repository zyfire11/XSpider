package com.zy.test;

import com.zy.logic.component.loader.SimpleLoader;
import com.zy.model.beans.PhoneNumber;
import com.zy.model.dao.inter.CommonDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyi on 2017/5/15.
 */
public class PhoneSpider {

    private static CommonDAO commonDAO;


    public void parseHenanxushi(){
        String baseUrl = "http://www.henanxushi.com/shop.asp?ex_index=1&Page=";

        int sum = 0;
        for(int i = 1; i < 19; i++){
            List<String> phoneList = new ArrayList<>();

            String url = baseUrl+i;
            String html = SimpleLoader.download(url, "gb2312");
            System.out.println("第" + i + "页c采集开始");
            if(html !=null && html.length() > 100){

                Document doc = Jsoup.parse(html);
                Element content = doc.select("div.shop_content").first();
                Elements tdEls = content.select("td.bar");
                for(Element tr : tdEls){
                    String phoneNumber = tr.select("a").text();
                    phoneList.add(phoneNumber);
                }
            }
            System.out.println("第" + i + "页c采集到号码：" + phoneList.size());
            for(String phone : phoneList){
                PhoneNumber phoneNumber = new PhoneNumber();
                phoneNumber.setName(phone);
                phoneNumber.setType(2);
                phoneNumber.setUsed(false);
                phoneNumber.setValid(true);
                commonDAO.saveOrUpdate(phoneNumber);
                sum ++;
            }
        }
        System.out.println("总共采集到：" + sum + "个");

    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        commonDAO = context.getBean(CommonDAO.class);
        new PhoneSpider().parseHenanxushi();
    }
}
