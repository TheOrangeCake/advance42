package swingy.controller;

import swingy.view.View;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;
import swingy.utils.Colors;

public class GameController {
    private final View view;

    public GameController(View view) {
        this.view = view;
    }

    public void start() {
        view.startPage(this::onMainMenuChoice);
    }

    private void onMainMenuChoice(MainMenuChoice choice) {
        switch (choice) {
            case NEW_GAME:
                System.out.println("New Game");
                break;
            case CONTINUE_SAVE:
                System.out.println("Load Save");
                break;
            case SETTING:
                System.out.println("Check Setting");
                break;
            case EXIT:
                view.stop();
                return;
            default:
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
        }
    }

}