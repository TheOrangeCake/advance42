package swingy.view.gui_view;

import swingy.view.game_menu.SettingMenuChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SettingPage {
    public void displayPage(Consumer<SettingMenuChoice> onChoice, JFrame frame) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel pageTitle = new JLabel("Setting");
        pageTitle.setForeground(Color.WHITE);
        pageTitle.setFont(Typography.H1.getTypography());
        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(pageTitle, gbc);

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
