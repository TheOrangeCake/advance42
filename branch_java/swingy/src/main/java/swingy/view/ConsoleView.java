 package swingy.view;

 import swingy.model.PlayerName;
 import swingy.model.ConsoleChoice;
 import swingy.model.character.Hero;
 import swingy.model.map.GameMap;
 import swingy.model.state.DatabaseConfig;
 import swingy.model.state.HeroState;
 import swingy.utils.Colors;
 import swingy.utils.ValidatorClient;
 import swingy.view.game_menu.*;
 import jakarta.validation.ConstraintViolation;
 import jakarta.validation.Validator;

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
             Colors.RESET);
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
         System.out.println(Colors.YELLOW + "SETTING\n" +
                 "Who are you?" + Colors.RESET);
         showMenu(HeroClassChoice.class);
         onChoice.accept(readInput(HeroClassChoice.class), getPlayerName());
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

     }

     @Override
     public void inGameSettingPage(Consumer<InGameSettingChoice> onChoice) {

     }

     @Override
     public void loadGamePage(Consumer<SaveSlotChoice> onChoice, Hero[] saves, LoadSaveType mode) {

     }

     @Override
     public void stop() {
         scanner.close();
         DatabaseConfig.close();
         System.out.println(Colors.YELLOW + "Fantasy over! Get back to work!" + Colors.RESET);
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
             System.out.println("What is your className?");
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
 }
