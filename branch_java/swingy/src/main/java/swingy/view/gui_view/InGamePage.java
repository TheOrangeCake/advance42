package swingy.view.gui_view;

import swingy.model.artifact.Artifact;
import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.villain.Villain;
import swingy.utils.FileLoader;
import swingy.view.PopUpType;
import swingy.view.game_menu.*;
import swingy.view.ui.Typography;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.function.Consumer;

public class InGamePage {
    private double mapScale   = 1.0;
    private double mapOffsetX = 0;
    private double mapOffsetY = 0;

    public void displayPage(
            Consumer<InGameChoice> onInGameChoice,
            Consumer<BattleChoice> onBattleChoice,
            Consumer<WinChoice> onWinChoice,
            Consumer<DefeatChoice> onDefeatChoice,
            JFrame frame,
            Hero hero,
            GameMap gameMap,
            PopUpType popUpType) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleBar.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("LEVEL " + gameMap.getLevel());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(Typography.H3.getTypography());
        titleBar.add(titleLabel);
        panel.add(titleBar, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.BLACK);
        body.setBorder(new EmptyBorder(12, 12, 12, 12));
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.BOTH;
        g.insets  = new Insets(0, 6, 0, 6);
        g.weighty = 1.0;

        g.gridx  = 0;
        g.weightx = 2.0;
        body.add(buildStatsPanel(hero, onInGameChoice), g);

        g.gridx  = 1;
        g.weightx = 6.0;
        MapPanel mapPanel = new MapPanel(gameMap, mapScale, mapOffsetX, mapOffsetY);
        mapPanel.setOnStateChanged((scale, ox, oy) -> {
            this.mapScale = scale;
            this.mapOffsetX = ox;
            this.mapOffsetY = oy;
        });
        body.add(mapPanel, g);

        g.gridx  = 2;
        g.weightx = 1.0;
        body.add(buildRightPanel(onInGameChoice, mapPanel), g);

        panel.add(body, BorderLayout.CENTER);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();

