package swingy.view.gui_view;

import swingy.model.character.Hero;
import swingy.utils.FileLoader;
import swingy.view.LoadSaveType;
import swingy.view.game_menu.SaveSlotChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class LoadSaveGamePage {
    private static final String[] STAT_LABELS = {"LVL", "EXP", "ATK", "DEF", "HP", "CRIT"};
    private static final Color[] STAT_COLORS = {
            new Color(180, 180, 255),
            new Color(255, 210, 100),
            new Color(255,  90,  90),
            new Color( 80, 200, 255),
            new Color(100, 220, 120),
            new Color(255, 160,  60),
    };

    private static final SaveSlotChoice[] SLOT_CHOICES = {
            SaveSlotChoice.SLOT_1,
            SaveSlotChoice.SLOT_2,
            SaveSlotChoice.SLOT_3,
    };

    public void displayPage(Consumer<SaveSlotChoice> onChoice,
                            Hero[] saves,
                            JFrame frame,
                            LoadSaveType mode) {
        JPanel root = new JPanel();
        root.setBackground(Color.BLACK);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String label = mode == LoadSaveType.SAVE ? "Save Game" : "Load Game";
        JLabel pageTitle = new JLabel(label);
        pageTitle.setForeground(Color.WHITE);
        pageTitle.setFont(Typography.H2.getTypography());
        pageTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        root.add(pageTitle);
        root.add(Box.createVerticalStrut(24));

        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        cardsPanel.setOpaque(false);

        Color cardDefault  = new Color(40, 40, 60);
        Color cardEmpty    = new Color(30, 30, 45);

        for (int i = 0; i < SLOT_CHOICES.length; i++) {
            final SaveSlotChoice slotChoice = SLOT_CHOICES[i];
            Hero save = (saves != null && saves.length > i) ? saves[i] : null;
            boolean isEmpty = (save == null);

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(isEmpty ? cardEmpty : cardDefault);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(
                            isEmpty ? new Color(60, 60, 80) : new Color(90, 90, 120), 2, true),
                    BorderFactory.createEmptyBorder(16, 20, 16, 20)
            ));
            card.setPreferredSize(new Dimension(300, 400));
            card.setCursor(Cursor.getPredefinedCursor(
                    isEmpty ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));

            JLabel slotLabel = new JLabel("SLOT " + (i + 1));
            slotLabel.setForeground(Color.WHITE);
            slotLabel.setFont(Typography.H5.getTypography().deriveFont(Font.BOLD));
            slotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(slotLabel);
            card.add(Box.createVerticalStrut(8));

            if (isEmpty) {
                buildEmptySlot(card);
                if (mode == LoadSaveType.SAVE) {
                    card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    card.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            onChoice.accept(slotChoice);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            card.setBackground(new Color(55, 50, 80));
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            card.setBackground(cardDefault);
                        }
                    });
                }
            } else {
                buildFilledSlot(card, save);
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onChoice.accept(slotChoice);
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        card.setBackground(new Color(55, 50, 80));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        card.setBackground(cardDefault);
                    }
                });
            }
            card.add(Box.createVerticalStrut(12));
            cardsPanel.add(card);
        }
        root.add(cardsPanel);
        root.add(Box.createVerticalStrut(24));

        Button clearButton = new Button("Clear all saves");
        clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearButton.addActionListener(_ -> onChoice.accept(SaveSlotChoice.CLEAR_SAVE));
        root.add(clearButton);

        Button backButton = new Button("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(_ -> onChoice.accept(SaveSlotChoice.BACK));
        root.add(backButton);

        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    private void buildEmptySlot(JPanel card) {
        card.add(Box.createVerticalGlue());

        JLabel emptyLabel = new JLabel("Empty Slot");
        emptyLabel.setForeground(Color.GRAY);
        emptyLabel.setFont(Typography.H4.getTypography());
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(emptyLabel);

        card.add(Box.createVerticalGlue());
    }

    private void buildFilledSlot(JPanel card, Hero save) {
        ImageIcon icon = FileLoader.loadScaledImage(save.getImageUrl(), 148, 148);
        JLabel imageLabel = new JLabel(icon != null ? icon : new ImageIcon());
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(imageLabel);
        card.add(Box.createVerticalStrut(8));

        JLabel nameLabel = new JLabel(save.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(Typography.H3.getTypography().deriveFont(Font.BOLD, 15f));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(2));

        JLabel classLabel = new JLabel(save.getClassName().toUpperCase());
        classLabel.setForeground(Color.WHITE);
        classLabel.setFont(Typography.H5.getTypography());
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(classLabel);
        card.add(Box.createVerticalStrut(8));

        JSeparator divider = new JSeparator();
        divider.setForeground(Color.DARK_GRAY);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(divider);
        card.add(Box.createVerticalStrut(12));

        int[] stats = {
                save.getLevel(),
                save.getExperience(),
                save.getAttack(),
                save.getDefense(),
                save.getHitPoints(),
                save.getCrit()
        };
        for (int i = 0; i < STAT_LABELS.length; i++) {
            card.add(buildStatRow(STAT_LABELS[i], stats[i], STAT_COLORS[i]));
            card.add(Box.createVerticalStrut(4));
        }
    }

    private JPanel buildStatRow(String label, int value, Color color) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel keyLabel = new JLabel(label);
        keyLabel.setForeground(color);
        keyLabel.setFont(Typography.H5.getTypography());

        JLabel dots = new JLabel("....................");
        dots.setForeground(Color.DARK_GRAY);
        dots.setFont(Typography.H5.getTypography());
        dots.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(Typography.H5.getTypography());
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        row.add(keyLabel, BorderLayout.WEST);
        row.add(dots, BorderLayout.CENTER);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }
}