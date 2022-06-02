/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.util.List;
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
public class TrabajadorbbddDao {
    private SessionFactory sf = null;
    private Session session = null;
    Transaction tx;
    
    public TrabajadorbbddDao() {
        sf = HibernateUtil.getSessionFactory();
        session = sf.openSession();
        tx = session.beginTransaction();
    }

    public void aniadirTrabajador(Trabajadorbbdd t) {
        try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
                tx = session.beginTransaction();
            } else {
                session = sf.getCurrentSession();
            }
            tx = session.beginTransaction();
            session.saveOrUpdate(t);
            tx.commit();
        } catch (HibernateException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean buscarTrabajador(Trabajadorbbdd dni) {
        List<Trabajadorbbdd> res = null;
        
        try {
            sf = HibernateUtil.getSessionFactory();
            if (sf.isClosed()) {
                session = sf.openSession();
            } else {
                session = sf.getCurrentSession();
                tx = session.beginTransaction();
            }
            String consulta = "FROM Trabajadorbbdd t WHERE (t.nifnie=:param1 and t.nombre=:param2 and t.fechaAlta=:param3)";
            Query query = session.createQuery(consulta);
            query.setParameter("param1", dni.getNifnie());
            query.setParameter("param2", dni.getNombre());
            query.setParameter("param3", dni.getFechaAlta());
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
}
