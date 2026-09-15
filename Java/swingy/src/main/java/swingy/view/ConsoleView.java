package swingy.view;

import swingy.model.PlayerName;
import swingy.model.ConsoleChoice;
import swingy.model.artifact.Artifact;
import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.villain.Villain;
import swingy.utils.Colors;
import swingy.utils.ValidatorClient;
import swingy.view.game_menu.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import swingy.view.game_menu.Menu;

import java.awt.*;
import java.util.Map;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public class ConsoleView implements View {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
        System.out.println(Colors.CYAN +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "_________________________________________________________________\n" +
            "\n" +
            "   _____ ________     _________       .__  \n" +
            "  /  |  |\\_____  \\   /   _____/_  _  _|__| ____    ____ ___.__.\n" +
            " /   |  |_/  ____/   \\_____  \\\\ \\/ \\/ /  |/    \\  / ___<   |  |\n" +
            "/    ^   /       \\   /        \\\\     /|  |   |  \\/ /_/  >___  |\n" +
            "\\____   |\\_______ \\ /_______  / \\/\\_/ |__|___|  /\\___  // ____|\n" +
            "     |__|        \\/         \\/                \\//_____/ \\/     \n" +
            "\n" +
            "42 Swingy made by Nguyen NGUYEN (hoannguy) from 42 Lausanne\n" +
            "_________________________________________________________________\n" +
            "\n" +
            "\n" +
            Colors.RESET
        );
    }

    @Override
    public void startPage(Consumer<MainMenuChoice> onChoice) {
        System.out.println(Colors.YELLOW + "WELCOME BRAVE ADVENTURER!\n" +
        "What do you want to do?" + Colors.RESET);
        showMenu(MainMenuChoice.class);
        onChoice.accept(readInput(MainMenuChoice.class));
    }

    @Override
    public void settingPage(Consumer<SettingMenuChoice> onChoice) {
        System.out.println(Colors.YELLOW + "SETTING\n" +
        "What do you want to do?" + Colors.RESET);
        showMenu(SettingMenuChoice.class);
        onChoice.accept(readInput(SettingMenuChoice.class));
    }

    @Override
    public void newGamePage(BiConsumer<HeroClassChoice, String> onChoice) {
        System.out.println(Colors.YELLOW +"NEW GAME\n" +
        "Who are you?" + Colors.RESET);
        showMenu(HeroClassChoice.class);
        onChoice.accept(readInput(HeroClassChoice.class), getPlayerName());
    }

    @Override
    public void loadGamePage(Consumer<SaveSlotChoice> onChoice, Hero[] saves, LoadSaveType mode) {
        if (mode == LoadSaveType.LOAD) {
            System.out.println(Colors.YELLOW + "LOAD GAME\n" +
            "Which save do you want to load? Slot 3 is reserved" + Colors.RESET);
        } else if (mode == LoadSaveType.SAVE) {
            System.out.println(Colors.YELLOW + "SAVE GAME\n" +
            "Which slot do you want to overwrite?" + Colors.RESET);
        }
        showLoadSaveSlot(saves, mode);
        System.out.println(Colors.CYAN + "( 4 ) " + Colors.RESET + "Clear saves");
        System.out.println(Colors.CYAN + "( 5 ) " + Colors.RESET + "Back");
        SaveSlotChoice choice = readInput(SaveSlotChoice.class);
        switch (choice) {
            case CLEAR_SAVE, BACK -> {
                onChoice.accept(choice);
                return;
            }
        }
        if (mode == LoadSaveType.LOAD) {
            switch (choice) {
                case SLOT_1, SLOT_2, SLOT_3 -> {
                    if (saves[choice.ordinal()] == null) {
                        System.err.println(Colors.RED + "Error: " + Colors.RESET + "Can not load empty slot");
                        loadGamePage(onChoice, saves, mode);
                        return;
                    }
                    onChoice.accept(choice);
                }
            }
        } else if (mode == LoadSaveType.SAVE) {
            switch (choice) {
                case SLOT_1, SLOT_2 -> onChoice.accept(choice);
                case SLOT_3 -> {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + "Slot 3 is reserved");
                    loadGamePage(onChoice, saves, mode);
                }
            }
        } else {
            onChoice.accept(choice);
        }
    }

    @Override
    public void inGamePage(
            Consumer<InGameChoice> onInGameChoice,
            Consumer<BattleChoice> onBattleChoice,
            Consumer<WinChoice> onWinChoice,
            Consumer<DefeatChoice> onDefeatChoice,
            Hero hero,
            GameMap gameMap,
            PopUpType popUpType) {
        printMap(gameMap);
        printHeroStats(hero);
        if (popUpType == PopUpType.BATTLE) {
            Villain villain = gameMap.getVillainAtHeroPosition();
            showBattlePrompt(hero, villain, onBattleChoice);
        } else if (popUpType == PopUpType.WIN) {
            showWinPrompt(onInGameChoice,
                    onBattleChoice,
                    onWinChoice,
                    onDefeatChoice,
                    hero,
                    gameMap);
        } else if (popUpType == PopUpType.DEFEAT) {
            showDefeatPrompt(hero, onDefeatChoice);
        } else {
            showMenu(InGameChoice.class);
            onInGameChoice.accept(readInput(InGameChoice.class));
        }
    }

    private void printMap(GameMap gameMap) {
        int size = gameMap.getSize();
        int[] heroPos = gameMap.getHeroPosition();
        Map<String, Villain> villains = gameMap.getVillains();

        System.out.println(Colors.YELLOW + "\n[ LEVEL " + gameMap.getLevel() + " ]\n" + Colors.RESET);
        System.out.print("   ");
        for (int col = 1; col <= size; col++) {
            System.out.printf("%2d ", col);
        }
        System.out.println();

        for (int row = 1; row <= size; row++) {
            System.out.printf("%2d ", row);
            for (int col = 1; col <= size; col++) {
                boolean isHero = heroPos[0] == col && heroPos[1] == row;
                boolean isVillain = villains.containsKey(col + "," + row);

                if (isHero) {
                    System.out.print(Colors.CYAN + "[H]" + Colors.RESET);
                } else if (isVillain) {
                    String className = villains.get(col + "," + row).getClassName();
                    if (className.equals("Skeleton")) {
                        System.out.print(Colors.RED + "[S]" + Colors.RESET);
                    } else if (className.equals("Goblin")) {
                        System.out.print(Colors.RED + "[G]" + Colors.RESET);
                    } else {
                        System.out.print(Colors.RED + "[V]" + Colors.RESET);
                    }
                } else {
                    System.out.print("[ ]");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printHeroStats(Hero hero) {
        int expNeeded = (int)(hero.getLevel() * 1000 + Math.pow(hero.getLevel() - 1, 2) * 450);
        System.out.println(Colors.YELLOW + "═══════════════════════════════" + Colors.RESET);
        System.out.println(Colors.CYAN + hero.getName() + Colors.RESET + " (" + hero.getClassName() + ")");
        System.out.println(Colors.BLUE + "LVL: " + Colors.RESET + hero.getLevel() +
                Colors.BLUE + "  XP: " + Colors.RESET + hero.getExperience() + "/" + expNeeded);
        System.out.println(Colors.BLUE + "ATK: " + Colors.RESET + hero.getAttack() +
                Colors.BLUE + "  DEF: " + Colors.RESET + hero.getDefense() +
                Colors.BLUE + "  HP: " + Colors.RESET + hero.getHitPoints() +
                Colors.BLUE + "  CRIT: " + Colors.RESET + hero.getCrit() + "%");
        System.out.println(Colors.YELLOW + "═══════════════════════════════" + Colors.RESET);
    }

    private void showBattlePrompt(Hero hero, Villain villain, Consumer<BattleChoice> onBattleChoice) {
        System.out.println(Colors.PURPLE + "\n  ENCOUNTER!" + Colors.RESET);
        System.out.println();
        System.out.println(
                Colors.CYAN + "[ " + hero.getName() + " ]" + Colors.RESET + "\t\t" +
                Colors.PURPLE + "[ " + villain.getClassName().toUpperCase() + " ]" + Colors.RESET);
        System.out.println("ATK:  " + hero.getAttack() + "\t\t" + "ATK:  " + villain.getAttack());
        System.out.println("DEF:  " + hero.getDefense() + "\t\t" +"DEF:  " + villain.getDefense());
        System.out.println("HP:   " + hero.getHitPoints() + "\t\t" + "HP:   " + villain.getHitPoints());
        System.out.println("CRIT: " + hero.getCrit() + "%" + "\t\t" + "CRIT: " + villain.getCrit() + "%");
        System.out.println();
        showMenu(BattleChoice.class);
        onBattleChoice.accept(readInput(BattleChoice.class));
    }

    private void showWinPrompt(Consumer<InGameChoice> onInGameChoice,
                               Consumer<BattleChoice> onBattleChoice,
                               Consumer<WinChoice> onWinChoice,
                               Consumer<DefeatChoice> onDefeatChoice,
                               Hero hero,
                               GameMap gameMap) {
        System.out.println(Colors.CYAN + "\n  VICTORY!" + Colors.RESET);
        Artifact artifact = gameMap.getDroppedArtifact();
        if (artifact == null) {
            System.out.println("No artifact dropped.");
            inGamePage(onInGameChoice,
                    onBattleChoice,
                    onWinChoice,
                    onDefeatChoice,
                    hero,
                    gameMap,
                    PopUpType.NONE);
            return;
        }
        System.out.println(Colors.YELLOW + "Artifact dropped: " + artifact.getClassName() + Colors.RESET);
        if (artifact.getAttack() > 0) {
            System.out.println(Colors.BLUE + "ATK: " + Colors.RESET + "+" + artifact.getAttack());
        }
        if (artifact.getDefense() > 0) {
            System.out.println(Colors.BLUE + "DEF: " + Colors.RESET + "+" + artifact.getDefense());
        }
        if (artifact.getHitPoints() > 0) {
            System.out.println(Colors.BLUE + "HP:  " + Colors.RESET + "+" + artifact.getHitPoints());
        }
        System.out.println();
        showMenu(WinChoice.class);
        onWinChoice.accept(readInput(WinChoice.class));
    }

    private void showDefeatPrompt(Hero hero, Consumer<DefeatChoice> onDefeatChoice) {
        System.out.println(Colors.RED + "\n  DEFEATED..." + Colors.RESET);
        System.out.println(hero.getName() + " has fallen.");
        System.out.println();
        showMenu(DefeatChoice.class);
        onDefeatChoice.accept(readInput(DefeatChoice.class));
    }

    @Override
    public void inGameSettingPage(Consumer<InGameSettingChoice> onChoice) {
        System.out.println(Colors.YELLOW + "SETTING\n" +
                "What do you want to do?" + Colors.RESET);
        showMenu(InGameSettingChoice.class);
        onChoice.accept(readInput(InGameSettingChoice.class));
    }

    @Override
    public void stop() {
        System.out.println(Colors.YELLOW + "Console view stopped" + Colors.RESET);
    }

    @Override
    public void showError(String title, String message) {
        System.err.println(Colors.RED + title + ": " + Colors.RESET + message);
    }

    private <T extends Enum<T> & Menu> void showMenu(Class<T> enumClass) {
        T[] choices = enumClass.getEnumConstants();
        for (T choice : choices) {
            System.out.println(Colors.CYAN +
            "( " +
            (choice.ordinal() + 1) +
            " ) " +
            Colors.RESET +
            choice.getDescription());
        }
    }

    private <T extends Enum<T> & Menu> T readInput(Class<T> enumClass) {
        T[] choices = enumClass.getEnumConstants();
        Validator validator = ValidatorClient.getValidator();
        while (true) {
            try {
                System.out.print("\nChoice: ");
                int input = Integer.parseInt(scanner.nextLine().trim());
                ConsoleChoice choice = new ConsoleChoice(input);
                Set<ConstraintViolation<ConsoleChoice>> violations = validator.validate(choice);
                if (!violations.isEmpty()) {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + violations.iterator().next().getMessage());
                    continue;
                }
                if (input > choices.length) {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + "Choice must be between 1 and " + choices.length);
                    continue;
                }
                System.out.println();
                return choices[input - 1];
            } catch (NumberFormatException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Does adventurer not know how to read? Please enter valid choice value.");
            } catch (NoSuchElementException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Something went wrong.");
                this.stop();
                System.exit(0);
            } catch (IllegalStateException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Bad programmer! I am shutting down");
                System.exit(-1);
            }
        }
    }

    private String getPlayerName() {
        Validator validator = ValidatorClient.getValidator();
        while (true) {
            System.out.println("What is your name?");
            String input = scanner.nextLine();
            PlayerName playerName = new PlayerName(input);
            Set<ConstraintViolation<PlayerName>> constraintViolations = validator.validate(playerName);
            if (!constraintViolations.isEmpty()) {
                System.err.println(Colors.RED + "Invalid className" + Colors.RESET + ": " + constraintViolations.iterator().next().getMessage());
                System.out.println();
                continue;
            }
            return input;
        }
    }

    private void showLoadSaveSlot(Hero[] saves, LoadSaveType mode) {
        for (int i = 1; i < 4; i++) {
            if (mode == LoadSaveType.SAVE && i == 3) {
                continue;
            }
            Hero save = (saves != null && saves.length >= i) ? saves[i - 1] : null;
            if (save == null) {
                System.out.println(Colors.CYAN +
                    "( " + i + " ) " +
                    Colors.RESET +
                    "Empty slot"
                );
                continue;
            }
            System.out.println(Colors.CYAN +
                "( " + i + " ) " +
                Colors.RESET +
                save.getClassName() + "\t- " +
                Colors.BLUE + "name: " + Colors.RESET +
                save.getName() + ", " +
                Colors.BLUE + "Lvl: " + Colors.RESET +
                save.getLevel() + ", " +
                Colors.BLUE + "Exp: " + Colors.RESET +
                save.getExperience() +  ", " +
                Colors.BLUE + "Atk: " + Colors.RESET +
                save.getAttack() +  ", " +
                Colors.BLUE + "Def: " + Colors.RESET +
                save.getDefense() +  ", " +
                Colors.BLUE + "Hp: " + Colors.RESET +
                save.getHitPoints()
            );
        }
    }
}
