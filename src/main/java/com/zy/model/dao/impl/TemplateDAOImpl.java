package com.zy.model.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import com.zy.model.dao.inter.CommonDAO;

import wuhao.tools.hibernate.Inquiry;
import wuhao.tools.hibernate.Projections;
import wuhao.tools.hibernate.projection.CountProjection;
import wuhao.tools.utils.StringUtil;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

public class TemplateDAOImpl<T> extends HibernateDaoSupport {

    protected static Log log = LogFactory.getLog(CommonDAO.class);
    protected Class<?> entityClass;
    protected String entityName;
    protected final String NAME = "name";
    protected final String ID = "id";
    protected final String CLIENT_ID = "clientId";
    protected final String CLIENT = "client";
    protected final String PROVINCE_ID = "provinceId";

    public Object unique(Inquiry inquiry) {
        Session session = null;
        try {
            session = openSession();
            Query query = session.createQuery(inquiry.getHql());
            return query.uniqueResult();
        } catch (Exception e) {
            log.error(e);
            return null;
        } finally {
            session.close();
        }
    }

    public List<?> find(Inquiry inquiry, int start, int limit) {
        return find(inquiry.getHql(), start, limit);
    }

    public List find(Inquiry inquiry) {
        return find(inquiry.getHql());
    }

