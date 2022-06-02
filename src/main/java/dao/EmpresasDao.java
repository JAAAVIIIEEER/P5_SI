/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import modelo.Empresas;
import java.util.List;
import modelo.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
/**
 *
 * @author jp
 */
public class EmpresasDao {
    private SessionFactory sf = null;
    private Session session = null;
    Transaction tx;
    
    public EmpresasDao() {
        sf = HibernateUtil.getSessionFactory();
        session = sf.openSession();
        tx = session.beginTransaction();
    }
    public boolean buscarEmpresa(String cif) {
        List<Empresas> resultadoConsulta = null;
        
        try {
            sf = HibernateUtil.getSessionFactory();
           if (sf.isClosed()) {
                session = sf.openSession();
                tx = session.beginTransaction();
            } else {
                session = sf.getCurrentSession();
                tx = session.beginTransaction();
            }
            String consulta = "FROM Empresas t WHERE t.cif=:param1";
            Query query = session.createQuery(consulta);
            query.setParameter("param1", cif);
            resultadoConsulta = query.list();
            session.flush();
            session.close();

        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        if(resultadoConsulta == null) {
            return true;
        } else {
            return resultadoConsulta.isEmpty();
                
        }
    }
    
    public void aniadirEmpresa(Empresas emp) {
        try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
                tx = session.beginTransaction();
            } else {
                session = sf.getCurrentSession();
            }
            tx = session.beginTransaction();
            session.saveOrUpdate(emp);
            tx.commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }
}


