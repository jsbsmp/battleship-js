package lv.ctco.javaschool.game.control;

import lv.ctco.javaschool.auth.entity.domain.User;
import lv.ctco.javaschool.game.entity.Cell;
import lv.ctco.javaschool.game.entity.CellState;
import lv.ctco.javaschool.game.entity.Game;
import lv.ctco.javaschool.game.entity.GameStatus;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class GameStore {
    @PersistenceContext
    private EntityManager em;

    public Optional<Game> getIncompleteGame() {
        return em.createQuery(
                "select g " +
                        "from Game g " +
                        "where g.status = :status", Game.class)
                .setParameter("status", GameStatus.INCOMPLETE)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public Optional<Game> getStartedGameFor(User user, GameStatus status) {
        return em.createQuery(
                "select g " +
                        "from Game g " +
                        "where g.status = :status " +
                        "  and (g.player1 = :user " +
                        "   or g.player2 = :user)", Game.class)
                .setParameter("status", status)
                .setParameter("user", user)
                .getResultStream()
                .findFirst();
    }

    public Optional<Game> getGameForUser(User user) {
        return em.createQuery(
                "select g " +
                        "from Game g " +
                        "where g.status <> :status " +
                        "  and (g.player1 = :user " +
                        "   or g.player2 = :user)", Game.class)
                .setParameter("status", GameStatus.FINISHED)
                .setParameter("user", user)
                .getResultStream()
                .findFirst();
    }


    public void setCellState(Game game, User player, String address, boolean targetArea, CellState state) {
        Optional<Cell> cell = em.createQuery(
                "select c from Cell c " +
                        "where c.game = :game " +
                        "  and c.user = :user " +
                        "  and c.targetArea = :target " +
                        "  and c.address = :address", Cell.class)
                .setParameter("game", game)
                .setParameter("user", player)
                .setParameter("target", targetArea)
                .setParameter("address", address)
                .getResultStream()
                .findFirst();
        if (cell.isPresent()) {
            cell.get().setState(state);
        } else {
            Cell newCell = new Cell();
            newCell.setGame(game);
            newCell.setUser(player);
            newCell.setAddress(address);
            newCell.setTargetArea(targetArea);
            newCell.setState(state);
            em.persist(newCell);
        }
    }

    public void setShips(Game game, User user, boolean targetArea, List<String> ships) {
        clearField(game, user, targetArea);
        ships.stream()
                .map(address -> {
                    Cell c = new Cell();
                    c.setGame(game);
                    c.setAddress(address);
                    c.setTargetArea(targetArea);
                    c.setUser(user);
                    c.setState(CellState.SHIP);
                    return c;
                }).forEach(e -> em.persist(e));
    }

    private void clearField(Game game, User player, boolean targetArea) {
        List<Cell> cells = em.createQuery("select c from Cell c " +
                "where c.game = :game " +
                "and c.user=:user " +
                "and c.targetArea=:target", Cell.class)
                .setParameter("game", game)
                .setParameter("user", player)
                .setParameter("target", targetArea)
                .getResultList();
        cells.forEach(c -> em.remove(c));
    }
}
