package lv.ctco.javaschool.game.control;

import lv.ctco.javaschool.auth.entity.domain.User;
import lv.ctco.javaschool.game.entity.*;

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

    public Optional<Game> getLatestGameFor(User user) {
        return em.createQuery(
                "select g " +
                        "from Game g " +
                        "where g.player1 = :user " +
                        "or g.player2 = :user order by g.id desc", Game.class)
                .setParameter("user", user)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }


    public void setCellState(Game game, User player, String address, TargetArea targetArea, CellState state) {
        Optional<Cell> cell = getCell(game, player, address, targetArea);
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

    public void setShips(Game game, User user, TargetArea targetArea, List<String> ships) {
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

    private void clearField(Game game, User player, TargetArea targetArea) {
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

    public Optional<Cell> getCell(Game game, User user, String address, TargetArea targetArea) {
        return em.createQuery(
                "select c from Cell c " +
                        "where c.user=:user and c.game=:game and c.address=:address and c.targetArea=:target", Cell.class)
                .setParameter("game", game)
                .setParameter("user", user)
                .setParameter("address", address)
                .setParameter("target", targetArea)
                .getResultStream()
                .findFirst();
    }

    public List<Cell> getCells(Game game, User player) {
        return em.createQuery(
                "select c " +
                        "from Cell c " +
                        "where c.game = :game " +
                        "  and c.user = :user ", Cell.class)
                .setParameter("game", game)
                .setParameter("user", player)
                .getResultList();
    }

    public List<Game> getFinishedGames(){
        return em.createQuery(
                "select g from Game g where g.status=:status",Game.class)
                .setParameter("status",GameStatus.FINISHED)
                .getResultList();
    }
}
