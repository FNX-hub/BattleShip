package it.tranigrillo.battleship.model;

public enum MatrixStatus {
    //PER LE NAVI CHE HO PIAZZATO IO
    WATER, //in questa zona non ho piazzato nulla
    SHIP, //in questa zona ho piazzato una nave

    //PER I COLPI SUBITI ED INFLITTI
    NONE, //non hai mai colpito questa zona
    MISS, //zona "water" che è stata colpita
    HIT, //zona "ship" che è stata colpita - nave NON affondata
    SANK; //zona "ship" che è stata colpita - nave affondata
}
