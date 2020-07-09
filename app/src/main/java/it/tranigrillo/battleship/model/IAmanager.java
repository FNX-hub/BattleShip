package it.tranigrillo.battleship.model;

import android.util.Log;

import java.util.Random;

public class IAmanager {
    private String name;
    private Integer QIlevel; //lv1 IA stupida   //lv2 IA che ragiona
    private Integer[] myLastMove; //ultima mossa fatta - usata per banale memorizzazione
    private Integer[] myLastUsefulMove; //ultima mossa in cui hai colpito qualcosa - usata dallo stato 1
    private Integer DFAstate; //stato della DFA
    private ShipMatrix myShipMatrix;
    private ShipMatrix myRadar;

    /* IA di lv1 colpisce a caso */

    /* IA di lv2 ragiona seguendo uno schema con memoria basato su una macchina a stati finiti (DFA)
    stato 0 = colpisco a caso ---> se colpisco qualcosa vado nello stato 1
    stato 1 = il mio ultimo colpo è andato a segno ---> colpisco un punto nelle immediate vicinanze
    ---> se colpisco qualcosa vado nello stato 2
    stato 2 = ho affondato qualcosa, torno allo stato 0         */

    public IAmanager(String name, Integer lv, int dim) {
        this.QIlevel = lv;
        this.DFAstate = 0;
        this.myLastMove = new Integer[2];
        this.myLastUsefulMove = new Integer[2];
        this.name = name;
        this.myShipMatrix = new ShipMatrix(dim);
        this.myRadar = new ShipMatrix(dim);
    }

    public ShipMatrix getMyShipMatrix(){
        return myShipMatrix;
    }

    //riempie in modo randomico la matrice con le varie navi
    public void fleetSetter(int numOfSmallShips, int numOfMediumShips, int numOfLargeShips, int numOfExtraLargeShips) {
        //seleziona randomicamente una posizione per la nave
        Integer[] startPosition = new Integer[2];
        Random random = new Random();
        Integer dimMat = myShipMatrix.getMatDim();
        Ship setShip;

        //piazza tutte le navi ExtraLarge
        while (numOfExtraLargeShips > 0) {
            //scegli delle coordinate randomiche dove cominciare a piazzare la nave
            startPosition[0] = random.nextInt(dimMat);
            startPosition[1] = random.nextInt(dimMat);

            //piazza la nave nel punto scelto, con un Orientation scelta randomicamente
            setShip = myShipMatrix.addShip(startPosition[0], startPosition[1], ShipDimension.EXTRA_LARGE, null);
            if (setShip == null) {
                Log.d("TESTING-IA", "La nave non è piazzabile nella cella scelta\n");
            } else {
                numOfExtraLargeShips--;
                Log.d("TESTING-IA", "Nave ExtrLarge piazzata, ne restano da piazzare " + numOfExtraLargeShips + "\n");
            }
        }

        //piazza tutte le navi Large
        while (numOfLargeShips > 0) {
            //scegli delle coordinate randomiche dove cominciare a piazzare la nave
            startPosition[0] = random.nextInt(dimMat);
            startPosition[1] = random.nextInt(dimMat);

            //piazza la nave nel punto scelto, con un Orientation scelta randomicamente
            setShip = myShipMatrix.addShip(startPosition[0], startPosition[1], ShipDimension.LARGE, null);
            if (setShip == null) {
                Log.d("TESTING-IA", "La nave non è piazzabile nella cella scelta\n");
            } else {
                numOfLargeShips--;
                Log.d("TESTING-IA", "Nave Large piazzata, ne restano da piazzare " + numOfLargeShips + "\n");
            }
        }

        //piazza tutte le navi Medium
        while (numOfMediumShips > 0) {
            //scegli delle coordinate randomiche dove cominciare a piazzare la nave
            startPosition[0] = random.nextInt(dimMat);
            startPosition[1] = random.nextInt(dimMat);

            //piazza la nave nel punto scelto, con un Orientation scelta randomicamente
            setShip = myShipMatrix.addShip(startPosition[0], startPosition[1], ShipDimension.MEDIUM, null);
            if (setShip == null) {
                Log.d("TESTING-IA", "La nave non è piazzabile nella cella scelta\n");
            } else {
                numOfMediumShips--;
                Log.d("TESTING-IA", "Nave Medium piazzata, ne restano da piazzare " + numOfMediumShips + "\n");
            }
        }

        //piazza tutte le navi Small
        while (numOfSmallShips > 0) {
            //scegli delle coordinate randomiche dove cominciare a piazzare la nave
            startPosition[0] = random.nextInt(dimMat);
            startPosition[1] = random.nextInt(dimMat);

            //piazza la nave nel punto scelto, con un Orientation scelta randomicamente
            setShip = myShipMatrix.addShip(startPosition[0], startPosition[1], ShipDimension.SMALL, null);
            if (setShip == null) {
                Log.d("TESTING-IA", "La nave non è piazzabile nella cella scelta");
            } else {
                numOfSmallShips--;
                Log.d("TESTING-IA", "Nave Small piazzata, ne restano da piazzare " + numOfSmallShips + "\n");
            }
        }

//        //tutto il resto viene riempito da zone d'acqua
//        for (int i = 0; i < dimMat; i++) {
//            for (int j = 0; j < dimMat; j++) {
//                MatrixStatus ms = myShipMatrix.getElement(i, j);
//                if (ms.equals(MatrixStatus.NONE)) {
//                    myShipMatrix.setValue(i, j, MatrixStatus.WATER);
//                }
//            }
//        }
    }