        if (popUpType == PopUpType.BATTLE) {
            Villain villain = gameMap.getVillainAtHeroPosition();
            showBattlePopup(frame, hero, villain, onBattleChoice);
        } else if (popUpType == PopUpType.WIN) {
            showWinPopup(frame, gameMap, onWinChoice);
        } else if (popUpType == PopUpType.DEFEAT) {
            showDefeatPopup(frame, hero, onDefeatChoice);
        }
    }

    private JPanel buildStatsPanel(Hero hero, Consumer<InGameChoice> onChoice) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        // Image
        JLabel heroImg = new JLabel();
        heroImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = FileLoader.loadScaledImage(hero.getImageUrl(), 120, 120);
        if (icon != null) heroImg.setIcon(icon);
        else {
            heroImg.setText("⚔");
            heroImg.setFont(Typography.H3.getTypography());
            heroImg.setForeground(Color.BLACK);
        }
        panel.add(heroImg);
        panel.add(Box.createVerticalStrut(10));

        // Name and class
        JLabel name = new JLabel(hero.getName());
        name.setFont(Typography.H5.getTypography());
        name.setForeground(Color.WHITE);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(name);

        JLabel className = new JLabel(hero.getClassName());
        className.setFont(Typography.PARAGRAPH.getTypography());
        className.setForeground(Color.WHITE);
        className.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(className);

        panel.add(Box.createVerticalStrut(16));
        panel.add(separator());
        panel.add(Box.createVerticalStrut(12));

        panel.add(statRow("LVL", String.valueOf(hero.getLevel())));
        panel.add(Box.createVerticalStrut(4));

        int expNeeded = (int)(hero.getLevel() * 1000 + Math.pow(hero.getLevel() - 1, 2) * 450);
        panel.add(statRow("XP",hero.getExperience() + " / " + expNeeded));
        panel.add(Box.createVerticalStrut(4));

        // Xp bar
        JPanel xpBar = xpBar(hero.getExperience(), expNeeded);
        xpBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(xpBar);
        panel.add(Box.createVerticalStrut(10));
        panel.add(separator());
        panel.add(Box.createVerticalStrut(10));

        panel.add(statRow("ATK", String.valueOf(hero.getAttack())));
        panel.add(Box.createVerticalStrut(4));
        panel.add(statRow("DEF", String.valueOf(hero.getDefense())));
        panel.add(Box.createVerticalStrut(4));
        panel.add(statRow("HP", String.valueOf(hero.getHitPoints())));
        panel.add(Box.createVerticalStrut(4));
        panel.add(statRow("CRIT", hero.getCrit() + "%"));

        panel.add(Box.createVerticalGlue());
        panel.add(separator());
        panel.add(Box.createVerticalStrut(10));

        JButton settingButton = styledButton(InGameChoice.SETTING.getDescription());
        settingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingButton.addActionListener(_ -> onChoice.accept(InGameChoice.SETTING));
        panel.add(settingButton);

        return panel;
    }

    private JPanel buildRightPanel(Consumer<InGameChoice> onChoice, MapPanel mapPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        JPanel villainCard = new JPanel();
        villainCard.setLayout(new BoxLayout(villainCard, BoxLayout.Y_AXIS));
        villainCard.setBackground(Color.BLACK);
        villainCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noSelection = new JLabel("Select a villain");
        noSelection.setFont(Typography.PARAGRAPH.getTypography());
        noSelection.setForeground(Color.WHITE);
        noSelection.setAlignmentX(Component.CENTER_ALIGNMENT);
        villainCard.add(noSelection);

        mapPanel.setOnVillainSelected(villain -> {
            villainCard.removeAll();
            if (villain == null) {
                villainCard.add(noSelection);
            } else {
                JLabel vImg = new JLabel();
                vImg.setAlignmentX(Component.CENTER_ALIGNMENT);
                ImageIcon vi = FileLoader.loadScaledImage(villain.getImageUrl(), 120, 120);
                if (vi != null) vImg.setIcon(vi);
                else {
                    vImg.setText(villain.getClassName());
                    vImg.setFont(Typography.PARAGRAPH.getTypography());
                    vImg.setForeground(Color.WHITE);
                }
                villainCard.add(vImg);
                villainCard.add(Box.createVerticalStrut(8));

                JLabel className = new JLabel(villain.getClassName());
                className.setFont(Typography.H5.getTypography());
                className.setForeground(Color.WHITE);
                className.setAlignmentX(Component.CENTER_ALIGNMENT);
                villainCard.add(className);

                JLabel level = new JLabel("Lvl: " + villain.getLevel());
                level.setFont(Typography.PARAGRAPH.getTypography());
                level.setForeground(Color.WHITE);
                level.setAlignmentX(Component.CENTER_ALIGNMENT);
                villainCard.add(level);

                villainCard.add(Box.createVerticalStrut(10));
                villainCard.add(separator());
                villainCard.add(Box.createVerticalStrut(8));
                villainCard.add(statRow("ATK", String.valueOf(villain.getAttack())));
                villainCard.add(Box.createVerticalStrut(4));
                villainCard.add(statRow("DEF", String.valueOf(villain.getDefense())));
                villainCard.add(Box.createVerticalStrut(4));
                villainCard.add(statRow("HP", String.valueOf(villain.getHitPoints())));
                villainCard.add(Box.createVerticalStrut(4));
                villainCard.add(statRow("CRIT", villain.getCrit() + "%"));
                villainCard.add(Box.createVerticalStrut(4));
                villainCard.add(statRow("EXP", String.valueOf(villain.getExperience())));
            }
            villainCard.revalidate();
            villainCard.repaint();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(Color.BLACK);
        bottomPanel.add(separator());
        bottomPanel.add(Box.createVerticalStrut(12));
        bottomPanel.add(buildControlPad(onChoice));

        panel.add(villainCard, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildControlPad(Consumer<InGameChoice> onChoice) {
        JPanel controlPad = new JPanel(new GridBagLayout());
        controlPad.setBackground(Color.BLACK);
        controlPad.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints g = new GridBagConstraints();

        JButton up = arrowButton(InGameChoice.UP.getDescription());
        up.addActionListener(_ -> onChoice.accept(InGameChoice.UP));
        JButton down = arrowButton(InGameChoice.DOWN.getDescription());
        down.addActionListener(_ -> onChoice.accept(InGameChoice.DOWN));
        JButton left = arrowButton(InGameChoice.LEFT.getDescription());
        left.addActionListener(_ -> onChoice.accept(InGameChoice.LEFT));
        JButton right = arrowButton(InGameChoice.RIGHT.getDescription());
        right.addActionListener(_ -> onChoice.accept(InGameChoice.RIGHT));
        Dimension buttonSize = new Dimension(100, 32);
        up.setPreferredSize(buttonSize);
        down.setPreferredSize(buttonSize);
        left.setPreferredSize(buttonSize);
        right.setPreferredSize(buttonSize);

        g.gridx = 1; g.gridy = 0; controlPad.add(up, g);
        g.gridx = 0; g.gridy = 1; controlPad.add(left, g);
        g.gridx = 1; g.gridy = 1; controlPad.add(new JLabel("x"), g);
        g.gridx = 2; g.gridy = 1; controlPad.add(right, g);
        g.gridx = 1; g.gridy = 2; controlPad.add(down, g);

        return controlPad;
    }

    private JPanel statRow(String attribute, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.BLACK);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        JLabel label = new JLabel(attribute);
        label.setFont(Typography.PARAGRAPH.getTypography());
        label.setForeground(Color.WHITE);
        JLabel val = new JLabel(value);
        val.setFont(Typography.PARAGRAPH.getTypography());
        val.setForeground(Color.WHITE);
        row.add(label, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);
        return row;
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Color.GRAY);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    private JPanel xpBar(int current, int max) {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g2) {
                super.paintComponent(g2);
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                int fill = max > 0 ? (int)((double) current / max * getWidth()) : 0;
                g2.setColor(Color.CYAN);
                g2.fillRoundRect(0, 0, fill, getHeight(), 4, 4);
            }
        };
        bar.setPreferredSize(new Dimension(0, 6));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        bar.setOpaque(false);
        return bar;
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g2) {
                g2.setColor(getModel().isRollover() ? Color.DARK_GRAY : Color.BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(Typography.PARAGRAPH.getTypography());
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return button;
    }

    private JButton arrowButton(String symbol) {
        JButton button = new JButton(symbol) {
            @Override protected void paintComponent(Graphics g2) {
                g2.setColor(getModel().isRollover() ? Color.DARK_GRAY : Color.BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(Typography.H5.getTypography());
        button.setPreferredSize(new Dimension(40, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    @FunctionalInterface
    interface TriConsumer {
        void accept(double scale, double ox, double oy);
    }

    static class MapPanel extends JPanel {
        private final GameMap gameMap;
        private static final int baseTileSize = 48;
        private double scale;
        private double offsetX;
        private double offsetY;
        private Point  dragStart = null;
        private double dragOffsetX = 0;
        private double dragOffsetY = 0;
        private Consumer<Villain> onVillainSelected;
        private TriConsumer onStateChanged;

        MapPanel(GameMap gameMap, double initScale, double initOffsetX, double initOffsetY) {
            this.gameMap = gameMap;
            this.scale = initScale;
            this.offsetX = initOffsetX;
            this.offsetY = initOffsetY;
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            // zoom
            addMouseWheelListener(e -> {
                double factor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                scale = Math.clamp(scale * factor, 0.3, 4.0);
                notifyStateChanged();
                repaint();
            });

            // drag
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        dragStart   = e.getPoint();
                        dragOffsetX = offsetX;
                        dragOffsetY = offsetY;
                    }
                }
                @Override public void mouseReleased(MouseEvent e) {
                    if (dragStart != null) {
                        double dx = e.getX() - dragStart.x;
                        double dy = e.getY() - dragStart.y;
                        if (Math.abs(dx) < 4 && Math.abs(dy) < 4) {
                            handleClick(e.getPoint());
                        }
                        dragStart = null;
                    }
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseDragged(MouseEvent e) {
                    if (dragStart != null) {
                        offsetX = dragOffsetX + (e.getX() - dragStart.x);
                        offsetY = dragOffsetY + (e.getY() - dragStart.y);
                        notifyStateChanged();
                        repaint();
                    }
                }
            });
        }

        private void notifyStateChanged() {
            if (this.onStateChanged != null)
                this.onStateChanged.accept(scale, offsetX, offsetY);
        }

        void setOnStateChanged(TriConsumer onStateChanged) {
            this.onStateChanged = onStateChanged;
        }

        void setOnVillainSelected(Consumer<Villain> onSelected) {
            this.onVillainSelected = onSelected;
        }

        private void handleClick(Point p) {
            int tileSize = (int)(baseTileSize * scale);
            int size = gameMap.getSize();
            double totalW = size * tileSize;
            double totalH = size * tileSize;
            double startX = (getWidth()  - totalW) / 2.0 + offsetX;
            double startY = (getHeight() - totalH) / 2.0 + offsetY;

            int col = (int)((p.x - startX) / tileSize) + 1;
            int row = (int)((p.y - startY) / tileSize) + 1;

            String key = col + "," + row;
            Villain villain = gameMap.getVillains().get(key);
            if (onVillainSelected != null) {
                onVillainSelected.accept(villain);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = gameMap.getSize();
            int tileSize = (int)(baseTileSize * scale);
            int totalW = size * tileSize;
            int totalH = size * tileSize;
            int startX = (int)((getWidth() - totalW) / 2.0 + offsetX);
            int startY = (int)((getHeight() - totalH) / 2.0 + offsetY);

            int[] heroPos = gameMap.getHeroPosition();
            Map<String, Villain> villains = gameMap.getVillains();

            for (int row = 1; row <= size; row++) {
                for (int col = 1; col <= size; col++) {
                    int px = startX + (col - 1) * tileSize;
                    int py = startY + (row - 1) * tileSize;

                    // checkerboard tiles
                    g2.setColor((row + col) % 2 == 0 ? Color.DARK_GRAY : Color.GRAY);
                    g2.fillRect(px, py, tileSize, tileSize);

                    // tile border
                    g2.setColor(Color.BLACK);
                    g2.drawRect(px, py, tileSize, tileSize);

                    boolean isHero    = heroPos[0] == col && heroPos[1] == row;
                    boolean isVillain = villains.containsKey(col + "," + row);
                    if (isHero) {
                        drawToken(g2, px, py, tileSize, Color.CYAN, "H");
                    } else if (isVillain) {
                        String className = villains.get(col + "," + row).getClassName();
                        if (className.equals("Skeleton")) {
                            drawToken(g2, px, py, tileSize, Color.ORANGE, "S");
                        } else if (className.equals("Goblin")) {
                            drawToken(g2, px, py, tileSize, Color.PINK, "G");
                        } else {
                            drawToken(g2, px, py, tileSize, Color.RED, "V");
                        }
                    }
                }
            }

            g2.setColor(Color.WHITE);
            g2.drawRect(startX, startY, totalW, totalH);
        }

        private void drawToken(Graphics2D g2, int px, int py, int tileSize, Color color, String letter) {
            int pad = Math.max(4, tileSize / 6);
            int radius = tileSize - pad * 2;
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60));
            g2.fillOval(px + pad, py + pad, radius, radius);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(Math.max(1, tileSize / 16f)));
            g2.drawOval(px + pad, py + pad, radius, radius);

            if (tileSize > 20) {
                g2.setFont(new Font("Monospaced", Font.BOLD, Math.max(8, tileSize / 3)));
                FontMetrics fm = g2.getFontMetrics();
                int tx = px + (tileSize - fm.stringWidth(letter)) / 2;
                int ty = py + (tileSize + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(letter, tx, ty);
            }
        }
    }

    private void showBattlePopup(JFrame frame, Hero hero, Villain villain, Consumer<BattleChoice> onBattleChoice) {
        JDialog dialog = new JDialog(frame, "Battle!", true);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(new Color(20, 20, 20));
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(24, 32, 24, 32)
        ));

        JLabel title = new JLabel("Encounter!", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(Typography.H3.getTypography());
        root.add(title, BorderLayout.NORTH);

        JPanel statsRow = new JPanel(new GridBagLayout());
        statsRow.setBackground(new Color(20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.insets = new Insets(0, 12, 0, 12);
        g.weighty = 1.0;

        g.gridx = 0; g.weightx = 1.0;
        statsRow.add(buildCombatantCard(
                hero.getName(),
                hero.getClassName(),
                hero.getImageUrl(),
                hero.getLevel(),
                hero.getAttack(),
                hero.getDefense(),
                hero.getHitPoints(),
                hero.getCrit(),
                hero.getExperience(),
                Color.CYAN
        ), g);

        g.gridx = 1; g.weightx = 0.0;
        JLabel vs = new JLabel("VS", SwingConstants.CENTER);
        vs.setForeground(Color.GRAY);
        vs.setFont(Typography.H3.getTypography());
        statsRow.add(vs, g);

        g.gridx = 2; g.weightx = 1.0;
        statsRow.add(buildCombatantCard(
                villain.getClassName(),
                villain.getClassName(),
                villain.getImageUrl(),
                villain.getLevel(),
                villain.getAttack(),
                villain.getDefense(),
                villain.getHitPoints(),
                villain.getCrit(),
                villain.getExperience(),
                Color.RED
        ), g);

        root.add(statsRow, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 16, 0));
        buttons.setBackground(new Color(20, 20, 20));

        JButton attack = styledButton(BattleChoice.ATTACK.getDescription());
        attack.setForeground(Color.CYAN);
        attack.addActionListener(_ -> {
            dialog.dispose();
            onBattleChoice.accept(BattleChoice.ATTACK);
        });

        JButton run = styledButton(BattleChoice.RUN.getDescription());
        run.setForeground(Color.ORANGE);
        run.addActionListener(_ -> {
            dialog.dispose();
            onBattleChoice.accept(BattleChoice.RUN);
        });

        buttons.add(attack);
        buttons.add(run);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(480, 300));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private JPanel buildCombatantCard(
            String name,
            String className,
            String imageUrl,
            int level,
            int atk,
            int def,
            int hp,
            int crit,
            int exp,
            Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.BLACK);

        JLabel img = new JLabel();
        img.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = FileLoader.loadScaledImage(imageUrl, 120, 120);
        if (icon != null) img.setIcon(icon);
        else {
            img.setText("?");
            img.setFont(Typography.H3.getTypography());
            img.setForeground(accentColor);
        }
        card.add(img);
        card.add(Box.createVerticalStrut(8));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(Typography.H5.getTypography());
        nameLabel.setForeground(accentColor);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        JLabel classLabel = new JLabel(className, SwingConstants.CENTER);
        classLabel.setFont(Typography.PARAGRAPH.getTypography());
        classLabel.setForeground(Color.GRAY);
        classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(classLabel);

        JLabel levelLabel = new JLabel("Lv." + level, SwingConstants.CENTER);
        levelLabel.setFont(Typography.PARAGRAPH.getTypography());
        levelLabel.setForeground(Color.GRAY);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(levelLabel);

        card.add(Box.createVerticalStrut(12));
        card.add(separator());
        card.add(Box.createVerticalStrut(8));

        card.add(statRow("ATK",  String.valueOf(atk)));
        card.add(Box.createVerticalStrut(4));
        card.add(statRow("DEF",  String.valueOf(def)));
        card.add(Box.createVerticalStrut(4));
        card.add(statRow("HP",   String.valueOf(hp)));
        card.add(Box.createVerticalStrut(4));
        card.add(statRow("CRIT", crit + "%"));
        card.add(Box.createVerticalStrut(4));
        card.add(statRow("EXP", String.valueOf(exp)));

        return card;
    }

    private void showWinPopup(JFrame frame, GameMap gameMap, Consumer<WinChoice> onWinChoice) {
        JDialog dialog = new JDialog(frame, "Victory!", true);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Color.BLACK);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(24, 32, 24, 32)
        ));

        JLabel title = new JLabel("Victory!", SwingConstants.CENTER);
        title.setForeground(Color.CYAN);
        title.setFont(Typography.H3.getTypography());
        root.add(title, BorderLayout.NORTH);

        // Artifact or no drop
        Artifact artifact = gameMap.getDroppedArtifact();
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.BLACK);

        if (artifact == null) {
            JLabel noDrop = new JLabel("No artifact dropped", SwingConstants.CENTER);
            noDrop.setForeground(Color.GRAY);
            noDrop.setFont(Typography.PARAGRAPH.getTypography());
            noDrop.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(Box.createVerticalGlue());
            center.add(noDrop);
            center.add(Box.createVerticalGlue());
        } else {
            JLabel img = new JLabel();
            img.setAlignmentX(Component.CENTER_ALIGNMENT);
            ImageIcon icon = FileLoader.loadScaledImage(artifact.getImageUrl(), 120, 120);
            if (icon != null) img.setIcon(icon);
            else {
                img.setText("?");
                img.setFont(Typography.H3.getTypography());
                img.setForeground(Color.YELLOW);
            }
            center.add(img);
            center.add(Box.createVerticalStrut(8));

            JLabel artifactName = new JLabel(artifact.getClassName(), SwingConstants.CENTER);
            artifactName.setFont(Typography.H5.getTypography());
            artifactName.setForeground(Color.YELLOW);
            artifactName.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(artifactName);

            center.add(Box.createVerticalStrut(12));
            center.add(separator());
            center.add(Box.createVerticalStrut(8));

            if (artifact.getAttack() > 0) {
                center.add(statRow("ATK", "+" + artifact.getAttack()));
                center.add(Box.createVerticalStrut(4));
            }
            if (artifact.getDefense() > 0) {
                center.add(statRow("DEF", "+" + artifact.getDefense()));
                center.add(Box.createVerticalStrut(4));
            }
            if (artifact.getHitPoints() > 0) {
                center.add(statRow("HP", "+" + artifact.getHitPoints()));
                center.add(Box.createVerticalStrut(4));
            }
        }
        root.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, artifact == null ? 1 : 2, 16, 0));
        buttons.setBackground(Color.BLACK);

        if (artifact == null) {
            JButton ok = styledButton("OK");
            ok.setForeground(Color.CYAN);
            ok.addActionListener(_ -> dialog.dispose());
            buttons.add(ok);
        } else {
            JButton take = styledButton(WinChoice.TAKE.getDescription());
            take.setForeground(Color.CYAN);
            take.addActionListener(_ -> {
                dialog.dispose();
                onWinChoice.accept(WinChoice.TAKE);
            });

            JButton discard = styledButton(WinChoice.DISCARD.getDescription());
            discard.setForeground(Color.ORANGE);
            discard.addActionListener(_ -> {
                dialog.dispose();
                onWinChoice.accept(WinChoice.DISCARD);
            });

            buttons.add(take);
            buttons.add(discard);
        }
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(360, 300));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showDefeatPopup(JFrame frame, Hero hero, Consumer<DefeatChoice> onDefeatChoice) {
        JDialog dialog = new JDialog(frame, "Defeat", true);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(Color.BLACK);
        root.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(24, 32, 24, 32)
        ));

        JLabel title = new JLabel("Defeated...", SwingConstants.CENTER);
        title.setForeground(Color.RED);
        title.setFont(Typography.H3.getTypography());
        root.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.BLACK);

        JLabel deadImg = new JLabel();
        deadImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = FileLoader.loadScaledImage(hero.getDeadImageUrl(), 140, 140);
        if (icon != null) deadImg.setIcon(icon);
        else {
            deadImg.setText("?");
            deadImg.setFont(Typography.H3.getTypography());
            deadImg.setForeground(Color.RED);
        }
        center.add(Box.createVerticalGlue());
        center.add(deadImg);
        center.add(Box.createVerticalStrut(12));

        JLabel msg = new JLabel(hero.getName() + " has fallen.", SwingConstants.CENTER);
        msg.setFont(Typography.PARAGRAPH.getTypography());
        msg.setForeground(Color.GRAY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(msg);
        center.add(Box.createVerticalGlue());

        root.add(center, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 16, 0));
        buttons.setBackground(Color.BLACK);

        JButton mainMenu = styledButton(DefeatChoice.MAIN_MENU.getDescription());
        mainMenu.setForeground(Color.CYAN);
        mainMenu.addActionListener(_ -> {
            dialog.dispose();
            onDefeatChoice.accept(DefeatChoice.MAIN_MENU);
        });

        JButton exit = styledButton(DefeatChoice.EXIT.getDescription());
        exit.setForeground(Color.RED);
        exit.addActionListener(_ -> {
            dialog.dispose();
            onDefeatChoice.accept(DefeatChoice.EXIT);
        });

        buttons.add(mainMenu);
        buttons.add(exit);
        root.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(320, 280));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
}
