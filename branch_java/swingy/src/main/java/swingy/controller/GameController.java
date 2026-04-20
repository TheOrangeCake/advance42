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
            case NEW_GAME -> System.out.println("New Game");
            case CONTINUE_SAVE -> System.out.println("Load Save");
            case SETTING -> view.settingPage(this::onSettingChoice);
            case EXIT -> view.stop();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
            }
        }
    }

    private void onSettingChoice(SettingMenuChoice choice) {
        switch (choice) {
            case SWITCH_VIEW -> System.out.println("Switch view");
            case BACK -> start();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
            }
        }
    }

}