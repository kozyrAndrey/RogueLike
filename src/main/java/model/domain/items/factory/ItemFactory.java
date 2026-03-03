package model.domain.items.factory;

import model.domain.Coordinates;
import model.domain.items.Item;

import java.util.Random;

public interface ItemFactory {
    Item createItem(Coordinates coor, Random rng, int level);
}
