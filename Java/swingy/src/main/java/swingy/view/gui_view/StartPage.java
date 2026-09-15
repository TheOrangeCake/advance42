package swingy.view.gui_view;

import swingy.view.game_menu.MainMenuChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class StartPage {
    public void displayPage(Consumer<MainMenuChoice> onChoice, JFrame frame) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new GridBagLayout());
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
            swingy.view.ui.Button button = new Button(choice.getDescription());
            button.addActionListener(_ -> onChoice.accept(choice));
            gbc.gridy++;
            panel.add(button, gbc);
        }
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
