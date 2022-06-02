/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.util.List;
import modelo.Categorias;
import modelo.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author jp
 */
public class CategoriaDao {
    private SessionFactory sf = null;
    private Session session = null;
    Transaction tx;
    
    public CategoriaDao() {
        sf = HibernateUtil.getSessionFactory();
        session = sf.openSession();
        tx = session.beginTransaction();
    }
    public boolean buscarCategoria(String nombre) {
        List<Categorias> res = null;
        
        try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
                tx = session.beginTransaction();
            } else {
                session = sf.getCurrentSession();
                tx = session.beginTransaction();
            }
            String consulta = "FROM Categorias c WHERE c.nombreCategoria=:param1";
            Query query = session.createQuery(consulta);
            query.setParameter("param1", nombre);
            res = query.list();
            session.flush();
            session.close();

        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
        if(res != null) {
            return res.isEmpty();
        } else {
            return true;
        }
      
    }

    
    public void aniadirCategoria(Categorias c) {
        try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
                tx = session.beginTransaction();
            } else {
                session = sf.getCurrentSession();
            }
            tx = session.beginTransaction();
            session.saveOrUpdate(c);
            tx.commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }
}
