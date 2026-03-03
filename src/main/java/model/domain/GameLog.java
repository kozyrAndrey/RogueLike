package model.domain;

import java.util.LinkedList;
import java.util.List;

public class GameLog {
    private final LinkedList<String> messages;
    private final int maxMessages;
    private final int maxVisibleMessages;

    public GameLog(int maxMessages, int maxVisibleMessages) {
        this.messages = new LinkedList<>();
        this.maxMessages = maxMessages;
        this.maxVisibleMessages = maxVisibleMessages;
    }

    public void addMessage(String message) {
        messages.addFirst(message);
        while (messages.size() > maxMessages) {
            messages.removeLast();
        }
    }

    public List<String> getVisibleMessages() {
        return messages.subList(0, Math.min(messages.size(), maxVisibleMessages));
    }

    public void clear() { messages.clear(); }
}
