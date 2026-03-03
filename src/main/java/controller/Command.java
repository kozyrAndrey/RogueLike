package controller;

public class Command {
    private final CommandType type;
    private final Character character;

    public Command(CommandType type, Character character) {
        this.type = type;
        this.character = character;
    }
    public Command(CommandType type) { this(type, null); }

    public CommandType getType() { return type; }
    public Character getCharacter() { return character; }
    public boolean hasCharacter() { return character != null; }

    public boolean isType(CommandType commandType) { return this.type == commandType; }

    enum CommandType {
        // Движение
        MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT,
        OPEN_BACKPACK_MENU,
        // Меню предметов
        OPEN_WEAPON_MENU, OPEN_FOOD_MENU, OPEN_ELIXIR_MENU, OPEN_SCROLL_MENU,
        // Выбор предметов
        SELECT_ITEM_1, SELECT_ITEM_2, SELECT_ITEM_3, SELECT_ITEM_4, SELECT_ITEM_5,
        SELECT_ITEM_6, SELECT_ITEM_7, SELECT_ITEM_8, SELECT_ITEM_9, UNEQUIP_WEAPON,
        // Навигация в меню
        MENU_UP, MENU_DOWN, MENU_SELECT, MENU_BACK,
        // Общие команды
        CONFIRM, CANCEL, QUIT_GAME,
        // Ввод текста
        INPUT_CHAR, INPUT_BACKSPACE,
        // Неизвестная команда
        UNKNOWN
    }
}
