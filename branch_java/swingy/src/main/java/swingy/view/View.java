package swingy.view;

import swingy.model.character.Hero;
import swingy.utils.game_menu.MainMenuChoice;

public interface View {
    public MainMenuChoice start();
    // public void update();
    public void stop();
}