package model.domain.events;

public record SystemEvent(String message) implements GameEvent {
    @Override
    public String toLogMessage() {
        return message;
    }
}
