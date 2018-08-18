package lv.ctco.javaschool.game.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class FinishedGameDto {

    private String winnerName;
    private int winnerMoves;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinishedGameDto that = (FinishedGameDto) o;
        return Objects.equals(winnerName, that.winnerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winnerName);
    }
}
