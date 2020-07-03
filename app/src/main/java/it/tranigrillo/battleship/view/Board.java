package it.tranigrillo.battleship.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Matrix;
import it.tranigrillo.battleship.model.Ship;
import it.tranigrillo.battleship.model.ShipDimension;
import it.tranigrillo.battleship.model.ShipOrientation;

public class Board extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {
    List<Row> rows = new ArrayList<>();
    Matrix matrix;
    HashMap<SquareCardView, Integer[]> boxToIndex = new HashMap<>();
    boolean deploy = false;

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
        this.setOrientation(VERTICAL);
        for (int i = 0; i<11; i++){
            Row row = new Row(context);
            if (i == 0) row.setFirstRow();
            else row.setMiddleRow(i);
            for (SquareCardView box : row.getScvList()){
                boxToIndex.put(box, new Integer[]{i, row.getScvList().indexOf(box)});
                if( !box.getStatus().equals(BoxStatus.BORDER)) {
                    box.setOnClickListener(this);
                    box.setOnLongClickListener(this);
                }
            }
            rows.add(row);
            this.addView(row);
        }
    }

    private void setHit(SquareCardView cardView) {
        rows.get(Objects.requireNonNull(boxToIndex.get(cardView))[0]).setHit(Objects.requireNonNull(boxToIndex.get(cardView))[1]);
    }

    private void setMiss(SquareCardView cardView) {
        rows.get(Objects.requireNonNull(boxToIndex.get(cardView))[0]).setMiss(Objects.requireNonNull(boxToIndex.get(cardView))[1]);
    }

    public boolean setDeploy(Boolean bool) {
        return deploy = bool;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public SquareCardView getBox(int posX, int posY) {
        return rows.get(posX).getScvList().get(posY);
    }

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
            popupMenu.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Ship ship = null;
            switch (item.getItemId()){
                case R.id.shortShip: ship = matrix.addShip(posX, posY, ShipDimension.SMALL, null);
                    break;
                case R.id.mediumTop: ship = matrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.VERTICAL_TOP);
                    break;
                case R.id.mediumLeft: ship = matrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.HORIZONTAL_LEFT);
                    break;
                case R.id.mediumBottom: ship = matrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.VERTICAL_BOTTOM);
                    break;
                case R.id.mediumRight: ship = matrix.addShip(posX, posY, ShipDimension.MEDIUM, ShipOrientation.HORIZONTAL_RIGHT);
                    break;
                case R.id.mediumAuto: ship = matrix.addShip(posX, posY, ShipDimension.MEDIUM, null);
                    break;

                case R.id.largeTop: ship = matrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.VERTICAL_TOP);
                    break;
                case R.id.largeLeft: ship = matrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.HORIZONTAL_LEFT);
                    break;
                case R.id.largeBottom: ship = matrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.VERTICAL_BOTTOM);
                    break;
                case R.id.largeRight: ship = matrix.addShip(posX, posY, ShipDimension.LARGE, ShipOrientation.HORIZONTAL_RIGHT);
                    break;
                case R.id.largeAuto: ship = matrix.addShip(posX, posY, ShipDimension.LARGE, null);
                    break;

                case R.id.extraLargeTop: ship = matrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.VERTICAL_TOP);
                    break;
                case R.id.extraLargeLeft: ship = matrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.HORIZONTAL_LEFT);
                    break;
                case R.id.extraLargeBottom: ship = matrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.VERTICAL_BOTTOM);
                    break;
                case R.id.extraLargeRight: ship = matrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, ShipOrientation.HORIZONTAL_RIGHT);
                    break;
                case R.id.extraLargeAuto: ship = matrix.addShip(posX, posY, ShipDimension.EXTRA_LARGE, null);
                    break;
                default:
            }
            showShip(ship);
            return false;
        }
    }

    public void showShip(Ship ship) {
        if (ship!= null) {
            BoxOrientation[] orientation;
            switch (ship.getShipOrientation()) {
                case VERTICAL_TOP: orientation = new BoxOrientation[]{BoxOrientation.BOTTOM_VERTICAL, BoxOrientation.TOP_VERTICAL, BoxOrientation.MIDDLE_VERTICAL};
                    break;
                case VERTICAL_BOTTOM: orientation = new BoxOrientation[]{BoxOrientation.TOP_VERTICAL, BoxOrientation.BOTTOM_VERTICAL, BoxOrientation.MIDDLE_VERTICAL};
                    break;
                case HORIZONTAL_LEFT: orientation = new BoxOrientation[]{BoxOrientation.LEFT_HORIZONTAL, BoxOrientation.RIGHT_HORIZONTAL, BoxOrientation.MIDDLE_HORIZONTAL};
                    break;
                case HORIZONTAL_RIGHT: orientation = new BoxOrientation[]{BoxOrientation.RIGHT_HORIZONTAL, BoxOrientation.LEFT_HORIZONTAL, BoxOrientation.MIDDLE_HORIZONTAL};
                    break;
                case NONE: getBox(ship.getPosition().get(0)[0]+1,ship.getPosition().get(0)[1]+1).showShip(BoxOrientation.NONE);
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + ship.getShipOrientation());
            }
            getBox(ship.getPosition().get(0)[0]+1,ship.getPosition().get(0)[1]+1).showShip(orientation[0]);
            getBox(ship.getPosition().get(ship.getPosition().size()-1)[0]+1,ship.getPosition().get(ship.getPosition().size()-1)[1]+1).showShip(orientation[1]);
            for (Integer[] integers : ship.getPosition().subList(1,ship.getPosition().size()-1)) {
                getBox(integers[0]+1,integers[1]+1).showShip(orientation[2]);
            }
        }
    }

    public void deleteShip(int posX, int posY) {
        Ship ship = matrix.removeShip(posX, posY);
        if (ship!= null) {
            for (Integer[] integers : ship.getPosition()) {
                getBox(integers[0]+1,integers[1]+1).setStatusNone();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Integer posX = Objects.requireNonNull(boxToIndex.get(v))[0]-1;
        Integer posY = Objects.requireNonNull(boxToIndex.get(v))[1]-1;
        if (deploy) {
            new AddMenu(v, posX, posY).popupMenu.show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Integer posX = Objects.requireNonNull(boxToIndex.get(v))[0]-1;
        Integer posY = Objects.requireNonNull(boxToIndex.get(v))[1]-1;
        if (deploy) {
            deleteShip(posX, posY);
        }
        return false;
    }

}
