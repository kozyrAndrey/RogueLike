package model.domain.creature.combat;

public record CombatResult(int damage, boolean didHit, boolean isFatal) {
    public boolean defenderDied() { return isFatal; }
}
