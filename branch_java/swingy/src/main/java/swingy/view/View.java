package swingy.view;

import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;

import java.util.function.Consumer;

public interface View {
    public void startPage(Consumer<MainMenuChoice> onChoice);
     public void settingPage(Consumer<SettingMenuChoice> onChoice);
    public void stop();
}