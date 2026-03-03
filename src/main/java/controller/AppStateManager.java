package controller;

public class AppStateManager {
    private AppState currentState = AppState.START_MENU;

    public AppState getCurrentState() { return currentState; }

    public void setState(AppState newState) { this.currentState = newState; }

    public boolean isInState(AppState state) { return this.currentState == state; }
}
