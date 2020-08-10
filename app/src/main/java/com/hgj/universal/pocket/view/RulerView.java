package com.hgj.universal.pocket.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;

/**
 * RulerView继承自SurfaceView，避免阻塞主线程
 */
public class RulerView extends SurfaceView implements Callback {
    public float UNIT_MM;               //毫米之间的间距
    public float RULE_HEIGHT;           //尺子的最长刻度线高度
    public float RULE_SCALE;            //刻度线的高度比例
    public int SCREEN_W;                //屏幕宽度
    public int SCREEN_H;                //屏幕高度
    public float FONT_SIZE;             //尺子刻度文字大小
    public float PADDING;               //左边距
    public float RADIUS_BIG;            //大环半径
    public float RADIUS_MEDIUM;         //中环半径
    public float RADIUS_SMALL;          //小环半径
    public float CYCLE_WIDTH;           //环的宽度
    public float DISPLAY_SIZE_BIG;      //大号字体
    public float DISPLAY_SIZE_SMALL;    //小号字体

    private SurfaceHolder holder;
    boolean unlockLineCanvas = false;
    float lineX;                        //
    float lineOffset;
    float startX;
    float lastX;
    int kedu;                           //测量的刻度
    Paint paint;
    Paint linePaint;
    Paint fontPaint;

