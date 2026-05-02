package swingy.view.game_menu;

public enum SaveSlotChoice {
    SLOT_1(1),
    SLOT_2(2),
    SLOT_3(3),
    CLEAR_SAVE(4),
    BACK(5);

    final int choiceNumber;

    SaveSlotChoice(int choiceNumber) {
        this.choiceNumber = choiceNumber;
    }

    public int getChoiceNumber() {
        return this.choiceNumber;
    }
}
