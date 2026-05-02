package swingy;

import swingy.controller.GameController;
import swingy.model.state.DatabaseConfig;
import swingy.utils.Colors;
import swingy.view.ViewSwitcher;
import swingy.view.ViewType;

public class Swingy {
    static void main(String[] args) {
        int len = args.length;
        ViewType gameView;
        switch (len) {
            case 0:
                System.out.println(Colors.YELLOW + "Warn" + Colors.RESET + ": No view specified, launching gui mode");
                gameView = ViewType.GUI;
                break;
            case 1:
                if (args[0].equalsIgnoreCase("gui")) {
                    gameView = ViewType.GUI;
                } else if (args[0].equalsIgnoreCase("console")) {
                    gameView = ViewType.CONSOLE;
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
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConfig.close();
            System.out.println(Colors.YELLOW + "Fantasy over! Get back to work!" + Colors.RESET);
        }));
        ViewSwitcher switcher = new ViewSwitcher(gameView);
        GameController gameController = new GameController(switcher);
        gameController.start();
    }
}
