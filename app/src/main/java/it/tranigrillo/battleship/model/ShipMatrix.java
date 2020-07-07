package it.tranigrillo.battleship.model;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ShipMatrix implements Serializable {
    private MatrixStatus[][] cellStatuses;
    private List<Ship> fleet = new ArrayList<>();
    private Integer matDim;


//--------------------
//  COSTRUTTORI
//--------------------
    public ShipMatrix(int matDim) {
        this.matDim = matDim;
        cellStatuses = new MatrixStatus[matDim][matDim];
        for (int i = 0; i < matDim; i++) {
             for (int j = 0; j < matDim; j++) {
                 cellStatuses[i][j] = MatrixStatus.NONE;
             }
        }
    }

    // imposta lo stato di una singola cella di cellStatus
    public void setValue(int i, int j, MatrixStatus cellStatus) {
        this.cellStatuses[i][j] = cellStatus;
    }

    // aggiunge se possibilie alla matrice una Ship e la ritorna, atrimenti ritorna null
    // usa prima la funzione createShip e poi verifica se è possibile inserirla nella posizione indicata con validateShip
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
                cellStatuses[integers[0]][integers[1]] = MatrixStatus.SHIP;
            }
            fleet.add(ship);
            return ship;
        }
        else {
            return null;
        }
    }

    // crea una Ship
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

    // verifica se la Ship è posizionabile in una posizione valida
    private boolean validateShip(Ship ship) {
        if (ship == null) return false;
        try {
            for (Integer[] integers : ship.getPosition()) {
                if (cellStatuses[integers[0]][integers[1]] != MatrixStatus.NONE) {
                    throw new IllegalArgumentException("Invalid zone");
                }
            }
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    // ritorna Ship se c'è in X,Y altrimenti null
    public Ship findShipByElement(int posX, int posY) {
        for (Ship ship : fleet) {
            if (ship.findShipByElement(posX, posY)) {
                return ship;
            }
        }
        return null;
    }

    // sfrutta findShipById, rimuove la nave se esite e la ritorna
    public Ship removeShip(int posX, int posY) {
        Ship ship = findShipByElement(posX, posY);
        if (ship != null) {
            fleet.remove(ship);
            for (Integer[] integers : ship.getPosition()) {
                cellStatuses[integers[0]][integers[1]] = MatrixStatus.NONE;
            }
            return ship;
        }
        return null;
    }

    // Getter per fleet
    public List<Ship> getFleet() {
        return fleet;
    }

    // Getter per cellStatuses
    public MatrixStatus[][] getCellStatuses(){
        return cellStatuses;
    }

    // Getter per singolo elemento di getStatuses
    public MatrixStatus getElement(int i, int j){
        return cellStatuses[i][j];
    }

    // Getter per la dimensione della matrice
    public Integer getMatDim() {
        return matDim;
    }

    // debug function
    public ShipMatrix print() {
        Log.d("TAG", "matrice\n"+ Arrays.deepToString(cellStatuses)
                .replace("[[", "|")
                .replace("[", "|")
                .replace("], ", "|\n")
                .replace("]]", "|"));
        return this;
    }
}