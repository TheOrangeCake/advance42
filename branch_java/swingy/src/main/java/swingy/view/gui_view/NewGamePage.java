package swingy.view.gui_view;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import swingy.model.PlayerName;
import swingy.utils.FileLoader;
import swingy.utils.ValidatorClient;
import swingy.view.game_menu.HeroClassChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class NewGamePage {
    private static final String[] STAT_LABELS = {"LVL", "EXP", "ATK", "DEF", "HP", "CRIT"};
    private static final Color[] STAT_COLORS = {
            new Color(180, 180, 255),
            new Color(255, 210, 100),
            new Color(255,  90,  90),
            new Color( 80, 200, 255),
            new Color(100, 220, 120),
            new Color(255, 160,  60),
    };

    public void displayPage(BiConsumer<HeroClassChoice, String> onChoice, JFrame frame) {
        Validator validator = ValidatorClient.getValidator();

        JPanel root = new JPanel();
        root.setBackground(Color.BLACK);
        root.setLayout(new GridBagLayout());
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel pageTitle = new JLabel("Create New Hero");
        pageTitle.setForeground(Color.WHITE);
        pageTitle.setFont(Typography.H2.getTypography());
        pageTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(pageTitle);
        root.add(Box.createVerticalStrut(24));

        // Name row (label + field)
        JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        nameRow.setOpaque(false);

        JLabel nameLabel = new JLabel("Hero className:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(Typography.H5.getTypography());

        JTextField nameField = new JTextField(20);
        nameField.setForeground(Color.BLACK);
        nameField.setFont(Typography.H4.getTypography());
        nameField.setHorizontalAlignment(SwingConstants.CENTER);

        nameRow.add(nameLabel);
        nameRow.add(nameField);
        root.add(nameRow);
        root.add(Box.createVerticalStrut(12));

        // Class cards
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        cardsPanel.setOpaque(false);

        HeroClassChoice[] selectedChoice = {null};
        List<JPanel> cards = new ArrayList<>();

        Color cardDefault    = new Color(40, 40, 60);
        Color cardSelected   = new Color(80, 60, 130);

        for (HeroClassChoice choice : HeroClassChoice.values()) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(cardDefault);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(90, 90, 120), 2, true),
                    BorderFactory.createEmptyBorder(16, 20, 16, 20)
            ));
            card.setPreferredSize(new Dimension(300, 400));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Hero image
            ImageIcon icon = FileLoader.loadScaledImage(choice.getImage(), 148, 148);
            JLabel imageLabel = new JLabel(icon != null ? icon : new ImageIcon());
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(imageLabel);
            card.add(Box.createVerticalStrut(8));

            // Class className
            JLabel classNameLabel = new JLabel(choice.getClassName().toUpperCase());
            classNameLabel.setForeground(Color.WHITE);
            classNameLabel.setFont(Typography.H3.getTypography().deriveFont(Font.BOLD, 15f));
            classNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(classNameLabel);
            card.add(Box.createVerticalStrut(8));

            JSeparator divider = new JSeparator();
            divider.setForeground(new Color(160, 160, 200));
            divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            card.add(divider);
            card.add(Box.createVerticalStrut(12));

            // Stat rows
            int[] stats = choice.getBaseStats();
            for (int i = 0; i < STAT_LABELS.length; i++) {
                card.add(buildStatRow(STAT_LABELS[i], stats[i], STAT_COLORS[i]));
                card.add(Box.createVerticalStrut(4));
            }

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedChoice[0] = choice;
                    for (JPanel c : cards) {
                        c.setBackground(cardDefault);
                        c.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(90, 90, 120), 2, true),
                                BorderFactory.createEmptyBorder(16, 20, 16, 20)
                        ));
                    }
                    card.setBackground(cardSelected);
                    card.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(180, 130, 255), 2, true),
                            BorderFactory.createEmptyBorder(16, 20, 16, 20)
                    ));
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (selectedChoice[0] != choice)
                        card.setBackground(new Color(55, 50, 80));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (selectedChoice[0] != choice)
                        card.setBackground(cardDefault);
                }
            });

            cards.add(card);
            cardsPanel.add(card);
        }
        root.add(cardsPanel);
        root.add(Box.createVerticalStrut(24));

        // Submit button
        swingy.view.ui.Button submitButton = new Button("Create Hero");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(Typography.H5.getTypography());
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addActionListener(_ -> {
            PlayerName playerName = new PlayerName(nameField.getText());
            Set<ConstraintViolation<PlayerName>> violations = validator.validate(playerName);

            boolean nameOk  = violations.isEmpty();
            boolean classOk = selectedChoice[0] != null;
            if (nameOk && classOk) {
                errorLabel.setText(" ");
                onChoice.accept(selectedChoice[0], playerName.getPlayerName());
            } else {
                if (!nameOk && !classOk) {
                    errorLabel.setText("Please enter a valid className and select a class");
                } else if (!nameOk) {
                    errorLabel.setText("Please enter a valid hero className: " + violations.iterator().next().getMessage());
                } else {
                    errorLabel.setText("Please select a hero class");
                }
            }
        });

        root.add(submitButton);
        root.add(Box.createVerticalStrut(6));
        root.add(errorLabel);
        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    private JPanel buildStatRow(String label, int value, Color color) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel keyLabel = new JLabel(label);
        keyLabel.setForeground(color);
        keyLabel.setFont(Typography.H5.getTypography());

        JLabel dots = new JLabel();
        dots.setForeground(new Color(160, 160, 200));
        dots.setFont(Typography.H5.getTypography());
        dots.setText("......................");
        dots.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(Typography.H5.getTypography());
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(keyLabel,   BorderLayout.WEST);
        row.add(dots,       BorderLayout.CENTER);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }
}