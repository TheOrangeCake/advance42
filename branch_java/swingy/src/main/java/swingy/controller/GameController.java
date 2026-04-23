package swingy.controller;

import swingy.model.artifact.Artifact;
import swingy.model.character.Defender;
import swingy.model.character.Fighter;
import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.view.View;
import swingy.view.game_menu.*;
import swingy.utils.Colors;

public class GameController {
    private final View view;
    private Hero hero = null;
    private GameMap gameMap = null;

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
                System.exit(-1);
            }
        }
    }

    private void onSettingChoice(SettingMenuChoice choice) {
        switch (choice) {
            case SWITCH_VIEW -> System.out.println("Switch view");
            case BACK -> this.start();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }

    private void onNewGameChoice(HeroClassChoice choice, String playerName) {
        switch (choice) {
            case DEFENDER:
                this.hero = new Defender(playerName);
                break;
            case FIGHTER:
                this.hero = new Fighter(playerName);
                break;
            default:
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
        }
        this.gameMap = new GameMap(1);
        view.inGamePage(this::onInGameChoice, this.hero, this.gameMap);
    }

    private void onInGameChoice(InGameChoice choice) {
        switch (choice) {
            case UP:
                gameMap.moveHero(0, 1);
                break;
            case DOWN:
                gameMap.moveHero(0, -1);
                break;
            case LEFT:
                gameMap.moveHero(-1, 0);
            case RIGHT:
                gameMap.moveHero(1, 0);
                break;
            case SETTING:
                view.inGameSettingPage(this::onInGameSettingChoice);
                return;
            default:
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
        }
        if (gameMap.isCombat()) {
            if (gameMap.isWin()) {
                // draw win popup with a generated artifact and WinChoice
            } else {
                // draw defeat popup with DefeatChoice
            }
        }
        view.inGamePage(this::onInGameChoice, this.hero, this.gameMap);
    }

    private void onInGameSettingChoice(InGameSettingChoice choice) {
        switch (choice) {
            case SWITCH_VIEW -> System.out.println("Switch view");
            case SAVE_GAME -> System.out.println("Save game");
            case MAIN_MENU -> this.start();
            case BACK -> view.inGamePage(this::onInGameChoice, this.hero, this.gameMap);
            case EXIT -> view.stop();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }

    private void onWinChoice(WinChoice choice, Artifact artifact) {
        switch (choice) {
            case TAKE:
                hero.setArtifact(artifact);
                break;
            case DISCARD:
                break;
            default:
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
        }
        view.inGamePage(this::onInGameChoice, this.hero, this.gameMap);
    }

    private void onDefeatChoice(DefeatChoice choice) {
        switch (choice) {
            case MAIN_MENU -> this.start();
            case EXIT -> view.stop();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }
}
