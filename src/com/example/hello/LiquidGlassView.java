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
    private float touchX = -999, touchY = -999;
    private boolean touching = false;
    private float phase = 0f;

    public LiquidGlassView(Context context) {
        super(context);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth(), h = getHeight();
        float gw = w * 0.85f, gh = h * 0.45f;
        float l = (w - gw) / 2f, t = (h - gh) / 2f, r = l + gw, b = t + gh;
        float corner = 60f;

        Path glass = new Path();
        glass.addRoundRect(l, t, r, b, corner, corner, Path.Direction.CW);

        canvas.saveLayerAlpha(0, 0, w, h, 255);
        canvas.clipPath(glass);

        glassPaint.setShader(new LinearGradient(l, t, r, b,
                new int[]{Color.argb(70, 255, 255, 255), Color.argb(15, 255, 255, 255), Color.argb(60, 255, 255, 255)},
                null, Shader.TileMode.CLAMP));
        canvas.drawRect(l, t, r, b, glassPaint);

        if (touching) {
            phase += 0.2f;
            if (phase > 6.28f) phase -= 6.28f;
            float dispR = Math.max(gw, gh) * 0.65f;
            float dx = (float) Math.cos(phase) * dispR * 0.35f;
            float dy = (float) Math.sin(phase) * dispR * 0.35f;
            dispersionPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            dispersionPaint.setShader(new RadialGradient(touchX + dx, touchY + dy, dispR,
                    new int[]{Color.argb(120, 255, 0, 120), Color.argb(90, 0, 180, 255), Color.argb(60, 120, 255, 0), Color.TRANSPARENT},
                    new float[]{0f, 0.35f, 0.65f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawCircle(touchX + dx, touchY + dy, dispR, dispersionPaint);
            dispersionPaint.setXfermode(null);
        }

        highlightPaint.setShader(new LinearGradient(l, t, l, b,
                new int[]{Color.argb(180, 255, 255, 255), Color.argb(20, 255, 255, 255), Color.TRANSPARENT},
                new float[]{0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(l, t, r, t + gh * 0.55f, highlightPaint);
        canvas.restore();

        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(3f);
        edgePaint.setShader(new LinearGradient(l, t, r, b,
                new int[]{Color.argb(220, 255, 255, 255), Color.argb(100, 180, 220, 255), Color.argb(220, 255, 255, 255)},
                null, Shader.TileMode.CLAMP));
        canvas.drawPath(glass, edgePaint);

        postInvalidateOnAnimation();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                touchX = e.getX(); touchY = e.getY(); touching = true; invalidate(); return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touching = false; invalidate(); return true;
        }
        return super.onTouchEvent(e);
    }
}
