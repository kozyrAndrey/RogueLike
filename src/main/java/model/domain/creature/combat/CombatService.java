package model.domain.creature.combat;

import model.domain.creature.Character;

import java.util.Random;

public class CombatService {
    public static CombatResult calculateAttack(Character attacker, Character target) {
        Random rng = new Random();
        int damage = 0;
        boolean didHit = false;
        boolean isFatal = false;
        // 1 - Проверка на попадание. Высчитывается из ловкости бьющего и цели удара
        int dex = attacker.getDexterity();
        int enemyDex = target.getDexterity();
        int totalChance = 10 - (dex - enemyDex);
        if (totalChance > 0) {
            int chance = rng.nextInt(100);
            if (chance > totalChance) didHit = true;
        } else {
            didHit = true;
        }
        if (didHit) {
            // 2 - Расчет урона. Высчитывается из силы и модификаторов
            damage = rng.nextInt(attacker.getStrength() / 2, attacker.getStrength());
            int newHealth = target.getHealth() - damage;
            // 3 - Применение урона. Если здоровье падает до 0 или ниже, то противник или персонаж погибает
            if (newHealth <= 0) {
                isFatal = true;
            }
        }
        return new CombatResult(damage, didHit, isFatal);
    }
}
