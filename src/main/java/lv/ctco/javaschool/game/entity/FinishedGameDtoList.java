package lv.ctco.javaschool.game.entity;

import lombok.Data;

import java.util.List;

@Data
public class FinishedGameDtoList {

    List<FinishedGameDto> winners;
}
