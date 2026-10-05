package com.example.hello;

import android.app.Activity;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#070B14"));
        setContentView(root);

        ParticleView particles = new ParticleView();
        root.addView(particles, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        LiquidGlassView glass = new LiquidGlassView(this);
        root.addView(glass, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        cp.gravity = Gravity.CENTER;
        root.addView(center, cp);

        TextView title = new TextView(this);
        title.setText("Liquid Glass");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setShadowLayer(30, 0, 0, Color.parseColor("#00E5FF"));
        center.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Touch the glass for dispersion");
        sub.setTextColor(Color.parseColor("#88FFFFFF"));
        sub.setTextSize(13);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sp.topMargin = 36;
        center.addView(sub, sp);

        title.setScaleX(0f); title.setScaleY(0f);
        title.animate().scaleX(1f).scaleY(1f).setDuration(900)
                .setInterpolator(new OvershootInterpolator(2f)).start();

        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(title, "translationY", -18f, 18f);
        floatAnim.setDuration(2400);
        floatAnim.setRepeatCount(ValueAnimator.INFINITE);
        floatAnim.setRepeatMode(ValueAnimator.REVERSE);
        floatAnim.start();
    }

    private class ParticleView extends View {
        private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random r = new Random();
        private final float[] xs = new float[80], ys = new float[80], rs = new float[80], sp = new float[80];
        ParticleView() {
            super(MainActivity.this);
            for (int i = 0; i < 80; i++) {
                xs[i] = r.nextFloat(); ys[i] = r.nextFloat();
                rs[i] = 1f + r.nextFloat() * 3f;
                sp[i] = 0.002f + r.nextFloat() * 0.006f;
            }
            postInvalidateOnAnimation();
        }
        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);
            int w = getWidth(), h = getHeight();
            for (int i = 0; i < 80; i++) {
                ys[i] -= sp[i];
                if (ys[i] < 0) { ys[i] = 1f; xs[i] = r.nextFloat(); }
                float cx = xs[i] * w, cy = ys[i] * h;
                p.setShader(new RadialGradient(cx, cy, rs[i] * 6,
                        Color.parseColor("#66FFFFFF"), Color.TRANSPARENT, Shader.TileMode.CLAMP));
                c.drawCircle(cx, cy, rs[i] * 6, p);
            }
            postInvalidateOnAnimation();
        }
    }
}
