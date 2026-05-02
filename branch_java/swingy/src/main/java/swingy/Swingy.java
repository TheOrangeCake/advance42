package swingy;

import swingy.controller.GameController;
import swingy.model.state.DatabaseConfig;
import swingy.utils.Colors;
import swingy.view.ViewFactory;
import swingy.view.View;
import swingy.view.ViewType;

public class Swingy {
    static void main(String[] args) {
        int len = args.length;
        View gameView;
        switch (len) {
            case 0:
                System.out.println(Colors.YELLOW + "Warn" + Colors.RESET + ": No view specified, launching gui mode");
                gameView = ViewFactory.createView(ViewType.GUI);
                break;
            case 1:
                if (args[0].equalsIgnoreCase("gui")) {
                    gameView = ViewFactory.createView(ViewType.GUI);
                } else if (args[0].equalsIgnoreCase("console")) {
                    gameView = ViewFactory.createView(ViewType.CONSOLE);
                } else {
                    System.out.println(Colors.RED + "Error" + Colors.RESET + ": Invalid view. Relaunch with either gui or console");
                    return;
                }
                break;
            default:
                System.err.println(Colors.RED + "Error" + Colors.RESET + ": Too many options!");
                return;
        }
        DatabaseConfig.getSessionFactory();
        GameController gameController = new GameController(gameView);
        gameController.start();
    }
}
