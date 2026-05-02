package swingy.controller;

import swingy.model.character.Defender;
import swingy.model.character.Fighter;
import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.state.DatabaseQueries;
import swingy.utils.RandomGenerator;
import swingy.view.LoadSaveType;
import swingy.view.PopUpType;
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
            case LOAD_GAME -> {
                Hero[] saves = DatabaseQueries.loadAllHeroStates();
                view.loadGamePage(this::onLoadChoice, saves, LoadSaveType.LOAD);
            }
            case SETTING -> view.settingPage(this::onSettingChoice);
            case EXIT -> view.stop();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }

    private void onLoadChoice(SaveSlotChoice choice) {
        switch (choice) {
            case SLOT_1, SLOT_2, SLOT_3 -> {
                Hero hero = DatabaseQueries.loadHero(choice);
                if (hero == null) {
                    view.showError("Load Error", "Failed to load slot " + choice.getChoiceNumber());
                    Hero[] saves = DatabaseQueries.loadAllHeroStates();
                    view.loadGamePage(this::onLoadChoice, saves, LoadSaveType.LOAD);
                    return;
                }
                this.hero = hero;
                GameMap gameMap = DatabaseQueries.loadMap(choice);
                if (gameMap == null) {
                    view.showError("Load Error", "Failed to load slot " + choice.getChoiceNumber());
                    Hero[] saves = DatabaseQueries.loadAllHeroStates();
                    view.loadGamePage(this::onLoadChoice, saves, LoadSaveType.LOAD);
                    return;
                }
                this.gameMap = gameMap;
                view.inGamePage(
                        this::onInGameChoice,
                        this::onBattleChoice,
                        this::onWinChoice,
                        this::onDefeatChoice,
                        this.hero,
                        this.gameMap,
                        PopUpType.NONE);
            }
            case CLEAR_SAVE -> {
                DatabaseQueries.clear();
                Hero[] saves = DatabaseQueries.loadAllHeroStates();
                view.loadGamePage(this::onLoadChoice, saves, LoadSaveType.LOAD);
            }
            case BACK -> this.start();
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
        view.inGamePage(
                this::onInGameChoice,
                this::onBattleChoice,
                this::onWinChoice,
                this::onDefeatChoice,
                this.hero,
                this.gameMap,
                PopUpType.NONE);
    }

    private void onInGameChoice(InGameChoice choice) {
        if (this.hero == null || this.gameMap == null) {
            return;
        }
        switch (choice) {
            case UP:
                gameMap.moveHero(0, -1);
                break;
            case DOWN:
                gameMap.moveHero(0, 1);
                break;
            case LEFT:
                gameMap.moveHero(-1, 0);
                break;
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
        if (gameMap.isEncounter()) {
            view.inGamePage(
                    this::onInGameChoice,
                    this::onBattleChoice,
                    this::onWinChoice,
                    this::onDefeatChoice,
                    this.hero,
                    this.gameMap,
                    PopUpType.BATTLE);
            return;
        }
        view.inGamePage(
                this::onInGameChoice,
                this::onBattleChoice,
                this::onWinChoice,
                this::onDefeatChoice,
                this.hero,
                this.gameMap,
                PopUpType.NONE);
    }

    private void onInGameSettingChoice(InGameSettingChoice choice) {
        if (this.hero == null || this.gameMap == null) {
            return;
        }
        switch (choice) {
            case SWITCH_VIEW -> System.out.println("Switch view");
            case SAVE_GAME -> {
                Hero[] saves = DatabaseQueries.loadAllHeroStates();
                view.loadGamePage(this::onSaveChoice, saves, LoadSaveType.SAVE);
            }
            case MAIN_MENU -> {
                DatabaseQueries.save(this.hero, this.gameMap, SaveSlotChoice.SLOT_3);
                this.start();
            }
            case BACK -> view.inGamePage(
                    this::onInGameChoice,
                    this::onBattleChoice,
                    this::onWinChoice,
                    this::onDefeatChoice,
                    this.hero,
                    this.gameMap,
                    PopUpType.NONE);
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }

    private void onSaveChoice(SaveSlotChoice choice) {
        if (this.hero == null || this.gameMap == null) {
            return;
        }
        switch (choice) {
            case SLOT_1, SLOT_2, SLOT_3 -> {
                DatabaseQueries.save(this.hero, this.gameMap, choice);
                view.inGamePage(
                        this::onInGameChoice,
                        this::onBattleChoice,
                        this::onWinChoice,
                        this::onDefeatChoice,
                        this.hero,
                        this.gameMap,
                        PopUpType.NONE);
            }
            case CLEAR_SAVE -> {
                DatabaseQueries.clear();
                Hero[] saves = DatabaseQueries.loadAllHeroStates();
                view.loadGamePage(this::onLoadChoice, saves, LoadSaveType.SAVE);
            }
            case BACK -> view.inGamePage(
                    this::onInGameChoice,
                    this::onBattleChoice,
                    this::onWinChoice,
                    this::onDefeatChoice,
                    this.hero,
                    this.gameMap,
                    PopUpType.NONE);
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }

    private void onBattleChoice(BattleChoice choice) {
        if (this.hero == null || this.gameMap == null) {
            return;
        }
        switch (choice) {
            case RUN -> {
                int isSuccess = RandomGenerator.getInstance().nextInt(2);
                switch (isSuccess) {
                    case 0 -> onBattleChoice(BattleChoice.ATTACK);
                    case 1 -> {
                        this.gameMap.resetPrevPosition();
                        view.inGamePage(
                                this::onInGameChoice,
                                this::onBattleChoice,
                                this::onWinChoice,
                                this::onDefeatChoice,
                                this.hero,
                                this.gameMap,
                                PopUpType.NONE);
                    }
                }
            }
            case ATTACK -> {
                int villainLevel = gameMap.isWin(hero);
                if (villainLevel > 0) {
                    view.inGamePage(
                            this::onInGameChoice,
                            this::onBattleChoice,
                            this::onWinChoice,
                            this::onDefeatChoice,
                            this.hero,
                            this.gameMap,
                            PopUpType.WIN);
                } else {
                    view.inGamePage(
                            this::onInGameChoice,
                            this::onBattleChoice,
                            this::onWinChoice,
                            this::onDefeatChoice,
                            this.hero,
                            this.gameMap,
                            PopUpType.DEFEAT);
                }
            }
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }

    private void onWinChoice(WinChoice choice) {
        if (this.hero == null || this.gameMap == null) {
            return;
        }
        switch (choice) {
            case TAKE:
                gameMap.equipDroppedArtifact(this.hero);
                break;
            case DISCARD:
                gameMap.clearDroppedArtifact();
                break;
            default:
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
        }
        view.inGamePage(
                this::onInGameChoice,
                this::onBattleChoice,
                this::onWinChoice,
                this::onDefeatChoice,
                this.hero,
                this.gameMap,
                PopUpType.NONE);
    }

    private void onDefeatChoice(DefeatChoice choice) {
        if (this.hero == null || this.gameMap == null) {
            return;
        }
        switch (choice) {
            case MAIN_MENU -> {
                this.hero = null;
                this.gameMap = null;
                this.start();
            }
            case EXIT -> view.stop();
            default -> {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid choice. Program terminated");
                view.stop();
                System.exit(-1);
            }
        }
    }
}
