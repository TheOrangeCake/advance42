package swingy.view.ui;

import javax.swing.JButton;
import java.awt.Color;

public class Button extends JButton {

    public Button(String text) {
        super(text);
        setBackground(new Color(255, 255, 255, 0));
        setForeground(new Color(255, 255, 255));
        setFocusPainted(false);
        setFont(Typography.H4.getTypography());
        setBorderPainted(false);
    }
}
