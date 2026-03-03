package datalayer.dto;

import java.util.List;

public record GameDataDTO(SavedSessionDTO lastSession, List<AttemptStatsDTO> attempts) {
}
