package lv.ctco.javaschool.game.boundary;

import lombok.extern.java.Log;
import lv.ctco.javaschool.auth.control.UserStore;
import lv.ctco.javaschool.auth.entity.domain.User;
import lv.ctco.javaschool.game.control.GameStore;
import lv.ctco.javaschool.game.entity.*;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Path("/game")
@Stateless
@Log
public class GameApi {
    @PersistenceContext
    private EntityManager em;
    @Inject
    private UserStore userStore;
    @Inject
    private GameStore gameStore;

    @POST
    @RolesAllowed({"ADMIN", "USER"})
    public void startGame() {
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getIncompleteGame();

        game.ifPresent(g -> {
            g.setPlayer2(currentUser);
            g.setStatus(GameStatus.PLACEMENT);
            g.setPlayer1Active(true);
            g.setPlayer2Active(true);
        });

        if (!game.isPresent()) {
            Game newGame = new Game();
            newGame.setPlayer1(currentUser);
            newGame.setStatus(GameStatus.INCOMPLETE);
            em.persist(newGame);
        }
    }

    @POST
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/cells")
    public void setShips(JsonObject field) {
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getStartedGameFor(currentUser, GameStatus.PLACEMENT);
        game.ifPresent(g -> {
            if (g.isPlayerActive(currentUser)) {
                List<String> ships = new ArrayList<>();
                for (Map.Entry<String, JsonValue> pair : field.entrySet()) {
                    log.info(pair.getKey() + " - " + pair.getValue());
                    String address = pair.getKey();
                    String value = ((JsonString) pair.getValue()).getString();
                    if ("SHIP".equals(value)) {
                        ships.add(address);
                    }
                }
                gameStore.setShips(g, currentUser, false, ships);
                g.setPlayerActive(currentUser, false);
                if (!g.isPlayer1Active() && !g.isPlayer2Active()) {
                    g.setStatus(GameStatus.STARTED);
                    g.setPlayer1Active(true);
                    g.setPlayer2Active(false);
                }
            }
        });
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/status")
    public GameDto getGameStatus() {
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getGameForUser(currentUser);
        return game.map(g -> {
            GameDto dto = new GameDto();
            dto.setStatus(g.getStatus());
            dto.setPlayerActive(g.isPlayerActive(currentUser));
            return dto;
        }).orElseThrow(IllegalStateException::new);
    }

    @POST
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/fire/{address}")
    public void doFire(@PathParam("address") String address) {
        log.info("Firing to " + address);
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getGameForUser(currentUser);
        game.ifPresent(g -> {
            User oppositeUser = g.getOpposite(currentUser);
            Optional<Cell> enemyCell = gameStore.findCell(g, oppositeUser, address, false);
            if (enemyCell.isPresent()) {
                Cell c = enemyCell.get();
                if (c.getState() == CellState.SHIP) {
                    c.setState(CellState.HIT);
                    gameStore.setCellState(g, currentUser, address, true, CellState.HIT);
                    return;
                } else if (c.getState() == CellState.EMPTY) {
                    c.setState(CellState.MISS);
                    gameStore.setCellState(g, currentUser, address, true, CellState.MISS);
                }
            } else {
                gameStore.setCellState(g, oppositeUser, address, false, CellState.MISS);
                gameStore.setCellState(g, currentUser, address, true, CellState.MISS);
            }
            boolean player1Active = g.isPlayer1Active();
            g.setPlayer1Active(!player1Active);
            g.setPlayer2Active(player1Active);
        });
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/state/{address}")
    public CellStateDto getCellState(@PathParam("address") String address) {
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getGameForUser(currentUser);
        CellStateDto cellStateDto = new CellStateDto();
        return game.map(g -> {
            Optional<Cell> cell = gameStore.getCell(g, currentUser, address);
            System.out.println(cell.toString());
            cellStateDto.setAddress(address);
            cell.ifPresent(c -> cellStateDto.setState(c.getState()));
            return cellStateDto;
        }).orElseThrow(IllegalStateException::new);
    }
}
