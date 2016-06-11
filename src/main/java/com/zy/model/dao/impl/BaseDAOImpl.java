package com.zy.model.dao.impl;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public abstract class BaseDAOImpl extends TemplateDAOImpl{
	@Resource(name = "sessionFactory")
	public void setSessionFactory0(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

}
