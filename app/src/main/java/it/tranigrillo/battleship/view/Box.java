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

public class Box extends CardView {
    private BoxStatus status = BoxStatus.NONE;
    private BoxOrientation orientation = BoxOrientation.NONE;

//    -----------------------------
//    COSTRUTTORI
//    -----------------------------

    public Box(Context context) {
        super(context);
        init(null, 0);
    }

    public Box(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Box(Context context, AttributeSet attrs, int defStyle) {
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

    //  Disegna il bordo della casella
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

//    -----------------------------
//    ORIENTATION
//    -----------------------------

    // Getter dell'orientamento della Box
    public BoxOrientation getOrientation() {
        return orientation;
    }

    // Setter dell'orientamento della Box
    public void setOrientation(BoxOrientation orientation) {
        this.orientation = orientation;
    }

//    -----------------------------
//    STATUS
//    -----------------------------

    // Getter dello stato della Box
    public BoxStatus getStatus() {
        return status;
    }

    // Setter dello stato della Box (imposta la casella come bordo)
    public void setStatusBorder() {
        this.status = BoxStatus.BORDER;
        setClickable(false);
    }

    // Setter dello stato della Box (imposta la casella come vuoto)
    public void setStatusNone() {
        setBorder(5, Color.LTGRAY, true, true, true, true);
        status = BoxStatus.NONE;
    }

    // Setter dello stato della Box (imposta la casella come colpito)
    public void setStatusHit() {
        status = BoxStatus.HIT;
        setClickable(false);
    }

    // Setter dello stato della Box (imposta la casella come mancato)
    public void setStatusMissed() {
        status = BoxStatus.MISSED;
        setClickable(false);
    }

    // Setter dello stato della Box (imposta la nave sulla casella come visibile)
    public void setStatusVisible(BoxOrientation position) {
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

    // Setter dello stato della Box (imposta la nave sulla casella come affondata)
    public void setStatusSank(BoxOrientation position){
        setStatusVisible(position);
        status = BoxStatus.SHIP_SANK;
        setClickable(false);
    }
}