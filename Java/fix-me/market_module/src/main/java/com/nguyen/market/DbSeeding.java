package com.nguyen.market;

import com.nguyen.colors.Colors;
import com.nguyen.market.model.FirstFlag;
import com.nguyen.market.model.Instrument;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class DbSeeding {
    private static final List<Instrument> instruments = List.of(
        new Instrument("AAPL", "Apple Inc.", 1000, 185.50),
        new Instrument("TSLA", "Tesla Inc.", 500, 720.10),
        new Instrument("MSFT", "Microsoft Corp.", 800, 410.25),
        new Instrument("GOOGL", "Alphabet Inc.", 600, 135.80),
        new Instrument("AMZN", "Amazon.com Inc.", 300, 178.90),
        new Instrument("NVDA", "NVIDIA Corp.", 450, 950.00),
        new Instrument("META", "Meta Platforms", 400, 480.75),
        new Instrument("NFLX", "Netflix Inc.", 250, 610.30),
        new Instrument("UBS", "UBS Group AG", 1200, 28.40),
        new Instrument("NOVN", "Novartis AG", 900, 92.15)
    );

    public static void seedMockData(SessionFactory sf) {
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "First run, seed mock data");
        try (Session session = sf.openSession()) {
            Transaction tx = session.beginTransaction();
            FirstFlag flag = new FirstFlag();
            session.persist(flag);
            for (Instrument i : instruments) {
                session.persist(i);
            }
            tx.commit();
        }
    }

    public static boolean isFirstRun(SessionFactory sf) {
        try (Session session = sf.openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(f) FROM FirstFlag f", Long.class)
                    .uniqueResult();
            return count == null || count == 0;
        }
    }
}
