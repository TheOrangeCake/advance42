package swingy.view;

import swingy.utils.ViewType;

public enum ViewFactory {
    INSTANCE;

    public static View createView(ViewType type) {
        switch (type) {
            case CONSOLE:
                return new ConsoleView();
            case GUI:
                return new GuiView(780, 1440);
            default:
                System.err.println("Error: Invalid view type");
                System.exit(-1);
        }
        return new ConsoleView();
    }
}
