package swingy.view;

import swingy.model.character.Hero;
import swingy.utils.game_menu.MainMenu;
import swingy.utils.game_menu.SettingMenu;

public interface View {
    public MainMenu start();
    public SettingMenu setting();
    public void stop();
}