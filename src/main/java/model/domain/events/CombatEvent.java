package model.domain.events;

import model.domain.creature.combat.CombatResult;

public record CombatEvent(
    CombatResult combatResult,
    String attacker,
    String defender,
    boolean isPlayerAttacking
) implements GameEvent {
    @Override
    public String toLogMessage() {
        if (!combatResult.didHit()) {
            return isPlayerAttacking ?
                    String.format("You missed %s", defender) :
                    String.format("%s missed you", attacker);
        }

        if (isPlayerAttacking) {
            return combatResult.defenderDied() ?
                    String.format("You killed %s!", defender) :
                    String.format("You hit %s for %d damage", defender, combatResult.damage());
        } else {
            return combatResult.defenderDied() ?
                    String.format("%s killed you!", attacker) :
                    String.format("%s hits you for %d damage", attacker, combatResult.damage());
        }
    }
}
