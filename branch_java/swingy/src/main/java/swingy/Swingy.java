package swingy;

import swingy.controller.GameController;
import swingy.view.ViewFactory;
import swingy.view.View;
import swingy.utils.ViewType;
import swingy.utils.game_menu.MainMenuChoice;
import swingy.utils.Colors;

public class Swingy {
    public static void main(String[] args) {
        View gameView = ViewFactory.createView(ViewType.CONSOLE);
        GameController gameController = new GameController(gameView);

        MainMenuChoice mainMenuChoice = gameController.createGame();
        switch (mainMenuChoice) {
            case NEW_GAME:
                System.out.println("Create new game");
                return;
            case CONTINUE_SAVE:
                System.out.println("Load saves");
                return;
            case SETTING:
                System.out.println("Load setting");
                return;
            case EXIT:
                gameController.closeGame();
                System.out.println(Colors.YELLOW + "Fantasy over! Get back to work!" + Colors.RESET);
                return;
        }
    }
}