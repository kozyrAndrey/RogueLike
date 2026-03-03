package controller;

import java.util.ArrayList;
import java.util.List;

public class MenuManager {
    public static final int NEW_GAME_INDEX = 0;
    public static final int CONTINUE_INDEX = 1;
    public static final int STATISTICS_INDEX = 2;
    private final List<MenuItem> menuItems;
    private int selectedIndex = 0;

    public MenuManager() {
        this.menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("New Game", true));
        menuItems.add(new MenuItem("Continue", false));
        menuItems.add(new MenuItem("Statistics", true));
    }

    public void moveUp() {
        if (menuItems.isEmpty()) return;

        int startIndex = selectedIndex;
        do {
            selectedIndex = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
        } while (!menuItems.get(selectedIndex).isEnabled() && selectedIndex != startIndex);
    }

    public void moveDown() {
        if (menuItems.isEmpty()) return;

        int startIndex = selectedIndex;
        do {
            selectedIndex = (selectedIndex + 1) % menuItems.size();
        } while (!menuItems.get(selectedIndex).isEnabled() && selectedIndex != startIndex);
    }

    public MenuItem getSelectedItem() {
        if (menuItems.isEmpty()) return null;
        return menuItems.get(selectedIndex);
    }

    public int getSelectedIndex() { return selectedIndex; }

    public List<MenuItem> getMenuItems() { return new ArrayList<>(menuItems); }

    public void addMenuItem(MenuItem item) { menuItems.add(item); }

    public void setMenuItemEnabled(int index, boolean enabled) {
        if (index >= 0 && index < menuItems.size()) {
            menuItems.get(index).setEnabled(enabled);
        }
    }
}
