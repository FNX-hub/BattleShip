package it.tranigrillo.battleship;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GridMatrix {
    private final Context context;
    private MatrixStatus[][] matrix;
    private ArrayList<Ship> fleet = new ArrayList<>();

    GridMatrix(Context context, int dim) {
        this.context = context;
        matrix = new MatrixStatus[dim][dim];
        for (int i = 0; i < dim; i++) {
             for (int j = 0; j < dim; j++) {
                 matrix[i][j] = MatrixStatus.NONE;
             }
        }
    }

    private Ship createShip(int x, int y, Fleet size, Orientation orientation) {
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
                if (matrix[integers[0]][integers[1]] != MatrixStatus.NONE) {
                    throw new IllegalArgumentException("Invalid zone");
                }
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    Ship findShipByElement(int posX, int posY) {
        for (Ship ship : fleet) {
            if (ship.findShipByElement(posX, posY)) {
                return ship;
            }
        }
        return null;
    }

    Ship addShip(int x, int y, Fleet size, Orientation orientation) {
        Ship ship = null;
        if (orientation == null) {
            for (Orientation tryOrientation : Orientation.values()) {
                if (tryOrientation == Orientation.NONE) {
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
                matrix[integers[0]][integers[1]] = MatrixStatus.SHIP;
            }
            fleet.add(ship);
            return ship;
        }
        else {
            Toast.makeText(context, "Invalid position", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    Ship removeShip(int posX, int posY) {
        Ship ship = findShipByElement(posX, posY);
        if (ship != null) {
            fleet.remove(ship);
            for (Integer[] integers : ship.getPosition()) {
                matrix[integers[0]][integers[1]] = MatrixStatus.NONE;
            }
            return ship;
        }
        return null;
    }

    List<Ship> getFleet() {
        return fleet;
    }

    MatrixStatus[][] getMatrix(){
        return matrix;
    }

    MatrixStatus getElement(int i, int j){
        return matrix[i][j];
    }


    // debug function
    void print() {
        Log.d("TAG", "matrice\n"+ Arrays.deepToString(matrix)
                .replace("[[", "|")
                .replace("[", "|")
                .replace("], ", "|\n")
                .replace("]]", "|"));
    }
}