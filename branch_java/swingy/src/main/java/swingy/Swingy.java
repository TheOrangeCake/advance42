package swingy;

import org.hibernate.SessionFactory;
import swingy.controller.GameController;
import swingy.model.state.DatabaseConfig;
import swingy.view.ViewFactory;
import swingy.view.View;
import swingy.view.ViewType;

public class Swingy {
    static void main(String[] args) {
        View gameView = ViewFactory.createView(ViewType.GUI);
        SessionFactory sessionFactory = DatabaseConfig.getSessionFactory();
        GameController gameController = new GameController(gameView);
        gameController.start();
    }
}
