package it.tranigrillo.battleship;

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
    Paint border = new Paint();
    Paint center = new Paint();
    BoxStatus status = BoxStatus.NONE;
    BoxOrientation orientation = BoxOrientation.NONE;

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
        setNull();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    void setNull(){
        setBorder(5, Color.LTGRAY, true, true, true, true);
        status = BoxStatus.NONE;
    }

    void showShip(BoxOrientation position) {
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

    void setHit(){
        border.setColor(Color.RED);
        center.setColor(Color.RED);
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/6, center);
                border.setStyle(Paint.Style.STROKE);
                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/3, border);
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
        this.setForeground(drawable);
        status = BoxStatus.HITTED;
    }

    void setMiss(){
        border.setColor(Color.BLUE);
        center.setColor(Color.BLUE);
        Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/6, center);
                border.setStyle(Paint.Style.STROKE);
                canvas.drawCircle((float) getWidth()/2, (float) getHeight()/2, (float) getWidth()/3, border);
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
        this.setForeground(drawable);
        status = BoxStatus.MISSED;
    }

    void setSink(BoxOrientation position){
        showShip(position);
        setHit();
        status = BoxStatus.SHIP_SINK;
    }

    BoxStatus getStatus() {
        return status;
    }

    void setBorder(Integer stroke, Integer color, final boolean top, final boolean bottom, final boolean start, final boolean end) {
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