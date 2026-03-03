import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import controller.InputHandler;
import controller.LanternaInputHandler;
import controller.RogueController;
import controller.RogueLoop;
import datalayer.GameDataRepository;
import datalayer.GameDataRepositoryImpl;
import model.RogueModel;
import view.presentation.RogueView;

import java.io.IOException;

public class App {
    private static final int SCREEN_WIDTH = 82;
    private static final int SCREEN_HEIGHT = 36;

    public static void main(String[] args) {
        Terminal terminal = null;
        Screen screen = null;
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(SCREEN_WIDTH, SCREEN_HEIGHT));
            terminalFactory.setForceTextTerminal(false);
            terminalFactory.setPreferTerminalEmulator(true);

            // Создаем терминал и экран
            terminal = terminalFactory.createTerminal();
            terminal.setCursorVisible(false);
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            screen.setCursorPosition(null);

            RogueView view = new RogueView(screen);
            GameDataRepository gameDataRepository = new GameDataRepositoryImpl();
            RogueModel model = new RogueModel(gameDataRepository);
            RogueController controller = new RogueController(model);
            InputHandler inputHandler = new LanternaInputHandler(view);
            RogueLoop rogueLoop = new RogueLoop(inputHandler, controller, view);

            rogueLoop.run();
            // Завершаем работу

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (screen != null) screen.stopScreen();
                if (terminal != null) terminal.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}