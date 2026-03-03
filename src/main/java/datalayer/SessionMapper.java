package datalayer;

import datalayer.dto.LevelStateDTO;
import datalayer.snapshots.*;
import model.domain.AccessLevel;
import model.domain.Coordinates;
import model.domain.LevelManager;
import model.domain.creature.enemy.*;
import model.domain.creature.enemy.factory.EnemyFactory;
import model.domain.creature.player.ActiveBuff;
import model.domain.creature.player.Hero;
import model.domain.geometry.Door;
import model.domain.geometry.Exit;
import model.domain.items.*;
import model.domain.service.BalanceService;

import java.util.ArrayList;
import java.util.List;

public class SessionMapper {
    public static LevelStateDTO toLevelState(LevelManager lm, Hero hero) {
        List<Enemy> enemies = lm.getEnemies();
        List<EnemySnapshot> enemiesSnapshots = new ArrayList<>();
        for (Enemy enemy : enemies) {
            enemiesSnapshots.add(toEnemySnapshot(enemy));
        }

        List<Item> items = lm.getItems();
        List<ItemSnapshot> itemsSnapshots = new ArrayList<>();
        for (Item item : items) {
            itemsSnapshots.add(toItemSnapshot(item));
        }

        List<Door> doors = lm.getDoors();
        List<DoorSnapshot> doorsSnapshots = new ArrayList<>();
        for (Door door : doors) {
            doorsSnapshots.add(new DoorSnapshot(door.getCoor().x(), door.getCoor().y(), door.getAccessLevel().name()));
        }
        Exit exit = lm.getExit();
        if (exit == null) {
            throw new IllegalStateException("No exit at this level");
        }
        ExitSnapshot exitSnapshot = new ExitSnapshot(exit.getCoor().x(), exit.getCoor().y());

        return new LevelStateDTO(
                lm.getNumberOfLevel(),
                lm.getFieldCopy(),
                lm.getExploredCopy(),
                toHeroSnapshot(hero),
                enemiesSnapshots,
                itemsSnapshots,
                doorsSnapshots,
                exitSnapshot,
                lm.getBalancePrevDifficulty(),
                lm.getBalanceMaxHpOnStart(),
                lm.getBalanceStartValue());
    }

    private static List<ItemSnapshot> getItemSnapshots(List<Item> items) {
        List<ItemSnapshot> itemSnapshots = new ArrayList<>();
        for (Item item : items) {
            itemSnapshots.add(toItemSnapshot(item));
        }
        return itemSnapshots;
    }

    private static List<BuffSnapshot> getBuffSnapshots(List<ActiveBuff> buffs) {
        List<BuffSnapshot> buffSnapshots = new ArrayList<>();
        for (ActiveBuff buff : buffs) {
            buffSnapshots.add(new BuffSnapshot(buff.getType().name(), buff.getValue(), buff.getTime()));
        }
        return buffSnapshots;
    }

    private static HeroSnapshot toHeroSnapshot(Hero hero) {
        List<Item> weapons = hero.getItemsByType(ItemType.WEAPON);
        List<Item> food = hero.getItemsByType(ItemType.FOOD);
        List<Item> scrolls = hero.getItemsByType(ItemType.SCROLL);
        List<Item> elixirs = hero.getItemsByType(ItemType.ELIXIR);
        List<BuffSnapshot> buffSnapshots = getBuffSnapshots(hero.getActiveBuffs());
        return new HeroSnapshot(
                hero.getName(),
                hero.getCoor().x(),
                hero.getCoor().y(),
                hero.getMaxHealth(),
                hero.getHealth(),
                hero.getBaseStrength(),
                hero.getDexterity(),
                hero.getValue(),
                hero.getKeys().clone(),
                getItemSnapshots(weapons),
                getItemSnapshots(food),
                getItemSnapshots(scrolls),
                getItemSnapshots(elixirs),
                hero.getEquippedWeapon() != null ? toItemSnapshot(hero.getEquippedWeapon()) : null,
                buffSnapshots);
    }

    private static EnemySnapshot toEnemySnapshot(Enemy enemy) {
        return new EnemySnapshot(
                enemy.getType().name(),
                enemy.getCoor().x(),
                enemy.getCoor().y(),
                enemy.getMaxHealth(),
                enemy.getHealth(),
                enemy.getStrength(),
                enemy.getDexterity(),
                enemy.getHostility());
    }

    private static ItemSnapshot toItemSnapshot(Item item) {
        int value = 0;
        String boostType = null;
        String accessLevel = null;
        if (item instanceof Weapon w) {
            value = w.getPower();
        } else if (item instanceof Food f) {
            value = f.getValue();
        } else if (item instanceof Scroll s) {
            value = s.getValue();
            boostType = s.getBoostType().name();
        } else if (item instanceof Elixir e) {
            value = e.getValue();
            boostType = e.getBoostType().name();
        } else if (item instanceof Treasure t) {
            value = t.getValue();
        } else if (item instanceof Key k) {
            accessLevel = k.getAccess().name();
        }
        return new ItemSnapshot(item.getType().name(), item.getCoor().x(), item.getCoor().y(), value, boostType,
                accessLevel, item.getName());
    }

