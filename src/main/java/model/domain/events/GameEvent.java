package model.domain.events;

public sealed interface GameEvent permits CombatEvent, ItemEvent, SystemEvent {
    String toLogMessage();
}
