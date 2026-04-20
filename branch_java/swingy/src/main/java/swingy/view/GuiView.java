package swingy.view;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.Container;
import java.util.function.Consumer;
import swingy.view.game_menu.MainMenuChoice;
import swingy.view.game_menu.SettingMenuChoice;

public class GuiView implements View {
    private final JFrame frame;
    private final int height;
    private final int width;

    public GuiView(int height, int width) {
        this.frame = new JFrame();
        this.height = height;
        this.width = width;
        System.out.println("GUI View");
    }

    @Override
    public void startPage(Consumer<MainMenuChoice> onChoice) {
        Container container = frame.getContentPane();
        container.removeAll();
        FlowLayout flowLayout = new FlowLayout();
        container.setLayout(flowLayout);
        
        frame.setSize(this.width, this.height);
        frame.setTitle("42 Swingy");

        for (MainMenuChoice choice : MainMenuChoice.values()) {
            JButton button = new JButton(choice.getDescription());
            button.addActionListener(ae -> onChoice.accept(choice));
            container.add(button);
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void settingPage(Consumer<SettingMenuChoice> onChoice) {

    }

    @Override
    public void stop() {
        frame.dispose();
    }
}
