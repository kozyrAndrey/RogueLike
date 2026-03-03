package controller;

public class MenuItem {
    private final String label;
    private boolean enabled;

    public MenuItem(String label, boolean enabled) {
        this.label = label;
        this.enabled = enabled;
    }

    public String getLabel() { return label; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return label;
    }
}
