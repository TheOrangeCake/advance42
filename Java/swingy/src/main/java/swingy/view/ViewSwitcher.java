package swingy.view;

public class ViewSwitcher {
    private View currentView;
    private ViewType currentType;

    public ViewSwitcher(ViewType viewType) {
        this.currentType = viewType;
        this.currentView = ViewFactory.createView(viewType);
    }

    public View switchView() {
        this.currentView.stop();
        this.currentType = (this.currentType == ViewType.GUI) ? ViewType.CONSOLE : ViewType.GUI;
        this.currentView = ViewFactory.createView(this.currentType);
        return this.currentView;
    }

    public View getCurrentView() {
        return this.currentView;
    }
}