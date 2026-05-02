package swingy.model.state;

import org.hibernate.Session;
import org.hibernate.Transaction;
import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.state.mapper.GameMapMapper;
import swingy.model.state.mapper.HeroMapper;
import swingy.view.game_menu.SaveSlotChoice;

public class DatabaseQueries {
    public static Hero[] loadAllHeroStates() {
        Hero[] saves = new Hero[3];
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            for (int i = 1; i <= 3; i++) {
                saves[i - 1] = HeroMapper.mapToObject(session.find(HeroState.class, i));
            }
        }
        return saves;
    }
    public static Hero loadHero(SaveSlotChoice slotChoice) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            HeroState state = session.find(HeroState.class, slotChoice.getChoiceNumber());
            return HeroMapper.mapToObject(state);
        }
    }

    public static GameMap loadMap(SaveSlotChoice slotChoice) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            GameMapState state = session.find(GameMapState.class, slotChoice.getChoiceNumber());
            return GameMapMapper.mapToObject(state);
        }
    }

    public static void save(Hero hero, GameMap gameMap, SaveSlotChoice slotChoice) {
        if (slotChoice.getChoiceNumber() < 1 || slotChoice.getChoiceNumber() > 3) {
            return;
        }
        HeroState heroState = HeroMapper.mapToState(hero, slotChoice.getChoiceNumber());
        GameMapState gameMapState = GameMapMapper.mapToState(gameMap, heroState);
        heroState.setGameMap(gameMapState);
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            HeroState existing = session.find(HeroState.class, heroState.getSlot());
            if (existing != null) {
                session.remove(existing);
                session.flush();
            }
            session.persist(heroState);
            transaction.commit();
        }
    }

    public static void clear() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            for (int i = 1; i <= 3; i++) {
                HeroState existing = session.find(HeroState.class, i);
                if (existing != null) {
                    session.remove(existing);
                }
            }
            transaction.commit();
        }
    }
}
