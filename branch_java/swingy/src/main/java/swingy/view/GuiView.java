package swingy.view;

import javax.swing.JFrame;
import swingy.model.character.Hero;
import swingy.model.character.Warrior;
import swingy.utils.game_menu.MainMenu;
import swingy.utils.game_menu.SettingMenu;

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
    public MainMenu start() {
        frame.setSize(this.height, this.width);
        frame.setTitle("42 Swingy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        return MainMenu.EXIT;
    }

    @Override
    public SettingMenu setting() {
        return SettingMenu.BACK;
    }

    @Override
    public void stop() {
    }
}
