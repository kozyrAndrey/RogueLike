package datalayer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import datalayer.dto.AttemptStatsDTO;
import datalayer.dto.GameDataDTO;
import datalayer.dto.SavedSessionDTO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

public class GameDataRepositoryImpl implements GameDataRepository {

    private static final String SAVE_DIR = "saves";
    private static final String SAVE_FILE_NAME = "game_data.json";
    private static final Path SAVE_FILE = Paths.get(SAVE_DIR, SAVE_FILE_NAME);

    private final Gson gson = new Gson();

    private GameDataDTO readData() {
        String gameData;
        try {
            gameData = Files.readString(SAVE_FILE);
        } catch (IOException e) {
            return new GameDataDTO(null, emptyList());
        }

        try {
            GameDataDTO parsed = gson.fromJson(gameData, GameDataDTO.class);
            if (parsed == null) {
                return new GameDataDTO(null, emptyList());
            }
            return parsed;
        } catch (JsonSyntaxException e) {
            return new GameDataDTO(null, emptyList());
        }
    }

    private void writeData(GameDataDTO gameData) {
        try {
            Path dir = SAVE_FILE.getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }
            String jsonGameData = gson.toJson(gameData);
            Files.writeString(SAVE_FILE, jsonGameData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SavedSessionDTO> loadLastSession() {
        GameDataDTO gameData = readData();
        return Optional.ofNullable(gameData.lastSession());
    }

    @Override
    public void saveLastSession(SavedSessionDTO session) {
        GameDataDTO old = readData();
        GameDataDTO updated = new GameDataDTO(session, old.attempts());
        writeData(updated);
    }

    @Override
    public void clearLastSession() {
        GameDataDTO old = readData();
        GameDataDTO updated = new GameDataDTO(null, old.attempts());
        writeData(updated);
    }

    @Override
    public boolean hasLastSession() {
        GameDataDTO gameData = readData();
        return gameData.lastSession() != null;
    }

    @Override
    public List<AttemptStatsDTO> loadAllAttempts() {
        GameDataDTO gameData = readData();
        if (gameData.attempts() == null) {
            return emptyList();
        }
        return gameData.attempts();
    }

    @Override
    public void appendAttempt(AttemptStatsDTO attempt) {
        GameDataDTO gameData = readData();
        List<AttemptStatsDTO> attempts = gameData.attempts();
        List<AttemptStatsDTO> newAttempts = new ArrayList<>();
        if (attempts != null) {
            newAttempts.addAll(attempts);
        }
        newAttempts.add(attempt);
        GameDataDTO newGameData = new GameDataDTO(gameData.lastSession(), newAttempts);
        writeData(newGameData);
    }
}
