package it.tranigrillo.battleship.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Ship {

    private Integer[] start = new Integer[2];
    private int life;
    private ShipOrientation orientation;
    private List<Integer[]> position = new ArrayList<>();

    private Ship(int posX, int posY) {
        start[0] = posX;
        start[1] = posY;
    }

    public void setOrientation(ShipOrientation orientation, int len) {
        this.orientation = orientation;
        for(int i = 0; i < len; i++) {
            switch (orientation) {
                case HORIZONTAL_RIGHT:
                    position.add(i, new Integer[] {start[0], start[1] + i});
                    break;
                case VERTICAL_BOTTOM:
                    position.add(i, new Integer[] {start[0] + i, start[1]});
                    break;
                case HORIZONTAL_LEFT:
                    position.add(i, new Integer[] {start[0], start[1] - i});
                    break;
                case VERTICAL_TOP:
                    position.add(i, new Integer[] {start[0] - i, start[1]});
                    break;
                default: return;
            }
        }
    }

    public List<Integer[]> getPosition(){
        return position;
    }

    public boolean findShipByElement(int posX, int posY) {
        for (Integer[] integers : position) {
            if (integers[0] == posX && integers[1] == posY) return true;
        }
        return false;
    }

    public ShipOrientation getShipOrientation() {
        return orientation;
    }

    public boolean isHit(int posX, int posY) {
        if (findShipByElement(posX, posY)) {
            life--;
            return true;
        }
        return false;
    }

    public boolean isSink() {
        return life == 0;
    }

//    ---------------------------------------------------------------------------------------

    public static class SmallShip extends Ship {
        SmallShip(int posX, int posY) {
            super(posX, posY);
            super.life = 1;
            super.orientation = ShipOrientation.NONE;

            super.position.add(0, new Integer[]{posX, posY});

        }
    }

    public static class MediumShip extends Ship {
        MediumShip(int posX, int posY, ShipOrientation mode) {
            super(posX, posY);
            super.life = 2;
            setOrientation(mode, 2);
        }
    }

    public static class LargeShip extends Ship {
        LargeShip(int posX, int posY, ShipOrientation mode) {
            super(posX, posY);
            super.life = 3;
            setOrientation(mode, 3);
        }
    }

    public static class ExtraLargeShip extends Ship {
        ExtraLargeShip(int posX, int posY, ShipOrientation mode) {
            super(posX, posY);
            super.life = 4;
            setOrientation(mode, 4);
        }
    }
}