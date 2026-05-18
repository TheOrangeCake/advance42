package com.nguyen.database;

import com.nguyen.colors.Colors;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSession {
    private static HibernateSession instance;
    private final SessionFactory sessionFactory;

    private HibernateSession() throws HibernateException {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Database connection established");
    }

    public static synchronized HibernateSession getInstance() throws HibernateException {
        if (instance == null) {
            instance = new HibernateSession();
        }
        return instance;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void stop() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}