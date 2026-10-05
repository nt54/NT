package com.example.hello;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

public class LiquidGlassView extends View {

    private final Paint glassPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dispersionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float touchX = -999;
    private float touchY = -999;
    private boolean touching = false;
    private float dispersionPhase = 0f;

    public LiquidGlassView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();

        float glassW = w * 0.82f;
        float glassH = h * 0.42f;
        float left = (w - glassW) / 2f;
        float top = (h - glassH) / 2f;
        float right = left + glassW;
        float bottom = top + glassH;
        float corner = 64f;

        Path glassPath = new Path();
        glassPath.addRoundRect(left, top, right, bottom, corner, corner, Path.Direction.CW);

        canvas.saveLayerAlpha(0, 0, w, h, 255);
        canvas.clipPath(glassPath);

        glassPaint.setShader(new LinearGradient(left, top, right, bottom,
                new int[]{
                        Color.argb(60, 255, 255, 255),
                        Color.argb(20, 255, 255, 255),
                        Color.argb(50, 255, 255, 255)
                }, null, Shader.TileMode.CLAMP));
        canvas.drawRect(left, top, right, bottom, glassPaint);

        if (touching) {
            dispersionPhase += 0.15f;
            if (dispersionPhase > 6.28f) dispersionPhase -= 6.28f;

            float dispRadius = Math.max(glassW, glassH) * 0.6f;
            float dx = (float) Math.cos(dispersionPhase) * dispRadius * 0.3f;
            float dy = (float) Math.sin(dispersionPhase) * dispRadius * 0.3f;

            dispersionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            dispersionPaint.setShader(new RadialGradient(
                    touchX + dx, touchY + dy, dispRadius,
                    new int[]{
                            Color.argb(90, 255, 0, 120),
                            Color.argb(70, 0, 180, 255),
                            Color.argb(50, 120, 255, 0),
                            Color.TRANSPARENT
                    },
                    new float[]{0f, 0.35f, 0.6f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawCircle(touchX + dx, touchY + dy, dispRadius, dispersionPaint);
            dispersionPaint.setXfermode(null);
        }

        highlightPaint.setShader(new LinearGradient(left, top, left, bottom,
                new int[]{
                        Color.argb(160, 255, 255, 255),
                        Color.argb(20, 255, 255, 255),
                        Color.TRANSPARENT
                }, new float[]{0f, 0.4f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(left, top, right, top + glassH * 0.5f, highlightPaint);

        canvas.restore();

        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(4f);
        edgePaint.setShader(new LinearGradient(left, top, right, bottom,
                new int[]{
                        Color.argb(200, 255, 255, 255),
                        Color.argb(80, 180, 220, 255),
                        Color.argb(200, 255, 255, 255)
                }, null, Shader.TileMode.CLAMP));
        canvas.drawPath(glassPath, edgePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                touchX = event.getX();
                touchY = event.getY();
                touching = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touching = false;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
