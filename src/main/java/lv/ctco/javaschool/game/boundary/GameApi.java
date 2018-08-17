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
import java.util.*;
import java.util.stream.Collectors;

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
                gameStore.setShips(g, currentUser, TargetArea.OPPONENT, ships);
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
        Optional<Game> game = gameStore.getLatestGameFor(currentUser);
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
        Optional<Game> game = gameStore.getLatestGameFor(currentUser);
        game.ifPresent(g -> {
            User oppositeUser = g.getOpposite(currentUser);
            Optional<Cell> enemyCell = gameStore.getCell(g, oppositeUser, address, TargetArea.OPPONENT);
            if (enemyCell.isPresent()) {
                Cell c = enemyCell.get();
                if (c.getState() == CellState.SHIP) {
                    c.setState(CellState.HIT);
                    gameStore.setCellState(g, currentUser, address, TargetArea.USER, CellState.HIT);
                    checkFinishedGameStatus(g, oppositeUser);
                    return;
                } else if (c.getState() == CellState.EMPTY) {
                    c.setState(CellState.MISS);
                    gameStore.setCellState(g, currentUser, address, TargetArea.USER, CellState.MISS);
                }
            } else {
                gameStore.setCellState(g, oppositeUser, address, TargetArea.OPPONENT, CellState.MISS);
                gameStore.setCellState(g, currentUser, address, TargetArea.USER, CellState.MISS);
            }
            if (currentUser.equals(g.getPlayer1())) {
                g.setPlayer1moves(g.getPlayer1moves() + 1);
            } else {
                g.setPlayer2moves(g.getPlayer2moves() + 1);
            }
            boolean player1Active = g.isPlayer1Active();
            g.setPlayer1Active(!player1Active);
            g.setPlayer2Active(player1Active);
        });
    }

    private void checkFinishedGameStatus(Game game, User user) {
        if (gameStore.getCells(game, user)
                .stream()
                .filter(ta -> ta.getTargetArea() == TargetArea.OPPONENT)
                .noneMatch(cs -> cs.getState() == CellState.SHIP)) {
            game.setStatus(GameStatus.FINISHED);
            game.setWinner(userStore.getCurrentUser());
        }
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/cells")
    public List<CellStateDto> getCells() {
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getStartedGameFor(currentUser, GameStatus.STARTED);
        return game.map(g -> {
            List<Cell> cells = gameStore.getCells(g, currentUser);
            return cells.stream()
                    .map(this::convertToCellDto)
                    .collect(Collectors.toList());
        }).orElseThrow(IllegalStateException::new);
    }

    private CellStateDto convertToCellDto(Cell cell) {
        CellStateDto dto = new CellStateDto();
        dto.setTargetArea(cell.getTargetArea());
        dto.setAddress(cell.getAddress());
        dto.setState(cell.getState());
        return dto;
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/user")
    public UserDto getUserName() {
        User currentUser = userStore.getCurrentUser();
        Optional<Game> game = gameStore.getLatestGameFor(currentUser);
        return game.map(g -> {
            UserDto dto = new UserDto();
            dto.setUsername(game.get().getWinner().getUsername());
            return dto;
        }).orElseThrow(IllegalStateException::new);
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/games")
    public List<GameDto> getFinishedGames() {
        List<Game> finishedGames = gameStore.getFinishedGames();
        return finishedGames.stream().sorted(Comparator.comparing(Game::getWinnerMoves))
                .map(this::convertToGameDto)
                .collect(Collectors.toList());
    }

    private GameDto convertToGameDto(Game game) {
        GameDto dto = new GameDto();
        dto.setWinner(game.getWinner());
        dto.setWinnerMoves(game.getWinnerMoves());
        return dto;
    }

    public List<Game> getSortedFinishedGames() {
        List<Game> finishedGames = gameStore.getFinishedGames();
        return finishedGames.stream().sorted(Comparator.comparing(Game::getWinnerMoves))
                .collect(Collectors.toList());
    }

    @GET
    @RolesAllowed({"ADMIN", "USER"})
    @Path("/winners")
    public List<UserDto> getWinners() {
        List<User> winners = new ArrayList<>();
        List<Game> sortedFinishedGames = getSortedFinishedGames();
        for (Game game : sortedFinishedGames) {
            winners.add(game.getWinner());
        }
        return winners.stream().sorted(Comparator.comparing(User::getVictories))
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setVictories(user.getVictories());
        return dto;
    }

}
