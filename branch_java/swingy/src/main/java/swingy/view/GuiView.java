package swingy.view;


import swingy.view.game_menu.HeroClassChoice;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;
import swingy.view.ui.Button;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiView implements View {
    private final JFrame frame;
    private final int height;
    private final int width;

    public GuiView(int height, int width) {
        this.frame = new JFrame();
        this.height = height;
        this.width = width;
    }

    @Override
    public void startPage(Consumer<MainMenuChoice> onChoice) {
        JPanel container = new JPanel();
        container.setBackground(new Color(0, 0, 0));

        frame.setSize(this.width, this.height);
        frame.setTitle("42 Swingy");
        frame.setContentPane(container);

        for (MainMenuChoice choice : MainMenuChoice.values()) {
            Button button = new Button(choice.getDescription());

            button.addActionListener(ae -> onChoice.accept(choice));
            container.add(button);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void settingPage(Consumer<SettingMenuChoice> onChoice) {

    }

    @Override
    public void newGamePage(BiConsumer<HeroClassChoice, String> onChoice) {
        onChoice.accept(HeroClassChoice.DEFENDER, "");
    }

    @Override
    public void stop() {
        frame.dispose();
    }
}
