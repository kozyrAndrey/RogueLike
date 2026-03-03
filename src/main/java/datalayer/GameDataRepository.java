package datalayer;

import datalayer.dto.AttemptStatsDTO;
import datalayer.dto.SavedSessionDTO;

import java.util.List;
import java.util.Optional;

public interface GameDataRepository {
    Optional<SavedSessionDTO> loadLastSession();
    void saveLastSession(SavedSessionDTO session);
    void clearLastSession();
    boolean hasLastSession();

    List<AttemptStatsDTO> loadAllAttempts();
    void appendAttempt(AttemptStatsDTO attempt);
}
