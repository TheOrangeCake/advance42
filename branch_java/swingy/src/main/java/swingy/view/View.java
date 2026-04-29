package swingy.view;

import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.state.HeroState;
import swingy.view.game_menu.*;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface View {
    void startPage(Consumer<MainMenuChoice> onChoice);
    void settingPage(Consumer<SettingMenuChoice> onChoice);
    void newGamePage(BiConsumer<HeroClassChoice, String> onChoice);
    void inGamePage(
            Consumer<InGameChoice> onInGameChoice,
            Consumer<BattleChoice> onBattleChoice,
            Consumer<WinChoice> onWinChoice,
            Consumer<DefeatChoice> onDefeatChoice,
            Consumer<SaveSlotChoice> onSaveChoice,
            Hero hero,
            GameMap gameMap,
            PopUpType popUpType);
    void inGameSettingPage(Consumer<InGameSettingChoice> onChoice);
    void loadGamePage(Consumer<SaveSlotChoice> onChoice, HeroState[] saves);
    void stop();
}