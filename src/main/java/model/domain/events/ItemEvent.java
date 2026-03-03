package model.domain.events;

public record ItemEvent(
        String itemName,
        ItemAction action
) implements GameEvent {
    public enum ItemAction { PICKED_UP, USED, EQUIPPED, UNEQUIPPED }

    @Override
    public String toLogMessage() {
        return switch (action) {
            case PICKED_UP -> String.format("Picked up: %s", itemName);
            case USED -> String.format("Used: %s", itemName);
            case EQUIPPED -> String.format("Equipped: %s", itemName);
            case UNEQUIPPED -> String.format("Unequipped: %s", itemName);
        };
    }
}
