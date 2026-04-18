package swingy.controller;

import swingy.view.View;
import swingy.utils.game_menu.MainMenuChoice;

public class GameController {
    private View view;

    public GameController(View view) {
        this.view = view;
    }

    public MainMenuChoice createGame() {
        return view.start();
    }

    public void closeGame() {
        view.stop();
    }
}