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

    public GuiView(int height, int width) {
        this.frame = new JFrame();
        frame.setSize(width, height);
        frame.setTitle("42 Swingy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void startPage(Consumer<MainMenuChoice> onChoice) {
        JPanel panel = setLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel gameTitle = new JLabel("42 Swingy");
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setFont(Typography.GAME_TITLE.getTypography());
        gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(gameTitle, gbc);

        JLabel divider = new JLabel("*-*-*-*-*-*-*-*-*");
        divider.setForeground(Color.WHITE);
        divider.setFont(Typography.PARAGRAPH.getTypography());
        divider.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(divider, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        for (MainMenuChoice choice : MainMenuChoice.values()) {
            Button button = new Button(choice.getDescription());
            button.addActionListener(_ -> onChoice.accept(choice));
            gbc.gridy++;
            panel.add(button, gbc);
        }
        repaintPage(panel);
    }

    @Override
    public void settingPage(Consumer<SettingMenuChoice> onChoice) {
        JPanel panel = setLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel gameTitle = new JLabel("Setting");
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setFont(Typography.H1.getTypography());
        gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(gameTitle, gbc);

        JLabel about = new JLabel("42 Swingy made by Nguyen NGUYEN (hoannguy) from 42 Lausanne");
        about.setForeground(Color.WHITE);
        about.setFont(Typography.H5.getTypography());
        about.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 8, 0);
        panel.add(about, gbc);

        JLabel divider2 = new JLabel("*-*-*-*-*-*-*-*-*");
        divider2.setForeground(Color.WHITE);
        divider2.setFont(Typography.PARAGRAPH.getTypography());
        divider2.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 40, 0);
        panel.add(divider2, gbc);

        gbc.insets = new Insets(12, 0, 12, 0);
        for (SettingMenuChoice choice : SettingMenuChoice.values()) {
            Button button = new Button(choice.getDescription());
            button.addActionListener(_ -> onChoice.accept(choice));
            gbc.gridy++;
            panel.add(button, gbc);
        }
        repaintPage(panel);
    }

    @Override
    public void newGamePage(BiConsumer<HeroClassChoice, String> onChoice) {
        JPanel panel = setLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        repaintPage(panel);
    }

    @Override
    public void stop() {
        frame.dispose();
    }

    private JPanel setLayout() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new GridBagLayout());
        return panel;
    }

    private void repaintPage(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
