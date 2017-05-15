package com.zy.logic.input;

import com.zy.logic.component.loader.SimpleLoader;
import com.zy.model.beans.Sockpuppet;
import com.zy.model.dao.inter.CommonDAO;
import com.zy.utils.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wuhao.tools.hibernate.Inquiry;

import java.util.List;

/**
 * Created by zhouyi on 2017/5/15.
 */
public class SinaInput {


    private static CommonDAO commonDAO;

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        commonDAO = context.getBean(CommonDAO.class);
//        String url = "C:\\Users\\Administrator\\Desktop\\文件\\账号\\sina_email.txt";
//        String html = FileUtil.readFile(url, "gbk");
//        System.out.println(html);
//        String[] params = html.split("\\n");
//        for(String para : params){
//            String name = para.split("----")[0];
//            String password = para.split("----")[1];
//            Sockpuppet sockpuppet = new Sockpuppet();
//            sockpuppet.setName(name);
//            sockpuppet.setPassword(password);
//            sockpuppet.setValid(true);
//            sockpuppet.setRegister(false);
//            commonDAO.saveOrUpdate(sockpuppet);
//
//        }
//        System.out.println("马甲录入完成");

        List<Sockpuppet> sockpuppets = commonDAO.findAll(Sockpuppet.class);
        for(Sockpuppet sockpuppet : sockpuppets){
            String name = sockpuppet.getName().trim();
            String password = sockpuppet.getPassword().trim();
            sockpuppet.setName(name);
            sockpuppet.setPassword(password);

            commonDAO.update(sockpuppet);
        }
        System.out.println("over");
    }
}
