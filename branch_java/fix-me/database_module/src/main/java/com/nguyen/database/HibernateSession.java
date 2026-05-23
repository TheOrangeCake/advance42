package com.nguyen.database;

import com.nguyen.colors.Colors;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSession {
    private static HibernateSession instance;
    private final SessionFactory sessionFactory;

    private HibernateSession(String configFile) throws HibernateException {
        sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Database connection established");
    }

    public static synchronized HibernateSession init(String configFile) throws HibernateException {
        if (instance == null) {
            instance = new HibernateSession(configFile);
        }
        return instance;
    }

    // Used everywhere else, no argument needed
    public static synchronized HibernateSession getInstance() throws HibernateException {
        if (instance == null) {
            throw new IllegalStateException("HibernateSession not initialized. Call init() first.");
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