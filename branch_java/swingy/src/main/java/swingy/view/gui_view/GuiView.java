package swingy.view.gui_view;

import org.hibernate.SessionFactory;
import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.state.DatabaseConfig;
import swingy.model.state.HeroState;
import swingy.view.PopUpType;
import swingy.view.View;
import swingy.view.game_menu.*;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiView implements View {
    private final JFrame frame;
    private final StartPage startPage;
    private final NewGamePage newGamePage;
    private final SettingPage settingPage;
    private final InGamePage inGamePage;
    private final InGameSettingPage inGameSettingPage;
    private final LoadGamePage loadGamePage;

    public GuiView(int height, int width) {
        this.frame = new JFrame();
        this.startPage = new StartPage();
        this.newGamePage = new NewGamePage();
        this.settingPage = new SettingPage();
        this.inGamePage = new InGamePage();
        this.inGameSettingPage = new InGameSettingPage();
        this.loadGamePage = new LoadGamePage();
        frame.setSize(width, height);
        frame.setTitle("42 Swingy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void startPage(Consumer<MainMenuChoice> onChoice) {
        startPage.displayPage(onChoice, frame);
    }

    @Override
    public void settingPage(Consumer<SettingMenuChoice> onChoice) {
        settingPage.displayPage(onChoice, frame);
    }

    @Override
    public void newGamePage(BiConsumer<HeroClassChoice, String> onChoice) {
        newGamePage.displayPage(onChoice, frame);
    }

    @Override
    public void inGamePage(
            Consumer<InGameChoice> onInGameChoice,
            Consumer<BattleChoice> onBattleChoice,
            Consumer<WinChoice> onWinChoice,
            Consumer<DefeatChoice> onDefeatChoice,
            Consumer<SaveSlotChoice>  onSaveChoice,
            Hero hero,
            GameMap gameMap,
            PopUpType popUpType) {
        inGamePage.displayPage(
                onInGameChoice,
                onBattleChoice,
                onWinChoice,
                onDefeatChoice,
                onSaveChoice,
                frame,
                hero,
                gameMap,
                popUpType);
    }

    @Override
    public void inGameSettingPage(Consumer<InGameSettingChoice> onChoice) {
        inGameSettingPage.displayPage(onChoice, frame);
    }

    @Override
    public void loadGamePage(Consumer<SaveSlotChoice> onChoice, HeroState[] saves) {
        loadGamePage.displayPage(onChoice, saves, frame);
    }

    @Override
    public void stop() {
        DatabaseConfig.close();
        frame.dispose();
    }
}
