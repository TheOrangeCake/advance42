package swingy.model.state;

import org.hibernate.Session;

public class DatabaseQueries {
    public static HeroState[] loadAllSlots() {
        HeroState[] saves = new HeroState[3];
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            for (int i = 1; i <= 3; i++) {
                saves[i - 1] = session.find(HeroState.class, i);
            }
        }
        return saves;
    }
}