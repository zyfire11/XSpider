package com.zy.controller.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;

import wuhao.tools.hibernate.Inquiry;

import com.opensymphony.xwork2.ActionSupport;
import com.zy.model.beans.User;
import com.zy.model.dao.inter.CommonDAO;

@ParentPackage("default")
public class UserAction extends ActionSupport {
	
	@Autowired
	private CommonDAO commonDAO;
	public String execute(){
		List<User> list = commonDAO.find(Inquiry.forClass(User.class));
		System.out.println(list.size());
		return null;
		
	}

}