    public RulerView(Context context) {
        super(context);
        init(context);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    //SurfaceView创建时触发，一般在这里调用画图线程
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread() {
            public void run() {
                draw();
            };
        }.start();
    }
    //SurfaceView的大小发生改变时触发
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }
    //SurfaceView销毁时触发，一般在这里将画图的线程停止、释放
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public int getKedu() {
        return kedu;
    }

    public void setKedu(int kedu) {
        this.kedu = kedu;
        draw();
    }

    public float getLineX() {
        return lineX;
    }

    public void setLineX(float lineX) {
        this.lineX = lineX;
        draw();
    }

    private void onTouchBegain(float x, float y) {
        lineOffset = Math.abs(x - lineX);
        if (lineOffset <= PADDING * 2) {
            startX = x;
            unlockLineCanvas = true;
        }
    }

    private void onTouchMove(float x, float y) {
        if (unlockLineCanvas) {
            lineX += x - startX;
            if (lineX < PADDING) {
                lineX = PADDING;
            } else if (lineX > lastX) {
                lineX = lastX;
            }
            kedu = Math.round((lineX - PADDING) / UNIT_MM);
            startX = x;
            draw();
        }
    }

    private void onTouchDone(float x, float y) {
        unlockLineCanvas = false;
        startX = -1;
        draw();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                onTouchDone(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_DOWN:
                onTouchBegain(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event.getX(), event.getY());
                break;
        }
        return true;
    }

    private void init(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        //设置大环、中环、小环的半径分别为46dp、40dp、20dp
        RADIUS_BIG = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46,
                dm);
        RADIUS_MEDIUM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                40, dm);
        RADIUS_SMALL = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                20, dm);
        //设置所有环的宽度为4dp
        CYCLE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                dm);
        //设置显示的文字大小分别为40dp、20dp
        DISPLAY_SIZE_BIG = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 40, dm);
        DISPLAY_SIZE_SMALL = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, dm);
        //设置刻度线间距为1毫米
        UNIT_MM = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1, dm);
        //设置尺子的最长刻度线高度为30dp
        RULE_HEIGHT = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                30, dm);
        //设置尺子刻度文字的大小为20dp
        FONT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                dm);
        //设置尺子的padding为10dp
        PADDING = FONT_SIZE / 2;
        SCREEN_W = dm.widthPixels;
        SCREEN_H = dm.heightPixels;
        holder = getHolder();
        holder.addCallback(this);
        //尺子刻度线的画笔
        paint = new Paint();
        paint.setColor(0xff666666);
        //参考线的画笔
        linePaint = new Paint();
        linePaint.setColor(0xff666666);
        linePaint.setStrokeWidth(4);
        //尺子刻度文字的画笔
        fontPaint = new Paint();
        fontPaint.setTextSize(FONT_SIZE);
        fontPaint.setAntiAlias(true);
        fontPaint.setColor(0xff666666);
        lineX = PADDING;
        kedu = 0;
    }

    /**
     * 画尺子测量的结果和尺子测量参考线
     * @param canvas
     */
    private void drawDisplay(Canvas canvas) {
        //测量的厘米值和毫米值
        String cm = String.valueOf(kedu / 10);
        String mm = String.valueOf(kedu % 10);
        Paint displayPaint1 = new Paint();
        displayPaint1.setAntiAlias(true);
        displayPaint1.setColor(0xffffffff);
        displayPaint1.setTextSize(DISPLAY_SIZE_BIG);
        float cmWidth = displayPaint1.measureText(cm);
        Rect bounds1 = new Rect();
        displayPaint1.getTextBounds(cm, 0, cm.length(), bounds1);
        Paint displayPaint2 = new Paint();
        displayPaint2.setAntiAlias(true);
        displayPaint2.setColor(0xffffffff);
        displayPaint2.setTextSize(DISPLAY_SIZE_SMALL);
        float mmWidth = displayPaint2.measureText(mm);
        Rect bounds2 = new Rect();
        displayPaint2.getTextBounds(mm, 0, mm.length(), bounds2);
        canvas.drawLine(lineX, 0, lineX, SCREEN_H, linePaint);
//         canvas.drawText(
//         String.valueOf(kedu / 10) + "." + String.valueOf(kedu % 10)
//         + "cm", PADDING, SCREEN_H - PADDING, fontPaint);
        //画白色的圆
        Paint cyclePaint = new Paint();
        cyclePaint.setColor(0xffffffff);
        cyclePaint.setAntiAlias(true);
        cyclePaint.setDither(true);
        cyclePaint.setStyle(Paint.Style.FILL);
        //画真正的圆
        Paint strokPaint = new Paint();
        strokPaint.setAntiAlias(true);//抗锯齿
        strokPaint.setDither(true);//防抖
        strokPaint.setStyle(Paint.Style.FILL);
        //画大圆
        strokPaint.setColor(0xff555555);
        //canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_BIG, cyclePaint);
        canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_BIG, strokPaint);
        //画中圆
        strokPaint.setColor(0xff888888);
        //canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_MEDIUM, cyclePaint);
        canvas.drawCircle(SCREEN_W / 2, SCREEN_H / 2, RADIUS_MEDIUM, strokPaint);
        //画小圆
        strokPaint.setColor(0xff555555);
        //canvas.drawCircle(SCREEN_W / 2 + RADIUS_BIG, SCREEN_H / 2, RADIUS_SMALL, cyclePaint);
        canvas.drawCircle(SCREEN_W / 2 + RADIUS_BIG, SCREEN_H / 2, RADIUS_SMALL, strokPaint);
        //画圆圈中的数字
        canvas.drawText(cm, SCREEN_W / 2 - cmWidth / 2, SCREEN_H / 2 + bounds1.height() / 2, displayPaint1);
        canvas.drawText(mm, SCREEN_W / 2 + RADIUS_BIG - mmWidth / 2, SCREEN_H / 2 + bounds2.height() / 2, displayPaint2);
    }

    /**
     * 画尺子刻度线和尺子刻度文字
     */
    private void draw() {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            canvas.drawColor(0xffffffff);
            //left是起始刻度线的位置
            float left = PADDING;
            for (int i = 0; SCREEN_W - PADDING - left > 0; i++) {
                //RULE_SCALE是刻度线的高度占比
                RULE_SCALE = 0.5f;
                if (i % 5 == 0) {
                    if ((i & 0x1) == 0) {
                        RULE_SCALE = 1f;
                        //如果是整厘米，就要在下面标注数字
                        String txt = String.valueOf(i / 10);
                        Rect bounds = new Rect();
                        float txtWidth = fontPaint.measureText(txt);
                        fontPaint.getTextBounds(txt, 0, txt.length(), bounds);
                        canvas.drawText(txt, left - txtWidth / 2, RULE_HEIGHT
                                + FONT_SIZE / 2 + bounds.height(), fontPaint);
                    } else {
                        RULE_SCALE = 0.75f;
                    }
                }
                RectF rect = new RectF();
                rect.left = left - 1;
                rect.top = 0;
                rect.right = left + 1;
                rect.bottom = rect.top + RULE_HEIGHT * RULE_SCALE;
                canvas.drawRect(rect, paint);
                left += UNIT_MM;
            }
            lastX = left - UNIT_MM;
            drawDisplay(canvas);
        } catch (Exception e) {
            // TODO: handle exception
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}