    // riempe le caselle libere di acqua
    public void fillWater() {
        for (int i = 0; i < myShipMatrix.getMatDim(); i++) {
            for (int j = 0; j < myShipMatrix.getMatDim(); j++) {
                MatrixStatus ms = myShipMatrix.getElement(i, j);
                if (ms.equals(MatrixStatus.NONE)) {
                    myShipMatrix.setValue(i, j, MatrixStatus.WATER);
                }
            }
        }
    }

    //attua le modifiche sulla tua matrice in base alla mossa subita e comunicale all'avversario
    public String moveReceiver(Integer[] enemyMove) {
        StringBuilder myResponse = new StringBuilder();

        //guarda cosa conteneva la cella PRIMA del colpo subito
        MatrixStatus result = myShipMatrix.getElement(enemyMove[0], enemyMove[1]);

        //CASO 1: nella cella appena colpita c'era una nave - controlla se è affondata oppure no
        if (result.equals(MatrixStatus.SHIP)) {
            //Identifica la nave appena colpita
            Ship hitShip = myShipMatrix.findShipByElement(enemyMove[0], enemyMove[1]);
            hitShip.isHit(enemyMove[0], enemyMove[1]);

            //controlla se è affondata
            if (hitShip.isSank()) {
                int len = hitShip.getLenght();
                Integer[] shipHead = hitShip.getStart();
                ShipOrientation orientation = hitShip.getShipOrientation();
                myResponse.append("sank").append("(").append(shipHead[0]).append(",").append(shipHead[1]).append(")").append("-").append(len).append("-").append(orientation);

                for( Integer[] integers: hitShip.getPosition() ){
                    myShipMatrix.setValue(integers[0], integers[1],MatrixStatus.SANK);
                }


            } else {
                myResponse.append("hit");
                myShipMatrix.setValue(enemyMove[0], enemyMove[1], MatrixStatus.HIT);
            }
        }

        //CASO 2: la cella appena colpita era vuota
        if (result.equals(MatrixStatus.WATER)) {
            myShipMatrix.setValue(enemyMove[0], enemyMove[1], MatrixStatus.MISS);
            myResponse.append("miss");
        }

        Log.d("IA",myResponse.toString());
        myShipMatrix.print();
        return myResponse.toString();
    }

    //sceglie la prossima mossa da fare
    //NB per ottimizzare il codice sia IA lv1 che IA lv2 sono implementate qui
    public Integer[] moveSelector() {
        Integer[] move = new Integer[2];
        if (DFAstate == 0) {
            //seleziona randomicamente una mossa - continua a selezionare finchè non esce una mossa valida
            boolean isValid = false;
            do {
                Random random = new Random();
                Integer dimMat = myRadar.getMatDim();
                move[0] = random.nextInt(dimMat);
                move[1] = random.nextInt(dimMat);

                MatrixStatus statusCella = myRadar.getElement(move[0], move[1]);

                //controlla se è una mossa valida OVVERO se stai per sparare in una zona che non hai mai colpito
                if (statusCella.equals(MatrixStatus.NONE)) {
                    isValid = true;
                } else {
                    Log.d("IA", "la cella (" + move[0].toString() + "," + move[1].toString() + ") NON è un bersaglio valido");
                }

            } while (!isValid);
        }
        if (DFAstate == 1) {
            //sei a conoscenza di una zona contenente una nave non ancora affondata, spara nei dintorni.
            Boolean isValid = false;
            do {
                Random random = new Random();
                //controlla se almeno uno dei 4 punti nelle immediate vicinanze è fattibile ALTRIMENTI spara nei dintorni
                    int myChoice = random.nextInt(8);
                    switch (myChoice) {
                        case 0:
                            move[0] = myLastUsefulMove[0];
                            move[1] = myLastUsefulMove[1] - 1;
                            break;
                        case 1:
                            move[0] = myLastUsefulMove[0];
                            move[1] = myLastUsefulMove[1] + 1;
                            break;
                        case 2:
                            move[0] = myLastUsefulMove[0] - 1;
                            move[1] = myLastUsefulMove[1];
                            break;
                        case 3:
                            move[0] = myLastUsefulMove[0] + 1;
                            move[1] = myLastUsefulMove[1];
                            break;
                        case 4:
                            move[0] = myLastUsefulMove[0];
                            move[1] = myLastUsefulMove[1] - 2;
                            break;
                        case 5:
                            move[0] = myLastUsefulMove[0];
                            move[1] = myLastUsefulMove[1] + 2;
                            break;
                        case 6:
                            move[0] = myLastUsefulMove[0] - 2;
                            move[1] = myLastUsefulMove[1];
                            break;
                        case 7:
                            move[0] = myLastUsefulMove[0] + 2;
                            move[1] = myLastUsefulMove[1];
                            break;
                        default:
                            move[0] = random.nextInt(10);
                            move[1] = random.nextInt(10);
                            Log.d("IA", "Errore nella scelta random - colpo nei dintorni");
                    }
                //controlla se è una mossa valida OVVERO se stai per sparare in una zona che non hai mai colpito
                MatrixStatus statusCell = myRadar.getElement(move[0], move[1]);
                if (statusCell.equals(MatrixStatus.NONE)) {
                    isValid = true;
                } else {
                    Log.d("IA", "la cella (" + move[0] + "," + move[1] + ") NON è un bersaglio valido");
                }
            } while (!isValid);
        }
        Log.d("IA", "IA (lv " + this.QIlevel + ") sceglie di colpire la cella (" + move[0] + "," + move[1] + ")");
        myLastMove[0] = move[0];
        myLastMove[1] = move[1];
        return move;
    }

