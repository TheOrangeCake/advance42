package swingy.view.gui_view;

import swingy.model.character.Hero;
import swingy.model.map.Map;
import swingy.view.game_menu.InGameChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class InGamePage {
    public void displayPage(Consumer<InGameChoice> onChoice, JFrame frame, Hero hero, Map map) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel pageTitle = new JLabel("Level " + map.getLevel());
        pageTitle.setForeground(Color.WHITE);
        pageTitle.setFont(Typography.H3.getTypography());
        pageTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(pageTitle, gbc);


        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }
}
