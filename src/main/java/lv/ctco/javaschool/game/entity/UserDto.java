package lv.ctco.javaschool.game.entity;

import lombok.Data;

@Data
public class UserDto {

    private String username;
    private Long victories;
    private boolean isActive;
}
