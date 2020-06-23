package it.tranigrillo.battleship;

enum Orientation {
    NONE,
    VERTICAL_TOP,
    VERTICAL_BOTTOM,
    HORIZONTAL_LEFT,
    HORIZONTAL_RIGHT;
}

enum Fleet {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE;
}

enum BoxOrientation {
    NONE,
    TOP_VERTICAL,
    RIGHT_HORIZONTAL,
    MIDDLE_VERTICAL,
    MIDDLE_HORIZONTAL,
    BOTTOM_VERTICAL,
    LEFT_HORIZONTAL;
}

enum BoxStatus {
    NONE,
    SHIP_VISIBLE,
    HITTED,
    MISSED,
    SHIP_SINK;
}
