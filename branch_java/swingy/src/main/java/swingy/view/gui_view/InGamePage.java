package swingy.view.gui_view;

import swingy.model.character.Hero;
import swingy.model.map.GameMap;
import swingy.model.villain.Villain;
import swingy.utils.FileLoader;
import swingy.view.game_menu.InGameChoice;
import swingy.view.ui.Typography;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.function.Consumer;

public class InGamePage {
    public void displayPage(Consumer<InGameChoice> onChoice, JFrame frame, Hero hero, GameMap gameMap) {
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
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.BOTH;
        g.insets  = new Insets(0, 6, 0, 6);
        g.weighty = 1.0;

        g.gridx  = 0;
        g.weightx = 0.2;
        body.add(buildStatsPanel(hero, onChoice), g);

        g.gridx  = 1;
        g.weightx = 0.6;
        MapPanel mapPanel = new MapPanel(gameMap);
        body.add(mapPanel, g);

        g.gridx  = 2;
        g.weightx = 0.2;
        body.add(buildRightPanel(onChoice, mapPanel), g);

        panel.add(body, BorderLayout.CENTER);

        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);

        // villain info
        JPanel villainCard = new JPanel();
        villainCard.setLayout(new BoxLayout(villainCard, BoxLayout.Y_AXIS));
        villainCard.setBackground(Color.BLACK);
        villainCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel noSelection = new JLabel("Click a villain");
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
            }
            villainCard.revalidate();
            villainCard.repaint();
        });

        panel.add(villainCard);
        panel.add(Box.createVerticalGlue());
        panel.add(separator());
        panel.add(Box.createVerticalStrut(12));
        panel.add(buildControlPad(onChoice));
        return panel;
    }

    private JPanel buildControlPad(Consumer<InGameChoice> onChoice) {
        JPanel controlPad = new JPanel(new GridBagLayout());
        controlPad.setBackground(Color.BLACK);
        controlPad.setAlignmentX(Component.CENTER_ALIGNMENT);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 2, 2, 2);

        JButton up = arrowButton(InGameChoice.UP.getDescription());
        up.addActionListener(_ -> onChoice.accept(InGameChoice.UP));
        JButton down = arrowButton(InGameChoice.DOWN.getDescription());
        down.addActionListener(_ -> onChoice.accept(InGameChoice.DOWN));
        JButton left = arrowButton(InGameChoice.LEFT.getDescription());
        left.addActionListener(_ -> onChoice.accept(InGameChoice.LEFT));
        JButton right = arrowButton(InGameChoice.RIGHT.getDescription());
        right.addActionListener(_ -> onChoice.accept(InGameChoice.RIGHT));

        g.gridx = 1; g.gridy = 0; controlPad.add(up, g);
        g.gridx = 0; g.gridy = 1; controlPad.add(left, g);
        g.gridx = 1; g.gridy = 1; controlPad.add(new JLabel(""), g);
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
        sep.setForeground(Color.DARK_GRAY);
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
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g2) {
                g2.setColor(getModel().isRollover() ? Color.DARK_GRAY : Color.BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(Typography.PARAGRAPH.getTypography());
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return btn;
    }

    private JButton arrowButton(String symbol) {
        JButton btn = new JButton(symbol) {
            @Override protected void paintComponent(Graphics g2) {
                g2.setColor(getModel().isRollover() ? Color.CYAN : Color.BLACK);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                super.paintComponent(g2);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(Typography.H5.getTypography());
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    static class MapPanel extends JPanel {
        private final GameMap gameMap;

        private static final int baseTileSize = 48;
        private double scale       = 1.0;
        private double offsetX     = 0;
        private double offsetY     = 0;
        private Point  dragStart   = null;
        private double dragOffsetX = 0;
        private double dragOffsetY = 0;

        private java.util.function.Consumer<Villain> onVillainSelected;

        MapPanel(GameMap gameMap) {
            this.gameMap  = gameMap;
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            // zoom
            addMouseWheelListener(e -> {
                double factor = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                scale = Math.clamp(scale * factor, 0.3, 4.0);
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
                            handleClick(e.getPoint()); // treat as click if barely moved
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
                        repaint();
                    }
                }
            });
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
                    g2.setColor(Color.GRAY);
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
}
