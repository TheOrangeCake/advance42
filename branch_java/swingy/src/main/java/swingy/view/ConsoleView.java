 package swingy.view;

 import java.util.Scanner;
 import java.util.InputMismatchException;
 import java.util.NoSuchElementException;
 import java.util.function.Consumer;
 import swingy.utils.Colors;
 import swingy.view.game_menu.Menu;
 import swingy.view.game_menu.MainMenuChoice;
 import swingy.view.game_menu.SettingMenuChoice;

 public class ConsoleView implements View {
     private final Scanner scanner;

     public ConsoleView() {
         this.scanner = new Scanner(System.in);
     }

     @Override
     public void startPage(Consumer<MainMenuChoice> onChoice) {
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
             "_________________________________________________________________\n" +
             "\n" +
             "\n" +
             Colors.RESET);
         System.out.println(Colors.YELLOW + "WELCOME BRAVE ADVENTURER!\n" +
             "What do you want to do?" + Colors.RESET);
         showMenu(MainMenuChoice.class);
         onChoice.accept(readInput(MainMenuChoice.class));
     }

     @Override
     public void settingPage(Consumer<SettingMenuChoice> onChoice) {
         System.out.println(Colors.YELLOW + "Setting\n" +
             "What do you want to do?" + Colors.RESET);
         showMenu(SettingMenuChoice.class);
         onChoice.accept(readInput(SettingMenuChoice.class));
     }

     @Override
     public void stop() {
         scanner.close();
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
         while (true) {
             try {
                 System.out.print("\nChoice: ");
                 int input = scanner.nextInt();
                 if (input > 0 && input <= choices.length) {
                     scanner.nextLine();
                     System.out.println();
                     return choices[input - 1];
                 } else {
                     throw new InputMismatchException();
                 }
             } catch (InputMismatchException e) {
                 System.err.println(Colors.RED + "Error: " + Colors.RESET + "Does adventurer not know how to read? Please enter valid choice value.");
                 scanner.nextLine();
             } catch (NoSuchElementException e) {
                 this.stop();
                 System.out.println(Colors.YELLOW + "Fantasy over! Get back to work!" + Colors.RESET);
                 System.exit(0);
             } catch (IllegalStateException e) {
                 System.err.println(Colors.RED + "Error: " + Colors.RESET + "Bad programmer! I am shutting down");
                 System.exit(-1);
             }
         }
     }
 }

