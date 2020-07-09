package it.tranigrillo.battleship.view;

import android.util.Log;

import java.util.Arrays;

import it.tranigrillo.battleship.model.GameRule;
import it.tranigrillo.battleship.model.IAmanager;
import it.tranigrillo.battleship.model.MatrixStatus;
import it.tranigrillo.battleship.model.Ship;
import it.tranigrillo.battleship.model.ShipOrientation;

public class TurnManager {
    Board brdMini;
    Board brdMaxi;
    IAmanager opponent;
    Boolean whoStart;

    public TurnManager(Board mini, Board maxi, GameRule gameRule){
        brdMini = mini;
        brdMaxi = maxi;
        opponent = new IAmanager("Pluto",gameRule.getLv(),10);
        opponent.fleetSetter(gameRule.getSmallShip(),gameRule.getMediumShip(),gameRule.getLargeShip(),gameRule.getExtraLargeShip());
        opponent.fillWater();
        whoStart = true;
    }

    public void playerTurn(Integer[] move,Box box){
        String result;
        Integer[] startPos = new Integer[2];
        Integer sankShipLen=0;
        ShipOrientation orientation=null;

        Log.d("TURN","Mossa ricevuta: " + move[0] + "," + move[1]);

        // il bot riceve la mossa e ne comunica l'esito
        result = opponent.moveReceiver(move);
        Log.d("TURN","RISPOSTA DEL BOT: "+result);

        //il player si segna l'esito
        if(result.equals("hit")){
            Log.d("TURN","HIT");
            brdMaxi.setHit(box);
        }
        if(result.equals("miss")){
            Log.d("TURN","MISS");
            brdMaxi.setMiss(box);
        }
        if(result.contains("sank")){
            Log.d("TURN","SANK");
            //Ottieni la prima cella della nave
            //N.B: siamo nel formato (X,Y) dove X e Y sono numeri compresi tra 0 e 9 - dato che la matrice è 10x10
            //TODO per eventuali altri formati si crea un if differente (per ora non è strettamente richiesto dalle specifiche)
            if (result.charAt(4) == '(' && result.charAt(6) == ',' && result.charAt(8) == ')') {
                Log.d("TURN","STARTPOS: "+result.substring(5, 6)+","+result.substring(7, 8));
                startPos[0] = Integer.valueOf(result.substring(5, 6));
                startPos[1] = Integer.valueOf(result.substring(7, 8));
                Log.d("TURN","STARTPOS: "+startPos[0]+","+startPos[1]);
            }
            //Ottieni la lunghezza della nave
            if (result.contains("-1-")) {
                sankShipLen = 1;
            }
            if (result.contains("-2-")) {
                sankShipLen = 2;
            }
            if (result.contains("-3-")) {
                sankShipLen = 3;
            }
            if (result.contains("-4-")) {
                sankShipLen = 4;
            }

            //Ottieni l'Orientation della nave
            if (result.contains("VERTICAL_BOTTOM")) {
                orientation = ShipOrientation.VERTICAL_BOTTOM;
            }
            if (result.contains("VERTICAL_TOP")) {
                orientation = ShipOrientation.VERTICAL_TOP;
            }
            if (result.contains("HORIZONTAL_LEFT")) {
                orientation = ShipOrientation.HORIZONTAL_LEFT;
            }
            if (result.contains("HORIZONTAL_RIGHT")) {
                orientation = ShipOrientation.HORIZONTAL_RIGHT;
            }
            if (result.contains("NONE")){
                orientation = ShipOrientation.NONE;
            }

            Log.d("TURN","PRIMA di SetSank");
            assert orientation != null;
            brdMaxi.setSank(startPos[0],startPos[1],sankShipLen,orientation);
            Log.d("TURN","DOPO di SetSank");
        }
    }

    public void iaTurn(){
        Integer[] myMove = new Integer[2];
        StringBuilder result = new StringBuilder();
        MatrixStatus myMatrixStatus;

        //IA attacca
        myMove = opponent.moveSelector();
        Log.d("TURN","");

        //Guarda la matrice del giocatore
        myMatrixStatus = brdMini.getLogicMatrix().getElement(myMove[0],myMove[1]);
        switch(myMatrixStatus){
            case WATER:
                brdMini.setMiss(brdMini.getBox(myMove[0], myMove[1]));
                result.append("miss");
                break;
            case SHIP:
                //ottieni la nave che è stata colpita
                Ship tempShip = brdMini.getLogicMatrix().findShipByElement(myMove[0],myMove[1]);
                tempShip.isHit(myMove[0],myMove[1]);
                if(tempShip.isSank()){
                    result.append("sank").append("(").append(tempShip.getPosition().get(0)[0]).append(",").append(tempShip.getPosition().get(0)[1]).append(")").append("-").append(tempShip.getLenght()).append("-").append(tempShip.getShipOrientation());
                    Log.d("TURN","Prima del SetSank");
                    brdMini.setSank(tempShip);
                }
                else{
                    result.append("hit");
                    brdMini.setHit(brdMini.getBox(myMove[0], myMove[1]));
                }
                break;
        }

        //IA riceve responso della propria mossa
        opponent.valueMyLastMoveOutcome(result.toString());
    }
}