    private static Item fromItemSnapshot(ItemSnapshot s) {
        Coordinates coor = new Coordinates(s.y(), s.x());
        return switch (s.type()) {
            case "WEAPON" -> new Weapon(coor, s.name(), s.value());
            case "FOOD" -> new Food(coor, s.name(), s.value());
            case "SCROLL" -> {
                BoostType bt = BoostType.valueOf(s.boostType());
                yield new Scroll(coor, s.name(), bt, s.value());
            }
            case "ELIXIR" -> {
                BoostType bt = BoostType.valueOf(s.boostType());
                yield new Elixir(coor, s.name(), bt, s.value());
            }
            case "TREASURE" -> new Treasure(coor, s.value());
            case "KEY" -> {
                AccessLevel access = AccessLevel.valueOf(s.accessLevel());
                yield new Key(coor, access, s.name());
            }
            default -> throw new IllegalArgumentException("Unknown item type");
        };
    }

    private static Hero fromHeroSnapshot(HeroSnapshot s) {
        Coordinates coor = new Coordinates(s.y(), s.x());
        Hero hero = new Hero(coor,
                s.maxHp(),
                s.strength(),
                s.dexterity(),
                s.curHp(),
                s.name(),
                s.score());
        hero.setKeys(s.keys().clone());
        equipHero(hero, s);
        restoreBuffs(hero, s.buffs());
        return hero;
    }

    private static void equipHero(Hero hero, HeroSnapshot s) {
        fillWeaponsAndEquip(hero, s);
        fillBackpack(hero, s.foodBag());
        fillBackpack(hero, s.scrollBag());
        fillBackpack(hero, s.elixirBag());
    }

    private static void fillWeaponsAndEquip(Hero hero, HeroSnapshot s) {
        ItemSnapshot es = s.equippedWeapon();
        for (ItemSnapshot is : s.weaponBag()) {
            Item item = fromItemSnapshot(is);
            hero.addItemToBackpack(item);
            if (es != null && is.type().equals(es.type())
                    && is.value() == es.value()
                    && is.name().equals(es.name())) {
                if (item instanceof Weapon w) {
                    hero.equipWeapon(w);
                }
            }
        }
    }

    private static void fillBackpack(Hero hero, List<ItemSnapshot> bag) {
        for (ItemSnapshot is : bag) {
            Item item = fromItemSnapshot(is);
            hero.addItemToBackpack(item);
        }
    }

    private static void restoreBuffs(Hero hero, List<BuffSnapshot> buffs) {
        if (buffs == null)
            return;
        for (BuffSnapshot buff : buffs) {
            BoostType type = BoostType.valueOf(buff.type());
            hero.addBuff(type, buff.value(), buff.remainingTime());
        }
    }

    private static Enemy fromEnemySnapshot(EnemySnapshot s) {
        Coordinates coor = new Coordinates(s.y(), s.x());
        Enemy enemy;
        switch (s.type()) {
            case "ZOMBIE" -> enemy = new Zombie(coor, s.maxHp(), s.str(), s.dex(), s.hos());
            case "VAMPIRE" -> enemy = new Vampire(coor, s.maxHp(), s.str(), s.dex(), s.hos());
            case "GHOST" -> enemy = new Ghost(coor, s.maxHp(), s.str(), s.dex(), s.hos());
            case "OGRE" -> enemy = new Ogre(coor, s.maxHp(), s.str(), s.dex(), s.hos());
            case "SNAKE_MAGE" -> enemy = new SnakeMage(coor, s.maxHp(), s.str(), s.dex(), s.hos());
            case "MIMIC" -> enemy = new Mimic(coor, s.maxHp(), s.str(), s.dex(), s.hos());
            default -> throw new IllegalStateException("Wrong enemy type");
        }
        enemy.setHealth(s.curHp());
        return enemy;
    }

    public static LevelManager fromLevelSnapshot(LevelStateDTO s) {
        Hero hero = fromHeroSnapshot(s.hero());
        int prevDifficulty = s.balancePrevDifficulty();
        int maxHpOnStart = s.balanceMaxHpOnStart();
        int startValue = s.balanceStartValue();
        validateBalanceData(prevDifficulty, maxHpOnStart, startValue);
        BalanceService balanceService = new BalanceService(hero, prevDifficulty, maxHpOnStart, startValue);
        LevelManager lm = new LevelManager(s.levelNumber(), hero, balanceService);
        lm.addEntity(hero);
        int[][] field = new int[s.field().length][];
        for (int i = 0; i < s.field().length; i++) {
            field[i] = s.field()[i].clone();
        }
        boolean[][] explored = new boolean[s.explored().length][];
        for (int i = 0; i < s.explored().length; i++) {
            explored[i] = s.explored()[i].clone();
        }
        lm.setField(field);
        lm.setExplored(explored);
        for (int i = 0; i < s.enemies().size(); i++) {
            lm.addEntity(fromEnemySnapshot(s.enemies().get(i)));
        }
        for (int i = 0; i < s.items().size(); i++) {
            lm.addEntity(fromItemSnapshot(s.items().get(i)));
        }
        for (int i = 0; i < s.doors().size(); i++) {
            Door door = new Door(new Coordinates(s.doors().get(i).y(), s.doors().get(i).x()),
                    AccessLevel.valueOf(s.doors().get(i).accessLevel()));
            lm.addEntity(door);
        }
        Exit exit = new Exit(new Coordinates(s.exit().y(), s.exit().x()));
        lm.addEntity(exit);
        return lm;
    }

    private static void validateBalanceData(int prevDifficulty, int maxHpOnStart, int startValue) {
        if (prevDifficulty < -2 || prevDifficulty > 2) {
            throw new IllegalStateException("Invalid balance prevDifficulty: " + prevDifficulty);
        }
        if (maxHpOnStart <= 0) {
            throw new IllegalStateException("Invalid balance maxHpOnStart: " + maxHpOnStart);
        }
        if (startValue <= 0) {
            throw new IllegalStateException("Invalid balance startValue: " + startValue);
        }
    }    
}
