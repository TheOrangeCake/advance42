package swingy;

import swingy.controller.GameController;
import swingy.view.ViewFactory;
import swingy.view.View;
import swingy.utils.ViewType;

public class Swingy {
    static void main(String[] args) {
        View gameView = ViewFactory.createView(ViewType.GUI);
        GameController gameController = new GameController(gameView);
        gameController.start();
    }
}
