package swingy.controller;

import swingy.view.View;
import swingy.view.game_menu.HeroClassChoice;
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
            case NEW_GAME -> view.newGamePage(this::onNewGameChoice);
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

    private void onNewGameChoice(HeroClassChoice choice, String playerName) {
        switch (choice) {
            case DEFENDER -> System.out.println("Defender");
            case FIGHTER -> System.out.println("Fighter");
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
            }
        }
    }

}