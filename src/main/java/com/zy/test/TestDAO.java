package com.zy.test;

import java.io.File;
import java.util.List;

import com.opensymphony.xwork2.util.ResolverUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import wuhao.tools.hibernate.Inquiry;

import com.zy.model.beans.User;
import com.zy.model.dao.inter.CommonDAO;
import wuhao.tools.reader.PathUtil;

public class TestDAO {
	static Log log = LogFactory.getLog(TestDAO.class);
	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
		CommonDAO commonDAO = context.getBean(CommonDAO.class);
		User user = new User();
		user.setName("周意");
		user.setPassword("zhouyi");
		commonDAO.saveOrUpdate(user);
		List<User> userList = commonDAO.find(Inquiry.forClass(User.class));
		System.setProperty("APP_PATH",  new File("").getAbsolutePath());
		String currentPath = new File("").getAbsolutePath();
		DOMConfigurator.configure(PathUtil.getClassesPath() + "/log4j.xml");
		System.out.println(System.getProperty("APP_PATH"));
		log.info(userList.size());
	}

}
