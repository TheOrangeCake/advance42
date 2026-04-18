package swingy.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import swingy.model.character.Hero;
import swingy.model.character.Warrior;
import swingy.view.game_menu.MainMenu;
import swingy.view.game_menu.SettingMenu;
import swingy.view.game_menu.Menu;

public class GuiView implements View {
    private JFrame frame;
    private int height;
    private int width;

    public GuiView(int height, int width) {
        this.frame = new JFrame();
        this.height = height;
        this.width = width;
        System.out.println("GUI View");
    }

    @Override
    public MainMenu startPage() {
        Container container = frame.getContentPane();
        container.removeAll();
        FlowLayout flowLayout = new FlowLayout();
        container.setLayout(flowLayout);
        
        frame.setSize(this.height, this.width);
        frame.setTitle("42 Swingy");


        List<JButton> buttons = renderMenu(MainMenu.class);
        for (JButton button : buttons) {
            container.add(button);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        return MainMenu.EXIT;
    }

    @Override
    public SettingMenu settingPage() {
        return SettingMenu.BACK;
    }

    @Override
    public void stop() {
        frame.dispose();
    }

    private <T extends Enum<T> & Menu> List<JButton> renderMenu(Class<T> enumClass) {
        T[] choices = enumClass.getEnumConstants();
        List<JButton> buttons = new ArrayList<>();
        for (T choice : choices) {
            JButton button = new JButton(choice.getDescription());
            ActionListener buttonListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    System.out.println(ae.getSource());
                }
            };
            button.addActionListener(buttonListener);
            buttons.add(button);
        }
        return buttons;
    }


}
