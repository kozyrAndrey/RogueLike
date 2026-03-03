package controller;

public class PlayerSession {
    private final StringBuilder playerName = new StringBuilder();
    private final int maxNameLength = 12;

    public String getPlayerName() { return playerName.toString(); }

    public void appendCharacter(char ch) {
        if (isValidNameCharacter(ch) && playerName.length() < maxNameLength) playerName.append(ch);
    }

    public void deleteLastCharacter() {
        if (!playerName.isEmpty()) {
            playerName.setLength(playerName.length() - 1);
        }
    }

    public boolean isNameValid() { return !playerName.isEmpty(); }

    public void reset() { playerName.setLength(0); }

    private boolean isValidNameCharacter(char ch) { return Character.isLetterOrDigit(ch); }
}
