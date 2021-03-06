package lv.ctco.javaschool.game.entity;

import lombok.Getter;
import lombok.Setter;
import lv.ctco.javaschool.auth.entity.domain.User;

import java.util.Objects;

@Getter
@Setter
public class GameDto {

    private GameStatus status;
    private boolean playerActive;
    private User winner;
    private int winnerMoves;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDto gameDto = (GameDto) o;
        return Objects.equals(winner, gameDto.winner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner);
    }
}
