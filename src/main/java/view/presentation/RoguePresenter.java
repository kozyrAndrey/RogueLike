package view.presentation;

import controller.ui.UIState;
import model.domain.GameState;
import model.domain.items.Item;
import view.presentation.dto.ItemDisplayDTO;
import view.presentation.dto.GamePresentation;
import view.presentation.mapper.PresentationMapper;
import datalayer.dto.AttemptStatsDTO;

import java.util.List;

public class RoguePresenter {
    private final PresentationMapper mapper;

    public RoguePresenter() {
        this.mapper = new PresentationMapper();
    }

    public RoguePresenter(PresentationMapper mapper) {
        this.mapper = mapper;
    }

    public GamePresentation present(GameState gameState, UIState uiState) {
        if (gameState == null) return GamePresentation.empty();

        return mapper.mapToPresentation(gameState, uiState);
    }

    public List<ItemDisplayDTO> presentItems(List<Item> items) {
        return mapper.mapItemsToDisplayDTO(items);
    }

    public ItemDisplayDTO presentItem(Item item, int index) {
        return mapper.mapItemToDisplayDTO(item, index);
    }

    public List<String> presentAttempts(List<AttemptStatsDTO> attempts) {
        return mapper.mapAttemptsToLines(attempts);
    }

    public GamePresentation emptyPresentation() {
        return GamePresentation.empty();
    }
}
