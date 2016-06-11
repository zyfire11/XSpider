package com.zy.model.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import wuhao.tools.beans.Pager;
import wuhao.tools.hibernate.Inquiry;

import com.zy.model.dao.inter.CommonDAO;

@Repository
public class CommonDAOImpl extends BaseDAOImpl implements CommonDAO{

	public boolean deleteById(Class<?> clazz, Integer id) {
		return delete(getHibernateTemplate().get(clazz, id));
	}

	public int deleteAll(List list) {
		Session session = null;
		try {
			session = getHibernateTemplate().getSessionFactory().openSession();
			for(Object o:list){
				session.delete(o);
			}
			session.flush();
			return list.size();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}finally {
			session.close();
		}
	}

	public int executeUpdate(List<Inquiry> inquiries) {
		Session session = null;
		try {
			int count = 0;
			session = getHibernateTemplate().getSessionFactory().openSession();
			try {
				for (Inquiry inquiry : inquiries) {
					Query query = session.createQuery(inquiry.getHql());
					int i = query.executeUpdate();
					count += i;
				}
				session.flush();
				return count;
			} catch (Exception e) {
				e.printStackTrace();
				for (Inquiry inquiry : inquiries) {
					int result = executeUpdate(inquiry);
					if (result > 0) {
						count++;
					}
				}
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			session.close();
		}
	}

	public List find(Inquiry inquiry, Pager pager) {
		return find(inquiry, (int)pager.getCurrentIndex(), (int)pager.getLimit());
	}

	public List findAll(Class class1) {
		return find(Inquiry.forClass(class1));
	}

	public <T> T findById(Class<T> class1, Integer id) {
		return getHibernateTemplate().get(class1, id);
	}

	public <T> T findById(Class<T> class1, String id) {
		Session session = null;
		try {
			session = openSession();
			return (T) session.get(class1, id);
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}finally {
			session.close();
		}
	}

	@Override
	public List findBySql(Inquiry inquiry, Pager pager) {
		return findBySql(inquiry.getSql(),(int)pager.getCurrentIndex(),(int)pager.getLimit());
	}

	public List findBySql(String sql){
		return findBySql(sql,-1,-1);
	}


	@Override
	public List findBySql(Inquiry inquiry, int start, int limit) {
		// TODO Auto-generated method stub
		return findBySql(inquiry.getSql(), start, limit);
	}
	


}
