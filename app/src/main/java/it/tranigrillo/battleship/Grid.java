package it.tranigrillo.battleship;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


import androidx.gridlayout.widget.GridLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Grid implements View.OnClickListener, View.OnLongClickListener {

    //    RULES
    private int numShortShip = 3;
    private int numMediumShip = 2;
    private int numLargeShip = 3;
    private int numExtraLargeShip = 1;

    //    MODE (in disposition TRUE / in game FALSE)
    private final boolean deploy;

    //
    private final GridLayout gridLayout;
    private GridMatrix matrix;
    private List<Ship> fleet;
    private HashMap<SquareCardView, Integer[]> mapCardToIndex = new HashMap<>();
    private ArrayList<SquareCardView> boxes = new ArrayList<>();

    private class MenuListener implements PopupMenu.OnMenuItemClickListener {

        Integer posX;
        Integer posY;
        SquareCardView cardView;
        Ship ship;

        MenuListener(SquareCardView cardView, Integer[] coord, PopupMenu menu) {
            this.cardView = cardView;
            posX = coord[0];
            posY = coord[1];
            if (numShortShip == 0) menu.getMenu().removeItem(R.id.shortShip);
            if (numMediumShip == 0) menu.getMenu().removeItem(R.id.mediumShip);
            if (numLargeShip == 0) menu.getMenu().removeItem(R.id.largeShip);
            if (numExtraLargeShip == 0) menu.getMenu().removeItem(R.id.extraLargeShip);
        }

        @Override
//        TODO REDUCE COMPLEXITY
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.shortShip:
                    if (numShortShip > 0) ship = matrix.addShip(posX, posY, Fleet.SMALL, null);
                    break;

//                    MEDIUM SIZE
                case R.id.mdeiumAuto:
                    if (numMediumShip > 0) ship = matrix.addShip(posX, posY, Fleet.MEDIUM, null);
                    break;
                case R.id.mdeiumTop:
                    if (numMediumShip > 0) ship = matrix.addShip(posX, posY, Fleet.MEDIUM, Orientation.VERTICAL_TOP);
                    break;
                case R.id.mdeiumRight:
                    if (numMediumShip > 0) ship = matrix.addShip(posX, posY, Fleet.MEDIUM, Orientation.HORIZONTAL_RIGHT);
                    break;
                case R.id.mdeiumBottom:
                    if (numMediumShip > 0) ship = matrix.addShip(posX, posY, Fleet.MEDIUM, Orientation.VERTICAL_BOTTOM);
                    break;
                case R.id.mdeiumLeft:
                    if (numMediumShip > 0) ship = matrix.addShip(posX, posY, Fleet.MEDIUM, Orientation.HORIZONTAL_LEFT);
                    break;

//                    LARGE SIZE
                case R.id.largeAuto:
                    if (numLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.LARGE, null);
                    break;
                case R.id.largeTop:
                    if (numLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.LARGE, Orientation.VERTICAL_TOP);
                    break;
                case R.id.largeRight:
                    if (numLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.LARGE, Orientation.HORIZONTAL_RIGHT);
                    break;
                case R.id.largeBottom:
                    if (numLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.LARGE, Orientation.VERTICAL_BOTTOM);
                    break;
                case R.id.largeLeft:
                    if (numLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.LARGE, Orientation.HORIZONTAL_LEFT);
                    break;

//                    EXTRA LARGE SIZE
                case R.id.extraLargeAuto:
                    if (numExtraLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.EXTRA_LARGE, null);
                    break;
                case R.id.extraLargeTop:
                    if (numExtraLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.EXTRA_LARGE, Orientation.VERTICAL_TOP);
                    break;
                case R.id.extraLargeRight:
                    if (numExtraLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.EXTRA_LARGE, Orientation.HORIZONTAL_RIGHT);
                    break;
                case R.id.extraLargeBottom:
                    if (numExtraLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.EXTRA_LARGE, Orientation.VERTICAL_BOTTOM);
                    break;
                case R.id.extraLargeLeft:
                    if (numExtraLargeShip > 0) ship = matrix.addShip(posX, posY, Fleet.EXTRA_LARGE, Orientation.HORIZONTAL_LEFT);
                    break;
                default: return false;
            }
            if (ship!= null) {
                setOrientation(ship);
                switch (ship.getPosition().size()){
                    case 1: numShortShip--;
                        break;
                    case 2: numMediumShip--;
                        break;
                    case 3: numLargeShip--;
                        break;
                    case 4: numExtraLargeShip--;
                        break;
                    default: return false;
                }
            }
            setVisible();
            return false;
        }
    }


    //    TODO REDUCE COMPLEXITY
    private void setOrientation(Ship ship) {
        for (int i = 0; i < ship.getPosition().size(); i++) {
            switch (ship.getShipOrientation()) {
                case VERTICAL_TOP:
                    if (i == 0)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.BOTTOM_VERTICAL;
                    else if (i == ship.getPosition().size() - 1)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.TOP_VERTICAL;
                    else
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.MIDDLE_VERTICAL;
                    break;
                case VERTICAL_BOTTOM:
                    if (i == 0)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.TOP_VERTICAL;
                    else if (i == ship.getPosition().size() - 1)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.BOTTOM_VERTICAL;
                    else
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.MIDDLE_VERTICAL;
                    break;
                case HORIZONTAL_LEFT:
                    if (i == 0)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.LEFT_HORIZONTAL;
                    else if (i == ship.getPosition().size()-1)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.RIGHT_HORIZONTAL;
                    else
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.MIDDLE_HORIZONTAL;
                    break;
                case HORIZONTAL_RIGHT:
                    if (i == 0)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.RIGHT_HORIZONTAL;
                    else if (i == ship.getPosition().size()-1)
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.LEFT_HORIZONTAL;
                    else
                        boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.MIDDLE_HORIZONTAL;
                    break;
                case NONE:
                    boxes.get(ship.getPosition().get(i)[0] * 10 + ship.getPosition().get(i)[1]).orientation = BoxOrientation.NONE;
            }
        }
    }

    private void setVisible() {
        for (SquareCardView box : boxes) {
            try {
                setOrientation(matrix.findShipByElement(Objects.requireNonNull(mapCardToIndex.get(box))[0], Objects.requireNonNull(mapCardToIndex.get(box))[1]));
                if (box.status != BoxStatus.SHIP_SINK) box.status = BoxStatus.SHIP_VISIBLE;
                box.setClickable(false);
            }
            catch (NullPointerException ignored) {
//                if null it do not care
            }
        }
        notifyDataSetChanged();
    }

    Grid(GridLayout grid, int numCol, GridMatrix matrix, boolean shipDeploy) {
        this.gridLayout = grid;
        this.matrix = matrix;
        this.deploy = shipDeploy;
        this.fleet = matrix.getFleet();

//        SETTING VIEW
        grid.setColumnCount(numCol);
        grid.setRowCount(numCol);

        for (int i = 0; i < numCol; i++) {
            for (int j = 0; j < numCol; j++) {
                SquareCardView box = new SquareCardView(grid.getContext());
                mapCardToIndex.put(box, new Integer[]{i, j});
                boxes.add(box);
                box.setOnClickListener(this);
                box.setOnLongClickListener(this);
                box.status = BoxStatus.NONE;
                box.orientation = BoxOrientation.NONE;

//                SETTING LAYOUT PARAMS (XML PARAMS) PROGRAMMATICALLY
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.columnSpec = GridLayout.spec(j, 1,1);
                grid.addView(box, params);
            }
        }
        if (!deploy) {
            setVisible();
        }
    }

    private void notifyDataSetChanged() {
        for ( SquareCardView box : boxes) {
            if (box.status.equals(BoxStatus.SHIP_VISIBLE)) box.showShip(box.orientation);
            if (deploy && matrix.getElement(Objects.requireNonNull(mapCardToIndex.get(box))[0], Objects.requireNonNull(mapCardToIndex.get(box))[1]) == 0) {
                box.setNull();
            }
            if (box.status.equals(BoxStatus.SHIP_SINK)) box.setSink(box.orientation);
            if (box.status.equals(BoxStatus.MISSED)) box.setMiss();
            if (box.status.equals(BoxStatus.HITTED)) box.setHit();
        }
    }

    @Override
    public void onClick(View view) {
        SquareCardView box = (SquareCardView) view;
        if (deploy && box.getStatus().equals(BoxStatus.NONE)) {
            PopupMenu menu = new PopupMenu(box.getContext(), box);
            menu.inflate(R.menu.ship_menu);
            menu.setOnMenuItemClickListener(new MenuListener(box, Objects.requireNonNull(mapCardToIndex.get(box)), menu));
            menu.show();
        }

        if (box.getStatus().equals(BoxStatus.NONE)) {
            //TODO AZIONI PER STATO MANCATO/COLPITO
            notifyDataSetChanged();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (deploy) {
            SquareCardView box = (SquareCardView) v;
            Ship ship = matrix.removeShip(Objects.requireNonNull(mapCardToIndex.get(box))[0], Objects.requireNonNull(mapCardToIndex.get(box))[1]);
            if (ship != null) {
                switch (ship.getPosition().size()) {
                    case 1:
                        numShortShip++;
                        break;
                    case 2:
                        numMediumShip++;
                        break;
                    case 3:
                        numLargeShip++;
                        break;
                    case 4:
                        numExtraLargeShip++;
                        break;
                    default: return false;
                }
                matrix.print();
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

}
