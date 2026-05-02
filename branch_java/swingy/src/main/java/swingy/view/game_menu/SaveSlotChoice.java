package swingy.view.game_menu;

public enum SaveSlotChoice implements Menu {
    SLOT_1("1"),
    SLOT_2("2"),
    SLOT_3("3"),
    CLEAR_SAVE("4"),
    BACK("5");

    final String choiceNumber;

    SaveSlotChoice(String choiceNumber) {
        this.choiceNumber = choiceNumber;
    }

    public String getDescription() {
        return this.choiceNumber;
    }
}
