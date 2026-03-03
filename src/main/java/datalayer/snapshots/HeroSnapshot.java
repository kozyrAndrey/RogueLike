package datalayer.snapshots;

import java.util.List;

public record HeroSnapshot(
        String name,
        int x, int y,
        int maxHp, int curHp,
        int strength, int dexterity,
        int score,
        boolean[] keys,
        List<ItemSnapshot> weaponBag,
        List<ItemSnapshot> foodBag,
        List<ItemSnapshot> scrollBag,
        List<ItemSnapshot> elixirBag,
        ItemSnapshot equippedWeapon,
        List<BuffSnapshot> buffs
) {}
