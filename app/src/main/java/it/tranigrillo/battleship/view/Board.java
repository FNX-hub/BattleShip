package it.tranigrillo.battleship.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Game;
import it.tranigrillo.battleship.model.IAmanager;
import it.tranigrillo.battleship.model.MatrixStatus;
import it.tranigrillo.battleship.model.Ship;
import it.tranigrillo.battleship.model.ShipDimension;
import it.tranigrillo.battleship.model.ShipMatrix;
import it.tranigrillo.battleship.model.ShipOrientation;

public class Board extends CardView implements View.OnClickListener, View.OnLongClickListener {
    ArrayList<Row> rows = new ArrayList<>();
    HashMap<Box, Integer[]> boxToIndex = new HashMap<>();
    ShipMatrix myShipMatrix;
    Row rCol;
    Row r1; Row r2; Row r3; Row r4; Row r5;
    Row r6; Row r7; Row r8; Row r9; Row r10;
    Game game;
    Integer smallShipCounter;
    Integer mediumShipCounter;
    Integer largeShipCounter;
    Integer extraLargeShipCounter;
    IAmanager iAmanager;
    TextViewObserver textViewObserver;
    boolean deploy = false;

//    ---------------------------------
//    COSTRUTTORI
//    ---------------------------------

    public Board(Context context) {
        super(context);
        init(context);
    }

    public Board(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Board(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.board_layout, this, true);
        this.setRadius(0);
        this.setElevation(0);
        this.setMaxCardElevation(0);
        setBorder(1, Color.LTGRAY, true, true, true, true);
        rCol = findViewById(R.id.col);
        rows.add(rCol);
        r1 = findViewById(R.id.row1);
        rows.add(r1);
        r2 = findViewById(R.id.row2);
        rows.add(r2);
        r3 = findViewById(R.id.row3);
        rows.add(r3);
        r4 = findViewById(R.id.row4);
        rows.add(r4);
        r5 = findViewById(R.id.row5);
        rows.add(r5);
        r6 = findViewById(R.id.row6);
        rows.add(r6);
        r7 = findViewById(R.id.row7);
        rows.add(r7);
        r8 = findViewById(R.id.row8);
        rows.add(r8);
        r9 = findViewById(R.id.row9);
        rows.add(r9);
        r10 = findViewById(R.id.row10);
        rows.add(r10);

        for (int i = 0; i< 11; i++) {
            Row row = rows.get(i);
            if (i == 0) {
                row.setFirstRow();
            }
            else {
                row.setMiddleRow(i);
                row.setClickable(true);
                row.setClickListener(this, this);
            }
            for (Box box : row.getScvList()){
                boxToIndex.put(box, new Integer[]{i, row.getScvList().indexOf(box)});
            }
        }
    }


    // Setter per il Game
    public void setGame(Game game, boolean deploy, boolean radar) {
        this.game = game;
        this.deploy = deploy;
        if(radar) {
            setRadar();
        }
        setRule(game.getSmallShip(), game.getMediumShip(), game.getLargeShip(), game.getExtraLargeShip());
        iAmanager = new IAmanager("deployer", 1, 10);
        this.myShipMatrix = iAmanager.getMyShipMatrix();
    }

    //  Setter per i contatori delle Ship (quante e quali sono disponibili)
    private void setRule(Integer s, Integer m, Integer l, Integer xl){
        smallShipCounter = s;
        mediumShipCounter = m;
        largeShipCounter = l;
        extraLargeShipCounter = xl;
    }

    public void confirmDisposition() {
        iAmanager.fillWater();
    }

    //  Setter per shipMatrix
    public void setMyShipMatrix(ShipMatrix myShipMatrix) {
        this.myShipMatrix = myShipMatrix;
    }

    //  Getter per shipMatrix
    public ShipMatrix getMyShipMatrix() {
        return myShipMatrix;
    }

    //  Imposta se la Board è in modalità radar (solo visione della flotta della matrice passata)
    private void setRadar() {
        deploy = false;
        for (Row row : rows) {
            row.setFontSize(7);
            row.setClickListener(null, null);
            row.setClickable(false);
        }
        for (Ship ship : myShipMatrix.getFleet()) {
            showShip(ship);
        }
    }

