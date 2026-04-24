package datalayer;

import datalayer.dto.AttemptStatsDTO;
import datalayer.dto.SavedSessionDTO;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс репозитория игровых данных.
 *
 * Определяет операции сохранения и загрузки последней игровой сессии,
 * очистки сохранений и работы со статистикой завершённых попыток.
 * Используется моделью игры для изоляции логики хранения данных.
 *
 * @see datalayer.GameDataRepositoryImpl
 */

public interface GameDataRepository {
    Optional<SavedSessionDTO> loadLastSession();
    void saveLastSession(SavedSessionDTO session);
    void clearLastSession();
    boolean hasLastSession();

    List<AttemptStatsDTO> loadAllAttempts();
    void appendAttempt(AttemptStatsDTO attempt);
}
