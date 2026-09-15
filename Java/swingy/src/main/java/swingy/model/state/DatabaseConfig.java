package swingy.model.state;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseConfig {
    private static SessionFactory sessionFactory;

    private DatabaseConfig() {

    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                sessionFactory = new Configuration()
                        .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                        .setProperty("hibernate.connection.url", "jdbc:h2:./swingyDb;DB_CLOSE_DELAY=-1")
                        .setProperty("hibernate.connection.username", "swingy")
                        .setProperty("hibernate.connection.password", "42Lausanne")
                        .setProperty("hibernate.hbm2ddl.auto", "update")
                        .addAnnotatedClass(HeroState.class)
                        .addAnnotatedClass(ArtifactState.class)
                        .addAnnotatedClass(GameMapState.class)
                        .addAnnotatedClass(VillainState.class)
                        .buildSessionFactory();
            } catch (HibernateException e) {
                System.err.println("Fail to create/connect H2 database: " + e.getMessage());
                System.exit(-1);
            }
        }
        return sessionFactory;
    }

    public static void close() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
