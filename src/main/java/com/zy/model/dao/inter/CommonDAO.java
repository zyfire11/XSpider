package com.zy.model.dao.inter;

import wuhao.tools.beans.Pager;
import wuhao.tools.hibernate.Inquiry;

import java.util.List;
import java.util.Map;

public interface CommonDAO {

	public int count(Inquiry inquiry);

	public Map<? extends Object, Integer> countByGroupProperty(Inquiry inquiry);

	public boolean delete(Object object);

	/**
	 * 根据ID删除数据
	 * @param clazz 要删除的对象类
	 * @param id    对象ID
	 */
	public boolean deleteById(Class<?> clazz, Integer id);

	public int deleteAll(List list);

	/**
	 * @param inquiry
	 */
	public int executeUpdate(Inquiry inquiry);

	/**
	 * 批量执行更新操作
	 * @param inquiries
	 */
	public int executeUpdate(List<Inquiry> inquiries);

	public int executeUpdate(String queryString);

	public List find(Inquiry inquiry);

	public List find(Inquiry inquiry, int start, int limit);

	public List find(Inquiry inquiry, Pager pager);

	public <T> List<T> findAll(Class<T> class1);

	public <T> T findById(Class<T> class1, Integer id);

	public <T> T findById(Class<T> class1, String id);

	public List findBySql(Inquiry inquiry, Pager pager);

	public List findBySql(String sql);

	public List findBySql(Inquiry inquiry, int start, int limit);

	public List findBySql(Inquiry inquiry);

	public boolean save(Object object);

	public int saveAll(List reflects);

	/**
	 * 保存或更新对象
	 * @param object
	 */
	public boolean saveOrUpdate(Object object);

	public int saveOrUpdateAll(List list);

	public Object unique(Inquiry inquiry);

	public boolean update(Object obj);

	/**
	 * 批量更新对象
	 * @param list
	 */
	public boolean updateAll(List list);

	public int executeSqlUpdate(String s);
}
