package swingy.view.ui;

import java.awt.Font;

public enum Typography {
    H1(new Font("Monospaced", Font.BOLD, 48)),
    H2(new Font("Monospaced", Font.PLAIN, 32)),
    H3(new Font("Monospaced", Font.PLAIN, 24)),
    H4(new Font("Monospaced", Font.BOLD, 20)),
    H5(new Font("Monospaced", Font.PLAIN, 16)),
    PARAGRAPH(new Font("Monospaced", Font.PLAIN, 12)),
    GAME_TITLE(new Font("Monospaced", Font.BOLD, 96));

    private final Font typography;
    Typography(Font typography) {
        this.typography = typography;
    }

    public Font getTypography() {
        return this.typography;
    }
}
