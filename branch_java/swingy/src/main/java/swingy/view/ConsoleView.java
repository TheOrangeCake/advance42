package swingy.view;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import swingy.utils.Colors;
import swingy.utils.game_menu.MainMenuChoice;
import swingy.model.character.Hero;
import swingy.model.character.Warrior;

public class ConsoleView implements View {
    private Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public MainMenuChoice start() {
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
        for (MainMenuChoice choice : MainMenuChoice.values()) {
            System.out.println(Colors.CYAN +
                "( " +
                (choice.ordinal() + 1) +
                " ) " +
                Colors.RESET +
                choice.getDescription());
        }
        while (true) {
            try {
                System.out.print("\nChoice: ");
                int input = scanner.nextInt();
                if (input > 0 && input <= MainMenuChoice.values().length) {
                    scanner.nextLine();
                    return MainMenuChoice.values()[input - 1];
                } else {
                    throw new InputMismatchException();
                }
            } catch (InputMismatchException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Does adventurer not know how to read? Please enter valid choice value.");
                scanner.nextLine();
            } catch (NoSuchElementException e) {
                return MainMenuChoice.EXIT;
            } catch (IllegalStateException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Bad programmer! I am shutting down");
                System.exit(-1);
            }
        }
    }

    @Override
    public void stop() {
        scanner.close();
    }
}