    public int count(Inquiry inquiry) {
        inquiry = inquiry.clone();
        if(inquiry.getSql().trim().toLowerCase().startsWith("select count(")==false){
            if(inquiry.getProjection() instanceof CountProjection == false){
                inquiry.setProjection(Projections.count());
            }
        }
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = null;
            if(inquiry.isSupportHql()){
                query = session.createQuery(inquiry.getHql());
            }else{
                query = session.createSQLQuery(inquiry.getSql());
            }
            int result = Integer.parseInt(query.list().get(0).toString());
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->执行结果：" + result);
            return result;
        } catch (Exception e) {
            if(inquiry.isSupportHql()){
                log.error("HQL执行错误：" + inquiry.getHql(), e);
            }else{
                log.error("SQL执行错误：" + inquiry.getSql(), e);
            }
            return -1;
        }finally {
            session.close();
        }
    }

    public int executeUpdate(Inquiry inquiry) {
        return executeUpdate(inquiry.getHql());
    }

    public Object uniqueResult(String property, Object value) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(entityClass);
            criteria.add(Restrictions.eq(property, value));
            Object object = criteria.uniqueResult();
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + object);
            return object;
        } catch (Exception e) {
            log.error(e);
            return null;
        } finally {
            session.close();
        }
    }

    private void log(long time, String content) {
        if (time >= 30000) {
            log.error(content);
        } else if (time > 10000 && time < 30000) {
            log.debug(content);
        } else {
            log.debug(content);
        }
    }

    public T uniqueResult(String queryString) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            Query query = session.createQuery(queryString);
            Object object = query.uniqueResult();
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + queryString);
            return (T) object;
        } catch (Exception e) {
            log.error("执行HQL查询出错：" + queryString, e);
            return null;
        } finally {
            session.close();
        }
    }

    public Object uniqueResultExcludeId(Integer id, String property, Object value) {
        Session session = null;
        try {
            session = getSessionFactory().openSession();
            Criteria criteria = session.createCriteria(entityClass);
            criteria.add(Restrictions.eq(property, value));
            criteria.add(Restrictions.ne("id", id));
            return criteria.uniqueResult();
        } catch (Exception e) {
            log.error(e);
            return null;
        } finally {
            session.close();
        }
    }

    public Session openSession() {
        return getHibernateTemplate().getSessionFactory().openSession();
    }

    public int countAll() {
        long currentTime = System.currentTimeMillis();
        String queryString = "select count(*) from " + entityName;
        try {
            int result = Integer.parseInt(getHibernateTemplate().find(queryString).get(0).toString());
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时:" + (System.currentTimeMillis() - currentTime) + "毫秒-->执行结果:" + result + "|" + queryString);
            return result;
        } catch (Exception e) {
            log.error("HQL执行错误：" + queryString, e);
            return -1;
        }
    }

    public List find(String queryString) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            if (!queryString.trim().toLowerCase().startsWith("select") && !queryString.trim().toLowerCase().startsWith("from")) {
                if (queryString.trim().toLowerCase().startsWith("where")) {
                    queryString = "from " + entityName + " " + queryString;
                } else {
                    queryString = "from " + entityName + " where " + queryString;
                }
            }
            Query query = session.createQuery(queryString);
            List list = query.list();
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->返回" + list.size() + "|" + queryString);
            return list;
        } catch (Exception e) {
            log.error("HQL执行错误：" + queryString, e);
            return null;
        }finally {
            session.close();
        }
    }

    public List find(String queryString, int start, int limit) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            if (!queryString.trim().toLowerCase().startsWith("select") && !queryString.trim().toLowerCase().startsWith("from")) {
                if (queryString.trim().toLowerCase().startsWith("where")) {
                    queryString = "from " + entityName + " " + queryString;
                } else {
                    queryString = "from " + entityName + " where " + queryString;
                }
            }
            Query query = session.createQuery(queryString);
            query.setCacheable(true);
            query.setFirstResult(start);
            query.setMaxResults(limit);
            List list = query.list();
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + "起始" + start + "|结束" + (start + limit) + "|返回" + list.size() + "|" + queryString);
            return list;
        } catch (Exception e) {
            log.error("HQL执行错误：" + queryString, e);
            return null;
        } finally {
            session.close();
        }
    }

    public boolean delete(Object object) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            session.delete(object);
            return true;
        } catch (Exception e) {
            log.error(object, e);
            return false;
        }finally {
            session.close();
        }
    }

    public boolean deleteAll(String ids) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createQuery("delete " + entityName + " where " + ID + " in (" + ids + ")");
            query.executeUpdate();
            return true;
        } catch (Exception e) {
            log.error(null, e);
            return false;
        } finally {
            session.close();
        }
    }

    public boolean deleteById(Integer id) {
        try {
            getHibernateTemplate().delete(getHibernateTemplate().get(entityClass, id));
            return true;
        } catch (Exception e) {
            log.error(null, e);
            return false;
        }
    }

    public boolean deleteById(String id) {
        try {
            getHibernateTemplate().delete(getHibernateTemplate().get(entityClass, id));
            return true;
        } catch (Exception e) {
            log.error(null, e);
            return false;
        }
    }

    public List<?> executeHql(String hql) {
        long currentTime = System.currentTimeMillis();
        try {
            List<?> list = getHibernateTemplate().find(hql);
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + hql);
            return list;
        } catch (Exception e) {
            log.error("HQL错误: " + hql + "|" + this.getClass().getName() + ":\t" + e.getMessage(), e);
            return null;
        }
    }

    public List<?> executeHql(String hql, int firstResult, int maxResult) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            if (hql.trim().toLowerCase().startsWith("where")) {
                hql = "from " + entityName + " " + hql.trim();
            } else if (!hql.trim().toLowerCase().contains("from") && !hql.trim().toLowerCase().contains("where")) {
                hql = "from " + entityName + " where " + hql.trim();
            }
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createQuery(hql);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResult);
            List<?> list = query.list();
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + "起始" + firstResult + "|返回" + maxResult + "|" + hql);
            return list;
        } catch (Exception e) {
            log.error("HQL错误: " + "起始" + firstResult + "|返回" + maxResult + "|" + hql + "|" + this.getClass().getName() + ":\t" + e.getMessage(), e);
            return null;
        } finally {
            session.close();
        }
    }

    public List<?> executeSql(String sql) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createSQLQuery(sql);
            List<?> list = query.list();
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + sql);
            return list;
        } catch (Exception e) {
            log.error("HQL错误: " + sql + "|" + this.getClass().getName() + ":\t" + e.getMessage(), e);
            return null;
        } finally {
            session.close();
        }
    }

    public List<?> executeSql(String sql, int firstResult, int maxResult) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createSQLQuery(sql);
            query.setFirstResult(firstResult);
            query.setMaxResults(maxResult);
            List<?> list = query.list();
            log(System.currentTimeMillis() - currentTime, "执行SQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->" + "起始" + firstResult + "|返回" + maxResult + "|" + sql);
            return list;
        } catch (Exception e) {
            log.error("SQL错误: " + "起始" + firstResult + "|返回" + maxResult + "|" + sql + "|" + this.getClass().getName() + ":\t" + e.getMessage(), e);
            return null;
        } finally {
            session.close();
        }
    }

    public List findByIdList(List idList) {
        long currentTime = System.currentTimeMillis();
        if (idList == null) {
            return null;
        } else if (idList.isEmpty()) {
            return new ArrayList();
        }
        String queryString = null;
        try {
            String idString = "";
            for (Object object : idList) {
                if (object instanceof String) {
                    if (idString.length() > 0) {
                        idString += ",'" + (String) object + "'";
                    } else {
                        idString += "'" + (String) object + "'";
                    }
                } else if (object instanceof Integer || object instanceof Long) {
                    if (idString.length() > 0) {
                        idString += "," + object;
                    } else {
                        idString += object;
                    }
                }
            }
            queryString = "from " + entityName + " where " + ID + " in (" + idString + ")";
            List list = getHibernateTemplate().find(queryString);
            log(System.currentTimeMillis() - currentTime, "执行HQL查询用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->执行结果：" + list.size() + "|" + queryString);
            return list;
        } catch (Exception e) {
            log.error("执行HQL查询出错：" + queryString, e);
            return null;
        }
    }

    public Map<Integer, Integer> countByGroupProperty(String propertyName) {
        return countByGroupProperty(propertyName, null, null);
    }

    public Map<Integer, Integer> countByGroupProperty(String propertyName, String whereClause) {
        return countByGroupProperty(propertyName, whereClause, null);
    }

    public Map<Object, Integer> countByGroupProperty(Inquiry inquiry) {
        try {
            List<Object> list = findBySql(inquiry);
            Map<Object, Integer> map = new HashMap<Object, Integer>();
            for (Object object : list) {
                map.put(Array.get(object, 0), Integer.valueOf(Array.get(object, 1).toString()));
            }
            return map;
        } catch (Exception e) {
            log.error("执行HQL查询出错：" + inquiry, e);
            return null;
        }
    }

    public Map<Integer, Integer> countByGroupProperty(String propertyName, String whereClause, String order) {
        try {
            long currentTime = System.currentTimeMillis();

            String queryString = "select " + propertyName + ",count(" + propertyName + ") from " + entityName + " ";
            if (whereClause != null) {
                if (whereClause.trim().toLowerCase().startsWith("where")) {
                    queryString += whereClause;
                } else {
                    queryString += " where " + whereClause;
                }
            }
            queryString += " group by " + propertyName;
            if (order != null && order.trim().length() > 0) {
                queryString += " " + order;
            }
            List list = find(queryString);
            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (Object object : list) {
                if (object.getClass().isArray()) {
                    int arrayLength = Array.getLength(object);
                    if (arrayLength == 2) {
                        if (Array.get(object, 0) == null) {
                            continue;
                        }
                        Integer id = toInteger(Array.get(object, 0));
                        Integer count = toInteger(Array.get(object, 1));
                        map.put(id, count);
                    }
                }
            }
            log.debug("执行HQL查询用时" + (System.currentTimeMillis() - currentTime) + "毫秒:" + queryString);
            return map;
        } catch (Exception e) {
            log.error(null, e);
            return new HashMap<Integer, Integer>();
        }
    }

    private static Integer toInteger(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof BigInteger) {
            return ((BigInteger) object).intValue();
        } else if (object instanceof Integer) {
            return (Integer) object;
        } else if (object instanceof Short) {
            return ((Short) object).intValue();
        } else if (object instanceof Long) {
            return ((Long) object).intValue();
        } else if (object instanceof Byte) {
            return ((Byte) object).intValue();
        } else {
            return null;
        }
    }

    public int executeUpdate(String queryString) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createQuery(queryString);
            int result = query.executeUpdate();
            log(System.currentTimeMillis() - currentTime, "执行HQL更新用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->执行结果：" + result + "|" + queryString);
            return result;
        } catch (Exception e) {
            log.error("执行HQL更新出错：" + queryString, e);
            return -1;
        } finally {
            session.close();
        }
    }
    public int executeSqlUpdate(String queryString) {
        long currentTime = System.currentTimeMillis();
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createSQLQuery(queryString);
            int result = query.executeUpdate();
            log(System.currentTimeMillis() - currentTime, "执行SQL更新用时：" + (System.currentTimeMillis() - currentTime) + "毫秒 -->执行结果：" + result + "|" + queryString);
            return result;
        } catch (Exception e) {
            log.error("执行SQL更新出错：" + queryString, e);
            return -1;
        } finally {
            session.close();
        }
    }

    public List findAll() {
        try {
            return getHibernateTemplate().find("from " + entityName);
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    public Object findById(int id) {
        try {
            return getHibernateTemplate().get(entityClass, id);
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    public List findByPage(int start, int limit) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            Query query = session.createQuery("from " + entityName);
            query.setFirstResult(start);
            query.setMaxResults(limit);
            return query.list();
        } catch (Exception e) {
            log.error(null, e);
            return null;
        } finally {
            session.close();
        }

    }

    public List findByProperty(String name, Object value) {
        try {
            String queryString = null;
            if (value instanceof String) {
                queryString = "from " + entityName + " as model where model." + name + "= '" + value + "'";
            } else if (value instanceof Date) {
                queryString = "from " + entityName + " as model where model." + name + " = '" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) value) + "'";
            } else {
                queryString = "from " + entityName + " as model where model." + name + " = " + value;
            }
            return find(queryString);
        } catch (Exception e) {
            log.error(null, e);
            return null;
        }
    }

    public boolean merge(Object object) {
        try {
            getHibernateTemplate().merge(object);
            return true;
        } catch (Exception e) {
            log.error(null, e);
            return false;
        }
    }

    public boolean save(Object object) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            session.save(object);
            session.flush();
            return true;
        } catch (Exception e) {
            log.error(object, e);
            return false;
        }finally {
            session.close();
        }
    }

    public int saveAll(List list) {
        Session session = null;
        Transaction tx = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            tx = session.beginTransaction();
            for (Object object : list) {
                session.save(object);
            }
            session.flush();
            tx.commit();
            return list.size();
        } catch (Exception e) {
            log.info("对象批量存储出错，进行单个对象存储");
            int count = 0;
            tx.rollback();
            try {
                for (Object object : list) {
                    try {
                        session.save(object);
                        count++;
                    } catch (Exception e1) {
                        if (StringUtil.getStackTraceAsString(e1).indexOf("Duplicate entry") >= 0) {
                            log.debug(e1);
                        } else {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (Exception e2) {
                log.error(null, e2);
            }
            session.flush();
            return count;
        } finally {
            session.close();
        }
    }

    public boolean saveOrUpdate(Object object) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            session.saveOrUpdate(object);
            session.flush();
            return true;
        } catch (Exception e) {
            log.error(object, e);
            return false;
        }finally {
            session.close();
        }
    }

    public boolean update(Object object) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            session.update(object);
            session.flush();
            return true;
        } catch (Exception e) {
            log.error(null, e);
            return false;
        }finally {
            session.close();
        }
    }

    public int saveOrUpdateAll(List list) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            for (Object object : list) {
                session.saveOrUpdate(object);
            }
            session.flush();
            return list.size();
        } catch (Exception e) {
            log.info("对象批量存储出错，进行单个对象存储");
            int count = 0;
            try {
                for (Object object : list) {
                    try {
                        session.saveOrUpdate(object);
                        count++;
                        //						log.info("存储成功，成功数：" + count);
                    } catch (Exception e1) {
                        if (StringUtil.getStackTraceAsString(e1).indexOf("Duplicate entry") >= 0) {
                            log.debug(e1);
                        } else {
                            e1.printStackTrace();
                        }
                    }
                }
                session.flush();
            } catch (Exception e2) {
                log.error(null, e2);
            }
            return count;
        } finally {
            session.close();
        }
    }

    public boolean updateAll(List list) {
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            for (Object obj : list) {
                session.update(obj);
            }
            session.flush();
            return true;
        } catch (Exception e) {
            log.error(null, e);
            return false;
        } finally {
            session.close();
        }
    }

    public String preHandleHql(String queryString) {
        String orderClause = null;
        String whereClause = null;
        String fromClause = null;
        if (queryString.toLowerCase().contains("order") && queryString.toLowerCase().contains("by")) {
            orderClause = queryString.substring(queryString.lastIndexOf("order"));
            queryString = queryString.substring(0, queryString.lastIndexOf("order")).trim();
        }
        if (queryString.toLowerCase().contains("from") && queryString.toLowerCase().contains("where") && (queryString.toLowerCase().indexOf("from") < queryString.toLowerCase().indexOf("where"))) {
            fromClause = queryString.substring(0, queryString.toLowerCase().indexOf("where")).trim();
        }
        if (queryString.toLowerCase().contains("where")) {
            whereClause = queryString.substring(queryString.toLowerCase().indexOf("where")).replaceFirst("where", "").trim();
        } else {
            whereClause = queryString;
        }
        if (fromClause != null) {
            queryString = fromClause + " where " + whereClause;
        } else {
            queryString = "from " + entityName + " where " + whereClause;
        }
        if (orderClause != null) {
            queryString += " " + orderClause;
        }
        return queryString;
    }

    public List findBySql(Inquiry inquiry) {
        return findBySql(inquiry.getSql(), -1, -1);
    }

    @Cacheable("spring_query_cache")
    protected List findBySql(String sql,int start,int limit){
        Session session = null;
        try {
            session = getHibernateTemplate().getSessionFactory().openSession();
            SQLQuery query = session.createSQLQuery(sql);
            if(start>= 0) {
                query.setFirstResult(start);
            }
            if(limit>=0){
                query.setMaxResults(limit);
            }
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }
    }

}
