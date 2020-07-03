package it.tranigrillo.battleship.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class SquareCardView extends CardView {
//    private Paint border = new Paint();
//    private Paint center = new Paint();
    private BoxStatus status = BoxStatus.NONE;
    private BoxOrientation orientation = BoxOrientation.NONE;

//    -----------------------------
//    CONSTRUCTOR
//    -----------------------------

    public SquareCardView(Context context) {
        super(context);
        init(null, 0);
    }

    public SquareCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SquareCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int i) {
        this.setRadius(0);
        this.setElevation(0);
        this.setMaxCardElevation(0);
        setStatusNone();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

//    -----------------------------
//    ORIENTATION
//    -----------------------------

    public BoxOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(BoxOrientation orientation) {
        this.orientation = orientation;
    }

//    -----------------------------
//    STATUS
//    -----------------------------

    public BoxStatus getStatus() {
        return status;
    }

    public void setStatusBorder() {
        this.status = BoxStatus.BORDER;
    }

    public void setStatusNone() {
        setBorder(5, Color.LTGRAY, true, true, true, true);
        status = BoxStatus.NONE;
    }

    public void setStatusHit() {
//        border.setColor(Color.RED);
//        center.setColor(Color.RED);
//        Drawable drawable = new Drawable() {
//            @Override
//            public void draw(@NonNull Canvas canvas) {
//                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/6, center);
//                border.setStyle(Paint.Style.STROKE);
//                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/3, border);
//            }
//
//            @Override
//            public void setAlpha(int alpha) {
////                not necessary
//            }
//
//            @Override
//            public void setColorFilter(@Nullable ColorFilter colorFilter) {
////                not necessary
//            }
//
//            @Override
//            public int getOpacity() {
//                return PixelFormat.UNKNOWN;
//            }
//        };
//        this.setForeground(drawable);
        status = BoxStatus.HIT;
    }

    public void setStatusMissed() {
//        border.setColor(Color.BLUE);
//        center.setColor(Color.BLUE);
//        Drawable drawable = new Drawable() {
//            @Override
//            public void draw(@NonNull Canvas canvas) {
//                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/6, center);
//                border.setStyle(Paint.Style.STROKE);
//                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/3, border);
//            }
//
//            @Override
//            public void setAlpha(int alpha) {
////                not necessary
//            }
//
//            @Override
//            public void setColorFilter(@Nullable ColorFilter colorFilter) {
////                not necessary
//            }
//
//            @Override
//            public int getOpacity() {
//                return PixelFormat.UNKNOWN;
//            }
//        };
//        this.setForeground(drawable);
        status = BoxStatus.MISSED;
    }

    public void setStatusSank(BoxOrientation position){
        showShip(position);
        setStatusHit();
        status = BoxStatus.SHIP_SANK;
    }

    public void showShip(BoxOrientation position) {
        int color = Color.BLUE;
        switch (position) {
            case TOP_VERTICAL:
                setBorder(10, color, true, false, true, true);
                break;
            case RIGHT_HORIZONTAL:
                setBorder(10, color, true, true, true, false);
                break;
            case BOTTOM_VERTICAL:
                setBorder(10, color, false, true, true, true);
                break;
            case LEFT_HORIZONTAL:
                setBorder(10, color, true, true, false, true);
                break;
            case MIDDLE_VERTICAL:
                setBorder(10, color, false, false, true, true);
                break;
            case MIDDLE_HORIZONTAL:
                setBorder(10, color, true, true, false, false);
                break;
            default:
                setBorder(10, color, true, true, true, true);
        }
        status = BoxStatus.SHIP_VISIBLE;
    }

//    DRAW BORDER
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
//                not necessary
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
//                not necessary
        }

        @Override
        public int getOpacity() {
            return PixelFormat.UNKNOWN;
        }
    };
        this.setBackground(drawable);
    }
}