    //  Restituisce la Box di riferimento alle coordiante della matrice
    public Box getBox(int posX, int posY) {
        return rows.get(posX).getScvList().get(posY);
    }

    public void setTextViewObserver(TextViewObserver observer) {
        textViewObserver = observer;
        updateViews();
    }

    private void updateViews() {
        textViewObserver.updateText(0, getResources().getString(R.string.small), smallShipCounter);
        textViewObserver.updateText(1, getResources().getString(R.string.medium), mediumShipCounter);
        textViewObserver.updateText(2, getResources().getString(R.string.large), largeShipCounter);
        textViewObserver.updateText(3, getResources().getString(R.string.extra_large), extraLargeShipCounter);
    }

    // classe che gestisce il deploy delle navi (orientamento e quante possono essere ancora piazzate)
    // tramite un PopUp menu
    private class AddMenu implements PopupMenu.OnMenuItemClickListener {
        PopupMenu popupMenu;
        int posX;
        int posY;

        AddMenu(View v, int posX, int posY){
            this.posX = posX;
            this.posY = posY;
            popupMenu = new PopupMenu(getContext(), v);
            Menu menu = popupMenu.getMenu();
            MenuInflater inflater = new MenuInflater(getContext());
            inflater.inflate(R.menu.ship_menu, menu);
            if(smallShipCounter<=0){
                menu.removeItem(R.id.shortShip);
            }
            if(mediumShipCounter<=0){
                menu.removeItem(R.id.mediumShip);
            }
            if(largeShipCounter<=0){
                menu.removeItem(R.id.largeShip);
            }
            if(extraLargeShipCounter<=0){
                menu.removeItem(R.id.extraLargeShip);
            }
            popupMenu.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Ship ship = null;
            switch (item.getItemId()){
                case R.id.shortShip:
                    if(smallShipCounter >0) {
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.SMALL, null);
                        if(ship != null){
                            smallShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.mediumTop:
                    if(mediumShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.VERTICAL_TOP);
                        if(ship != null){
                            mediumShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.mediumLeft:
                    if(largeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.HORIZONTAL_LEFT);
                        if(ship != null){
                            mediumShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.mediumBottom:
                    if(mediumShipCounter>0) {
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.VERTICAL_BOTTOM);
                        if(ship != null){
                            mediumShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.mediumRight: if(mediumShipCounter>0) {
                    ship = myShipMatrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.HORIZONTAL_RIGHT);
                    if(ship != null){
                        mediumShipCounter--;
                    }
                    else {
                        Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                    }
                }
                    break;
                case R.id.mediumAuto:
                    if(mediumShipCounter>0) {
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.MEDIUM, null);
                        if(ship != null){
                            mediumShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.largeTop:
                    if(largeShipCounter>0) {
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.VERTICAL_TOP);
                        if(ship != null){
                            largeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.largeLeft:
                    if(largeShipCounter>0) {
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.HORIZONTAL_LEFT);
                        if(ship != null){
                            largeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.largeBottom:
                    if(largeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.VERTICAL_BOTTOM);
                        if(ship != null){
                            largeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.largeRight:
                    if(largeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.HORIZONTAL_RIGHT);
                        if(ship != null){
                            largeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.largeAuto:
                    if(largeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.LARGE, null);
                        if(ship != null){
                            largeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.extraLargeTop:
                    if(extraLargeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.VERTICAL_TOP);
                        if(ship != null){
                            extraLargeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.extraLargeLeft:
                    if(extraLargeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.HORIZONTAL_LEFT);
                        if(ship != null){
                            extraLargeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.extraLargeBottom:
                    if(extraLargeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.VERTICAL_BOTTOM);
                        if(ship != null){
                            extraLargeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.extraLargeRight:
                    if(extraLargeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.HORIZONTAL_RIGHT);
                        if(ship != null){
                            extraLargeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.extraLargeAuto:
                    if(extraLargeShipCounter>0){
                        ship = myShipMatrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, null);
                        if(ship != null){
                            extraLargeShipCounter--;
                        }
                        else {
                            Toast.makeText(getContext(), R.string.invalid_position, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
            if (ship != null) {
                showShip(ship);
            }
            updateViews();
            return false;
        }
    }

    public void autoDisposition() {
        iAmanager.fleetSetter(smallShipCounter, mediumShipCounter, largeShipCounter, extraLargeShipCounter);
        smallShipCounter = mediumShipCounter = largeShipCounter = extraLargeShipCounter = 0;
        for(Ship ship : myShipMatrix.getFleet()) {
            showShip(ship);
        }
        updateViews();
    }

    //  Mostra la Box come Mancato
    public void setMiss(Box box) {
        rows.get(Objects.requireNonNull(boxToIndex.get(box))[0]).setMiss(Objects.requireNonNull(boxToIndex.get(box))[1]);
    }

    //  Mostra la Box come Colpito
    public void setHit(Box box) {
        rows.get(Objects.requireNonNull(boxToIndex.get(box))[0]).setHit(Objects.requireNonNull(boxToIndex.get(box))[1]);
    }

    // Data una nave la mostra sulla Board
    public void showShip(Ship ship) {
        if (ship!= null) {
            BoxOrientation[] orientation;
            switch (ship.getShipOrientation()) {
                case VERTICAL_TOP:
                    orientation = new BoxOrientation[]{BoxOrientation.BOTTOM_VERTICAL, BoxOrientation.TOP_VERTICAL, BoxOrientation.MIDDLE_VERTICAL};
                    break;
                case VERTICAL_BOTTOM:
                    orientation = new BoxOrientation[]{BoxOrientation.TOP_VERTICAL, BoxOrientation.BOTTOM_VERTICAL, BoxOrientation.MIDDLE_VERTICAL};
                    break;
                case HORIZONTAL_LEFT:
                    orientation = new BoxOrientation[]{BoxOrientation.LEFT_HORIZONTAL, BoxOrientation.RIGHT_HORIZONTAL, BoxOrientation.MIDDLE_HORIZONTAL};
                    break;
                case HORIZONTAL_RIGHT:
                    orientation = new BoxOrientation[]{BoxOrientation.RIGHT_HORIZONTAL, BoxOrientation.LEFT_HORIZONTAL, BoxOrientation.MIDDLE_HORIZONTAL};
                    break;
                case NONE:
                    rows.get(ship.getPosition().get(0)[0]+1).setVisibile(ship.getPosition().get(0)[1]+1, BoxOrientation.NONE);
                    return;
                default:
                    return;
            }
            Integer[] coord = ship.getPosition().get(0);
            rows.get(coord[0]+1).setVisibile(coord[1]+1, orientation[0]);
            coord = ship.getPosition().get(ship.getLenght()-1);
            rows.get(coord[0]+1).setVisibile(coord[1]+1, orientation[1]);
            for (int i = 1; i < ship.getLenght()-1; i++) {
                coord = ship.getPosition().get(i);
                rows.get(coord[0]+1).setVisibile(coord[1]+1, orientation[2]);
            }
        }
    }

    public void setSank(Ship ship) {
        if (ship!= null) {
            BoxOrientation[] orientation;
            switch (ship.getShipOrientation()) {
                case VERTICAL_TOP:
                    orientation = new BoxOrientation[]{BoxOrientation.BOTTOM_VERTICAL, BoxOrientation.TOP_VERTICAL, BoxOrientation.MIDDLE_VERTICAL};
                    break;
                case VERTICAL_BOTTOM:
                    orientation = new BoxOrientation[]{BoxOrientation.TOP_VERTICAL, BoxOrientation.BOTTOM_VERTICAL, BoxOrientation.MIDDLE_VERTICAL};
                    break;
                case HORIZONTAL_LEFT:
                    orientation = new BoxOrientation[]{BoxOrientation.LEFT_HORIZONTAL, BoxOrientation.RIGHT_HORIZONTAL, BoxOrientation.MIDDLE_HORIZONTAL};
                    break;
                case HORIZONTAL_RIGHT:
                    orientation = new BoxOrientation[]{BoxOrientation.RIGHT_HORIZONTAL, BoxOrientation.LEFT_HORIZONTAL, BoxOrientation.MIDDLE_HORIZONTAL};
                    break;
                case NONE:
                    rows.get(ship.getPosition().get(0)[0]+1).setVisibile(ship.getPosition().get(0)[1]+1, BoxOrientation.NONE);
                    return;
                default:
                    return;
            }
            Integer[] coord = ship.getPosition().get(0);
            rows.get(coord[0]+1).setSank(coord[1]+1, orientation[0]);
            coord = ship.getPosition().get(ship.getLenght()-1);
            rows.get(coord[0]+1).setSank(coord[1]+1, orientation[1]);
            for (int i = 1; i < ship.getLenght()-1; i++) {
                coord = ship.getPosition().get(i);
                rows.get(coord[0]+1).setSank(coord[1]+1, orientation[2]);
            }
        }
    }

    // Elimina una nave, se presente alle coorinate date
    public void deleteShip(int posX, int posY) {
        Ship ship = myShipMatrix.removeShip(posX, posY);
        if (ship != null) {
            for (Integer[] integers : ship.getPosition()) {
                getBox(integers[0]+1,integers[1]+1).setStatusNone();
            }
            switch(ship.getPosition().size()){
                case 1:
                    smallShipCounter++;
                    break;
                case 2:
                    mediumShipCounter++;
                    break;
                case 3:
                    largeShipCounter++;
                    break;
                case 4:
                    extraLargeShipCounter++;
                    break;
                default:
                    break;
            }
        }
        updateViews();
    }

    //  Disegna il bordo della Board
    public void setBorder(Integer stroke, Integer color, final boolean top, final boolean bottom, final boolean start, final boolean end) {
        final Paint grid = new Paint();
        grid.setStrokeWidth(stroke);
        grid.setColor(color);
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                if (top) {
                    canvas.drawLine(0, 0, getWidth(), 0, grid);
                }
                if (bottom) {
                    canvas.drawLine(0, getHeight(), getWidth(), getHeight(), grid);
                }
                if (start) {
                    canvas.drawLine(0, 0, 0, getHeight(), grid);
                }
                if (end) {
                    canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), grid);
                }
            }

            @Override
            public void setAlpha(int alpha) {
//                NON NECESSARIO
            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {
//                NON NECESSARIO
            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        this.setBackground(drawable);
    }


//    private void checkBox(int posX, int posY) {
//        Ship ship = enemyShipMatrix.findShipByElement(posX, posY);
//        Box box = getBox(posX+1, posY+1);
//        switch (enemyShipMatrix.getElement(posX, posY)) {
//            case WATER:
//                setMiss(box);
//                break;
//            case SHIP:
//                setHit(box);
//                ship.isHit(posX, posY);
//                if (ship.isSink()) {
//                    setSank(ship);
//                }
//                break;
//        }
//    }



    // Gestisce il click delle Box della board
    @Override
    public void onClick(View v) {
        Box box = (Box)v;
        int posX = Objects.requireNonNull(boxToIndex.get(box))[0]-1;
        int posY = Objects.requireNonNull(boxToIndex.get(box))[1]-1;
        if (deploy && !box.getStatus().equals(BoxStatus.SHIP_VISIBLE)) {
            new AddMenu(v, posX, posY).popupMenu.show();
        }
        if (!deploy) {
//            checkBox(posX, posY);
//            if (enemyShipMatrix.getElement(posX, posY).equals(MatrixStatus.WATER)) {
//                setMiss(box);
//            }
//            else {
//                setHit(box);
//            }
        }
    }

    // Gestisce il long click delle Box della board
    @Override
    public boolean onLongClick(View v) {
        Box box = (Box)v;
        int posX = Objects.requireNonNull(boxToIndex.get(box))[0]-1;
        int posY = Objects.requireNonNull(boxToIndex.get(box))[1]-1;
        if (deploy) {
            deleteShip(posX, posY);
            Log.d("TAG", String.valueOf(myShipMatrix.getFleet().size()));
        }
        return false;
    }


}