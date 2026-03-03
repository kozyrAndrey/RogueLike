package controller;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import view.presentation.RogueView;

import java.io.IOException;

public class LanternaInputHandler implements InputHandler {
    private final RogueView view;
    public LanternaInputHandler(RogueView view) {
        this.view = view;
    }

    @Override
    public Command getNextCommand() throws IOException {
        KeyStroke stroke = view.readInput();
        if (stroke == null) return new Command(Command.CommandType.UNKNOWN);
        return convertToCommand(stroke);
    }

    private Command convertToCommand(KeyStroke stroke) {
        switch (stroke.getKeyType()) {
            case KeyType.Character -> {
                char ch = stroke.getCharacter();
                return convertCharacterToCommand(ch);
            }
            case KeyType.ArrowUp -> { return new Command(Command.CommandType.MENU_UP); }
            case KeyType.ArrowDown -> { return new Command(Command.CommandType.MENU_DOWN); }
            case KeyType.Enter -> { return new Command(Command.CommandType.MENU_SELECT); }
            case KeyType.Backspace -> { return new Command(Command.CommandType.INPUT_BACKSPACE); }
            case KeyType.Escape -> { return new Command(Command.CommandType.MENU_BACK); }
            default -> { return new Command(Command.CommandType.UNKNOWN); }
        }
    }

    private Command convertCharacterToCommand(char ch) {
        switch (Character.toLowerCase(ch)) {
            case 'w' -> { return new Command(Command.CommandType.MOVE_UP, ch); }
            case 'd' -> { return new Command(Command.CommandType.MOVE_RIGHT, ch); }
            case 's' -> { return new Command(Command.CommandType.MOVE_DOWN, ch); }
            case 'a' -> { return new Command(Command.CommandType.MOVE_LEFT, ch); }
            case 'b' -> { return new Command(Command.CommandType.OPEN_BACKPACK_MENU, ch); }
            case 'h' -> { return new Command(Command.CommandType.OPEN_WEAPON_MENU, ch); }
            case 'j' -> { return new Command(Command.CommandType.OPEN_FOOD_MENU, ch); }
            case 'k' -> { return new Command(Command.CommandType.OPEN_ELIXIR_MENU, ch); }
            case 'e' -> { return new Command(Command.CommandType.OPEN_SCROLL_MENU, ch); }
            case 'q' -> { return new Command(Command.CommandType.QUIT_GAME, ch); }
            case '0' -> { return new Command(Command.CommandType.UNEQUIP_WEAPON, ch); }
            case '1' -> { return new Command(Command.CommandType.SELECT_ITEM_1, ch); }
            case '2' -> { return new Command(Command.CommandType.SELECT_ITEM_2, ch); }
            case '3' -> { return new Command(Command.CommandType.SELECT_ITEM_3, ch); }
            case '4' -> { return new Command(Command.CommandType.SELECT_ITEM_4, ch); }
            case '5' -> { return new Command(Command.CommandType.SELECT_ITEM_5, ch); }
            case '6' -> { return new Command(Command.CommandType.SELECT_ITEM_6, ch); }
            case '7' -> { return new Command(Command.CommandType.SELECT_ITEM_7, ch); }
            case '8' -> { return new Command(Command.CommandType.SELECT_ITEM_8, ch); }
            case '9' -> { return new Command(Command.CommandType.SELECT_ITEM_9, ch); }
            default -> { return new Command(Command.CommandType.INPUT_CHAR, ch); }
        }
    }
}