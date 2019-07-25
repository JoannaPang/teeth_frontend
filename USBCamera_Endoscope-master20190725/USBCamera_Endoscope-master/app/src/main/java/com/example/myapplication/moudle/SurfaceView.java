package com.example.myapplication.moudle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import java.util.ArrayList;
import java.util.List;

public class SurfaceView extends android.view.SurfaceView implements Callback {
    private int LastSecond1;
    private int Second1;
    private String Tag;
    private Thread VideoThread;
    public Bitmap bitmap;
    private int count1;
    private boolean has_new;
    private int height;
    private int index;
    private boolean isStop;
    private List<Bitmap> list;
    private Canvas m_canvas;
    private SurfaceHolder m_holder;
    private Paint m_paint;
    private int offset_x;
    private int offset_y;
    private Rect rect;
    private float scale_multispy;
    private int width;


    public SurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.m_holder = null;
        this.m_canvas = null;
        this.m_paint = null;
        this.bitmap = null;
        this.isStop = false;
        this.VideoThread = null;
        this.list = new ArrayList();
        this.index = 0;
        this.scale_multispy = 1.0f;
        this.offset_x = 0;
        this.offset_y = 0;
        this.has_new = false;
        this.rect = null;
        this.Tag = "SurfaceView";
        this.m_holder = getHolder();
        this.m_holder.addCallback(this);
        new DisplayMetrics();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.width = dm.widthPixels;
        this.height = dm.heightPixels;
        this.m_paint = new Paint();
        this.m_paint.setColor(-16776961);
        this.m_paint.setAntiAlias(true);
        this.m_holder.setFormat(-2);
        this.rect = new Rect(0, 0, getWidth(), getHeight());
    }

    public void SetBitmap(Bitmap bmp) {
        if (bmp != null) {
            this.m_canvas = this.m_holder.lockCanvas(this.rect);
            if (this.m_canvas != null) {
                this.m_paint = new Paint();
                this.m_canvas.scale(this.scale_multispy, this.scale_multispy, (float) this.offset_x, (float) this.offset_y);
                this.m_canvas.drawBitmap(bmp, null, this.rect, this.m_paint);
                if (this.m_holder != null) {
                    this.m_holder.unlockCanvasAndPost(this.m_canvas);
                }
            }
        }
    }

    public void stop() {
        this.isStop = true;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width2, int height2) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.isStop = false;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.isStop = true;
    }

    public void Stop() {
        this.isStop = true;
    }

    public void setRect() {
        this.rect = new Rect(0, 0, getWidth(), getHeight());
    }

    public void setNew(float scale_f, int offset_x2, int offset_y2) {
        this.scale_multispy = scale_f;
        this.offset_x = offset_x2;
        this.offset_y = offset_y2;
    }
}

