package com.zy.logic.timer;

import com.zy.model.beans.NickName;
import com.zy.model.beans.PhoneNumber;
import com.zy.model.beans.Sockpuppet;
import com.zy.model.dao.inter.CommonDAO;
import com.zy.model.entity.StateCode;
import com.zy.sina.LoginSina;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import wuhao.tools.hibernate.Inquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhouyi on 2017/5/15.
 */
public class WeiboRegisterThread extends Thread {

    private static Logger log = Logger.getLogger(WeiboRegisterThread.class);
    private static CommonDAO commonDAO;
    private static List<Sockpuppet> sockpuppets = new ArrayList<>();
    private static List<PhoneNumber> phoneNumbers = new ArrayList<>();
    private static List<NickName> nickNameList = new ArrayList<>();

    public void run(){
        log.info("注册开始，手机号总数：" + phoneNumbers.size() + ",邮箱总数：" + sockpuppets.size() + ",昵称数：" + nickNameList.size());
        int sum = 0;
        if(sockpuppets.size() > 0 && phoneNumbers.size() > 0){
            for(Sockpuppet sockpuppet : sockpuppets){
                if(phoneNumbers.size() > 0) {
                    PhoneNumber phoneNumber = phoneNumbers.get(0);
                    NickName nickName = nickNameList.get(new Random().nextInt(nickNameList.size()));
                    LoginSina loginSina = new LoginSina(sockpuppet.getName(), sockpuppet.getPassword(), nickName.getName(), phoneNumber.getName());
                    //这块需整理登录的状态（手机号有问题还是邮箱有问题）
                    int code = loginSina.dologinSina();
                    if(code == 1){
                        log.info("账号：<" + sockpuppet.getName() + ">,绑定到手机号<"+ phoneNumber.getName() +">,注册成功");
                        commonDAO.executeUpdate(Inquiry.forClass(Sockpuppet.class).addUpdate("isRegister", true).addEq("id", sockpuppet.getId()));
                        commonDAO.executeUpdate(Inquiry.forClass(NickName.class).addUpdate("num", nickName.getNum()+1).addEq("id", nickName.getId()));
                        sum ++;
                    }else{
                        log.info("账号：" + sockpuppet.getName() + "注册失败,失败代码：" + code);
                    }
                    //更新手机和邮箱的状态
                    if(code == StateCode.SUCCESS){
                        commonDAO.executeUpdate(Inquiry.forClass(PhoneNumber.class).addUpdate("used", true).addUpdate("usedNum", phoneNumber.getUsedNum()+1)
                                .addEq("id", phoneNumber.getId()));
                        phoneNumbers.remove(phoneNumber);
                    }else if(code == StateCode.EMAIL_ERROR || code == StateCode.EMAIL_REGISTERED){
                        commonDAO.executeUpdate(Inquiry.forClass(Sockpuppet.class).addUpdate("valid", false).addEq("id", sockpuppet.getId()));
                    } else if(code == StateCode.PHONE_REGISTERED){
                        commonDAO.executeUpdate(Inquiry.forClass(PhoneNumber.class).addUpdate("used", true).addUpdate("valid", false)
                                .addEq("id", phoneNumber.getId()));
                        phoneNumbers.remove(phoneNumber);
                    }else if(code == StateCode.PHONE_REGISTER_FREQUENTLY){
                        phoneNumbers.remove(phoneNumber);
                    }
                    //

                }else{
                    log.info("手机号不够");
                    break;
                }
                try {
                    Thread.sleep(1 * 60 * 1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }else{
            log.info("号码或邮箱账号不足");
        }
        log.info("微博账号注册结束，注册成功个数：" + sum);

    }

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        commonDAO = context.getBean(CommonDAO.class);

        sockpuppets = commonDAO.find(Inquiry.forClass(Sockpuppet.class).addEq("isRegister", false).addEq("valid", true));
        phoneNumbers = commonDAO.find(Inquiry.forClass(PhoneNumber.class).addEq("valid", true).addEq("used", false));
        nickNameList = commonDAO.find(Inquiry.forClass(NickName.class));
        new WeiboRegisterThread().start();
    }
}