    //modifica lo stato della DFA in base all'esito della tua ultima mossa & aggiorna il tuo radar di conseguenza
    //NB per ottimizzare il codice sia IA lv1 che IA lv2 sono implementate qui
    public void valueMyLastMoveOutcome(String result) {
        //NB un giocatore NON ha alcun accesso alla matrice dell'avversario.
        //l'unico modo che ha per conoscere l'esito dei suoi attacchi è quello di chiedere all'avversario.

        Log.d("IA", "Ultima mossa fatta: (" + myLastMove[0] + "," + myLastMove[1] + ")\n");

        //nella tua ultima mossa hai mancato
        if (result.equals("miss") && this.QIlevel > 1) {
            //se eri nello stato 0
            if (this.DFAstate == 0) {
                //continua a colpire randomicamente
                this.DFAstate = 0;
            }
            //se eri nello stato 1
            if (this.DFAstate == 1) {
                //hai scelto il "punto intorno" sbagliato
                //NON cambiare l'ultima mossa utile e riprova un altro punto intorno
                this.DFAstate = 1;
            }
            myRadar.setValue(myLastMove[0], myLastMove[1], MatrixStatus.MISS);
        }
        //nella tua ultima mossa hai colpito qualcosa
        if (result.equals("hit") && this.QIlevel > 1) {
            //se eri nello stato 0 OPPURE nello stato 1
            //segnati come "utile" l'ultima mossa fatta
            //al prossimo turno sparerai intorno a quel punto
            myLastUsefulMove[0] = myLastMove[0];
            myLastUsefulMove[1] = myLastMove[1];
            this.DFAstate = 1;

            myRadar.setValue(myLastMove[0], myLastMove[1], MatrixStatus.HIT);
        }
        //nella tua ultima mossa hai affondato qualcosa
        if (result.contains("sank") && this.QIlevel > 1) {
            //se eri nello stato 0 OPPURE nello stato 1
            this.DFAstate = 0;
            int sankShipLen = 0;
            Integer[] startPos = new Integer[2];

            //Ottieni la prima cella della nave
            //N.B: siamo nel formato (X,Y) dove X e Y sono numeri compresi tra 0 e 9 - dato che la matrice è 10x10
            //TODO per eventuali altri formati si crea un if differente (per ora non è strettamente richiesto dalle specifiche)
            if (result.charAt(4) == '(' && result.charAt(6) == ',' && result.charAt(8) == ')') {
                startPos[0] = Integer.valueOf(result.substring(5, 6));
                startPos[1] = Integer.valueOf(result.substring(7, 8));

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
            //Marca le celle del radar come "sank" in base all'Orientation della nave

                //
                if (result.contains("VERTICAL_TOP")) {

                    for(int i=0 ; i<sankShipLen ; i++){
                        myRadar.setValue(startPos[0] - i, startPos[1], MatrixStatus.SANK);
                    }
                }
                if (result.contains("VERTICAL_BOTTOM")) {
                    for(int i=0 ; i<sankShipLen ; i++){
                        myRadar.setValue(startPos[0] + i, startPos[1], MatrixStatus.SANK);
                    }
                }
                if (result.contains("HORIZONTAL_LEFT")) {
                    for(int i=0 ; i<sankShipLen ; i++) {
                        myRadar.setValue(startPos[0], startPos[1] - i, MatrixStatus.SANK);
                    }
                }
                if (result.contains("HORIZONTAL_RIGHT")) {
                    for(int i=0 ; i<sankShipLen ; i++){
                        myRadar.setValue(startPos[0], startPos[1] + i, MatrixStatus.SANK);
                    }
                }
        }
    }
}