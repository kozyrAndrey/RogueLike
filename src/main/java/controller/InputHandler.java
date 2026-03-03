package controller;

import java.io.IOException;

public interface InputHandler {
    Command getNextCommand() throws IOException;
}
