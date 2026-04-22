package swingy.view.gui_view;

import swingy.view.View;
import swingy.view.game_menu.HeroClassChoice;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiView implements View {
    private final JFrame frame;
    private final StartPage startPage;
    private final NewGamePage newGamePage;
    private final SettingPage settingPage;

    public GuiView(int height, int width) {
        this.frame = new JFrame();
        this.startPage = new StartPage();
        this.newGamePage = new NewGamePage();
        this.settingPage = new SettingPage();
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
    public void stop() {
        frame.dispose();
    }
}
