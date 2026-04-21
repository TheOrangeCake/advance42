package swingy.view;

import swingy.view.game_menu.HeroClassChoice;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface View {
    void startPage(Consumer<MainMenuChoice> onChoice);
    void settingPage(Consumer<SettingMenuChoice> onChoice);
    void newGamePage(BiConsumer<HeroClassChoice, String> onChoice);
    void stop();
}