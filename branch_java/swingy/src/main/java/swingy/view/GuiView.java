package swingy.view;


import swingy.view.game_menu.HeroClassChoice;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.swing.*;
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
        container.setBackground(Color.BLACK);
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel gameTitle = new JLabel("42 Swingy");
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setFont(Typography.GAME_TITLE.getTypography());
        gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        container.add(gameTitle, gbc);

        JLabel divider = new JLabel("__________");
        divider.setForeground(Color.WHITE);
        divider.setFont(Typography.PARAGRAPH.getTypography());
        divider.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 40, 0);
        container.add(divider, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        for (MainMenuChoice choice : MainMenuChoice.values()) {
            Button button = new Button(choice.getDescription());
            button.addActionListener(ae -> onChoice.accept(choice));
            gbc.gridy++;
            container.add(button, gbc);
        }

        frame.setSize(this.width, this.height);
        frame.setTitle("42 Swingy");
        frame.setContentPane(container);
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
