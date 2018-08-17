package lv.ctco.javaschool.game.entity;

public enum TargetArea {

    USER("user"),
    OPPONENT("opponent");

    private final String name;

    TargetArea(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
