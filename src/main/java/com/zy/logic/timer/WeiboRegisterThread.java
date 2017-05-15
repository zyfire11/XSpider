package com.zy.logic.timer;

import com.zy.model.beans.PhoneNumber;
import com.zy.model.beans.Sockpuppet;
import com.zy.model.dao.inter.CommonDAO;
import com.zy.sina.LoginSina;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wuhao.tools.hibernate.Inquiry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyi on 2017/5/15.
 */
public class WeiboRegisterThread extends Thread {

    private static CommonDAO commonDAO;
    private static List<Sockpuppet> sockpuppets = new ArrayList<>();
    private static List<PhoneNumber> phoneNumbers = new ArrayList<>();

    public void run(){
        if(sockpuppets.size() > 0 && phoneNumbers.size() > 0){
            for(Sockpuppet sockpuppet : sockpuppets){
                if(phoneNumbers.size() > 0) {
                    PhoneNumber phoneNumber = phoneNumbers.get(0);
                    LoginSina loginSina = new LoginSina(sockpuppet.getName(), sockpuppet.getPassword(), "蘑菇头", phoneNumber.getName());
                    boolean flag = loginSina.dologinSina();
                    commonDAO.executeUpdate(Inquiry.forClass(PhoneNumber.class).addUpdate("used", true).addUpdate("usedNum", phoneNumber.getUsedNum()+1)
                            .addEq("id", phoneNumber.getId()));
                    if(flag){
                        System.out.println("账号：" + sockpuppet.getName() + "注册成功");
                        commonDAO.executeUpdate(Inquiry.forClass(Sockpuppet.class).addEq("isRegister", true).addEq("id", sockpuppet.getId()));
                    }else{
                        System.out.println("账号：" + sockpuppet.getName() + "注册失败");
                    }
                    phoneNumbers.remove(phoneNumber);
                }
            }

        }else{
            System.out.println("号码或邮箱账号不足");
        }

    }

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        commonDAO = context.getBean(CommonDAO.class);

        sockpuppets = commonDAO.find(Inquiry.forClass(Sockpuppet.class).addEq("isRegister", false).addEq("valid", true));
        phoneNumbers = commonDAO.find(Inquiry.forClass(PhoneNumber.class).addEq("valid", true).addEq("used", false));
        new WeiboRegisterThread().start();
    }
}
