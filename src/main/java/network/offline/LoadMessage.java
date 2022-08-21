package network.offline;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.List;

public class LoadMessage {
    private static SessionFactory sessionFactory = SaveMessage.sessionFactory;

    public static <T> T fetch(Class<T> tClass, Serializable id) {
        Session session = sessionFactory.openSession();
        T t = session.get(tClass, id);
        session.close();
        return t;
    }
    public static <E> List<E> fetchAll(Class<E> entity) {
        Session session = sessionFactory.openSession();
        List<E> list = session.createQuery("from " + entity.getName(), entity).getResultList();
        session.close();
        return list;
    }

    public static void delete(OfflineMessage message) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(message);
        session.getTransaction().commit();
        session.close();
    }
}
