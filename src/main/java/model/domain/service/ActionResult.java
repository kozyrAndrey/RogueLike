package model.domain.service;

import model.domain.events.GameEvent;

import java.util.Collections;
import java.util.List;

public class ActionResult {
    private final boolean success;
    private final List<GameEvent> events;

    private ActionResult(boolean success, List<GameEvent> events) {
        this.success = success;
        this.events = Collections.unmodifiableList(events);
    }

    public static ActionResult success(GameEvent... events) {
        return new ActionResult(true, List.of(events));
    }

    public static ActionResult success(List<GameEvent> events) {
        return new ActionResult(true, events);
    }

    public static ActionResult failure() {
        return new ActionResult(false, Collections.emptyList());
    }

    public static ActionResult failure(GameEvent event) {
        return new ActionResult(false, List.of(event));
    }

    public boolean isSuccess() { return success; }
    public List<GameEvent> getEvents() { return events; }
}
