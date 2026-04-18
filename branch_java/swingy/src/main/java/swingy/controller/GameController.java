package swingy.controller;

import swingy.view.View;
import swingy.utils.game_menu.MainMenu;
import swingy.utils.game_menu.SettingMenu;

public class GameController {
    private View view;

    public GameController(View view) {
        this.view = view;
    }

    public MainMenu createGame() {
        return view.start();
    }

    public SettingMenu setting() {
        return view.setting();
    }

    public void closeGame() {
        view.stop();
    }
}