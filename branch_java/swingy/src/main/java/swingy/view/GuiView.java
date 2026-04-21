package swingy.view;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import swingy.model.PlayerName;
import swingy.utils.ValidatorClient;
import swingy.view.game_menu.HeroClassChoice;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;
import swingy.view.ui.Button;
import swingy.view.ui.Typography;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
            Button button = new Button(choice.getDescription());
            button.addActionListener(_ -> onChoice.accept(choice));
            gbc.gridy++;
            panel.add(button, gbc);
        }
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

    private static final String[] STAT_LABELS = {"LVL", "EXP", "ATK", "DEF", "HP", "CRIT"};
    private static final Color[] STAT_COLORS = {
            new Color(180, 180, 255),
            new Color(255, 210, 100),
            new Color(255,  90,  90),
            new Color( 80, 200, 255),
            new Color(100, 220, 120),
            new Color(255, 160,  60),
    };

    @Override
    public void newGamePage(BiConsumer<HeroClassChoice, String> onChoice) {
        Validator validator = ValidatorClient.getValidator();

        JPanel root = setLayout();
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

        JLabel nameLabel = new JLabel("Hero name:");
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
            ImageIcon icon = loadScaledIcon(choice.getImage(), 148, 148);
            JLabel imageLabel = new JLabel(icon != null ? icon : new ImageIcon());
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(imageLabel);
            card.add(Box.createVerticalStrut(8));

            // Class name
            JLabel classNameLabel = new JLabel(choice.getClassName().toUpperCase());
            classNameLabel.setForeground(Color.WHITE);
            classNameLabel.setFont(Typography.H3.getTypography().deriveFont(Font.BOLD, 15f));
            classNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(classNameLabel);
            card.add(Box.createVerticalStrut(8));

            JSeparator divider = new JSeparator();
            divider.setForeground(new Color(80, 80, 110));
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
        Button submitButton = new Button("Create Hero");
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
                    errorLabel.setText("Please enter a valid name and select a class");
                } else if (!nameOk) {
                    errorLabel.setText("Please enter a valid hero name: " + violations.iterator().next().getMessage());
                } else {
                    errorLabel.setText("Please select a hero class");
                }
            }
        });

        root.add(submitButton);
        root.add(Box.createVerticalStrut(6));
        root.add(errorLabel);
        repaintPage(root);
    }

    private ImageIcon loadScaledIcon(String resourcePath, int maxW, int maxH) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) return null;
            Image img = ImageIO.read(url).getScaledInstance(maxW, maxH, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    private JPanel buildStatRow(String label, int value, Color color) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel keyLabel = new JLabel(label);
        keyLabel.setForeground(color);
        keyLabel.setFont(Typography.H5.getTypography());

        JLabel dots = new JLabel();
        dots.setForeground(new Color(70, 70, 100));
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
