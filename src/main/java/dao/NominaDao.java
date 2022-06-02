/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import modelo.Nomina;
import modelo.HibernateUtil;
import modelo.Trabajadorbbdd;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author jp
 */
public class NominaDao {
    
    public void aniadirNomina(Nomina n) {
        SessionFactory sf;
        Session session;
        Transaction tx;
        
        try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
            } else {
                session = sf.getCurrentSession();
            }
            tx = session.getTransaction();
            session.save(n);
            tx.commit();
            session.getTransaction().commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public boolean buscarNomina(Nomina n) {
        SessionFactory sf;
        Session session;
        List<Nomina> res = null;

         try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
            } else {
                session = sf.getCurrentSession();
            }
            String consulta = "FROM Nomina n WHERE (n.mes=:param1 and n.anio=:param2 and n.trabajadorbbdd.idTrabajador=:param3 and n.brutoNomina=:param4 and n.liquidoNomina=:param5)";
            session.beginTransaction();
            Query query = session.createQuery(consulta);
            query.setParameter("param1", n.getMes());
            query.setParameter("param2", n.getAnio());
            query.setParameter("param3", n.getTrabajadorbbdd().getIdTrabajador());
            query.setParameter("param4", n.getBrutoNomina());
            query.setParameter("param5", n.getLiquidoNomina());
            res = query.list();
            session.flush();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        if(res != null) {
            return res.isEmpty();
        } else {
            return true;
        }
    }
}
