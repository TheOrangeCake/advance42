package swingy.view;

import swingy.model.character.Hero;
import swingy.model.map.Map;
import swingy.view.game_menu.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface View {
    void startPage(Consumer<MainMenuChoice> onChoice);
    void settingPage(Consumer<SettingMenuChoice> onChoice);
    void newGamePage(BiConsumer<HeroClassChoice, String> onChoice);
    void inGamePage(Consumer<InGameChoice> onChoice, Hero hero, Map map);
    void inGameSettingPage(Consumer<InGameSettingChoice> onChoice);
    void stop();
}