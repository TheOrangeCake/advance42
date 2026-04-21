package swingy.view.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Button extends JButton {
    private final String originalText;

    public Button(String text) {
        super(text);
        this.originalText = text;
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setFont(Typography.H4.getTypography());
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(Color.CYAN);
                setText("->  " + originalText + "  <-");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setText(originalText);
                setForeground(Color.WHITE);
            }
        });
    }
}
