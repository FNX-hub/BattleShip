package it.tranigrillo.battleship.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matrix {
    private final Context context;
    private MatrixStatus[][] status;
    private List<Ship> fleet = new ArrayList<>();

    public Matrix(Context context, int dim) {
        this.context = context;
        status = new MatrixStatus[dim][dim];
        for (int i = 0; i < dim; i++) {
             for (int j = 0; j < dim; j++) {
                 status[i][j] = MatrixStatus.NONE;
             }
        }
    }

    public Matrix(Context context, MatrixStatus[][] status, List<Ship> fleet) {
        this.context = context;
        this.status = status;
        this.fleet = fleet;
    }

    private Ship createShip(int x, int y, ShipDimension size, ShipOrientation orientation) {
        Ship ship;
        switch (size) {
            case SMALL:
                ship = new Ship.SmallShip(x, y);
                break;
            case MEDIUM:
                ship = new Ship.MediumShip(x, y, orientation);
                break;
            case LARGE:
                ship = new Ship.LargeShip(x, y, orientation);
                break;
            case EXTRA_LARGE:
                ship = new Ship.ExtraLargeShip(x, y, orientation);
                break;
            default:
                return null;
        }
        return ship;
    }

    private boolean validateShip(Ship ship) {
        if (ship == null) return false;
        try {
            for (Integer[] integers : ship.getPosition()) {
                if (status[integers[0]][integers[1]] != MatrixStatus.NONE) {
                    throw new IllegalArgumentException("Invalid zone");
                }
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public Ship findShipByElement(int posX, int posY) {
        for (Ship ship : fleet) {
            if (ship.findShipByElement(posX, posY)) {
                return ship;
            }
        }
        return null;
    }

    public Ship addShip(int x, int y, ShipDimension size, ShipOrientation orientation) {
        Ship ship = null;
        if (orientation == null) {
            for (ShipOrientation tryOrientation : ShipOrientation.values()) {
                if (tryOrientation == ShipOrientation.NONE) {
                    continue;
                }
                ship = createShip(x, y, size, tryOrientation);
                if (validateShip(ship)) {
                    break;
                }
            }
        }
        else {
            ship = createShip(x, y, size, orientation);
        }
        if (validateShip(ship)) {
            for (Integer[] integers : ship.getPosition()) {
                status[integers[0]][integers[1]] = MatrixStatus.SHIP;
            }
            fleet.add(ship);
            return ship;
        }
        else {
            Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public Ship removeShip(int posX, int posY) {
        Ship ship = findShipByElement(posX, posY);
        if (ship != null) {
            fleet.remove(ship);
            for (Integer[] integers : ship.getPosition()) {
                status[integers[0]][integers[1]] = MatrixStatus.NONE;
            }
            return ship;
        }
        return null;
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    public MatrixStatus[][] getStatus(){
        return status;
    }

    MatrixStatus getElement(int i, int j){
        return status[i][j];
    }


    // debug function
    public Matrix print() {
        Log.d("TAG", "matrice\n"+ Arrays.deepToString(status)
                .replace("[[", "|")
                .replace("[", "|")
                .replace("], ", "|\n")
                .replace("]]", "|"));
        return this;
    }
}