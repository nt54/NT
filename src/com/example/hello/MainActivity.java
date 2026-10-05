package com.example.hello;

import android.app.Activity;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        LiquidGlassView glass = new LiquidGlassView(this);
        root.addView(glass, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout center = new LinearLayout(this);
        center.setOrientation(LinearLayout.VERTICAL);
        center.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams centerParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        centerParams.gravity = Gravity.CENTER;
        root.addView(center, centerParams);

        final TextView title = new TextView(this);
        title.setText("Hello Termux");
        title.setTextColor(Color.WHITE);
        title.setTextSize(34);
        title.setShadowLayer(30, 0, 0, Color.parseColor("#00E5FF"));
        center.addView(title);

        final TextView subtitle = new TextView(this);
        subtitle.setText("Touch the glass to see dispersion");
        subtitle.setTextColor(Color.parseColor("#88FFFFFF"));
        subtitle.setTextSize(13);
        LinearLayout.LayoutParams subParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subParams.topMargin = 36;
        center.addView(subtitle, subParams);

        title.setScaleX(0f);
        title.setScaleY(0f);
        title.animate().scaleX(1f).scaleY(1f).setDuration(900)
                .setInterpolator(new OvershootInterpolator(2f)).start();

        ObjectAnimator floatAnim = ObjectAnimator.ofPropertyValuesHolder(
                title,
                PropertyValuesHolder.ofFloat("translationY", -18f, 18f));
        floatAnim.setDuration(2400);
        floatAnim.setRepeatCount(ValueAnimator.INFINITE);
        floatAnim.setRepeatMode(ValueAnimator.REVERSE);
        floatAnim.start();

        ObjectAnimator glow = ObjectAnimator.ofFloat(title, "alpha", 0.6f, 1f);
        glow.setDuration(1200);
        glow.setRepeatCount(ValueAnimator.INFINITE);
        glow.setRepeatMode(ValueAnimator.REVERSE);
        glow.start();
    }

    private class ParticleView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Random random = new Random();
        private final float[] xs = new float[80];
        private final float[] ys = new float[80];
        private final float[] rs = new float[80];
        private final float[] speeds = new float[80];

        ParticleView() {
            super(MainActivity.this);
            for (int i = 0; i < xs.length; i++) {
                xs[i] = random.nextFloat();
                ys[i] = random.nextFloat();
                rs[i] = 1f + random.nextFloat() * 3f;
                speeds[i] = 0.002f + random.nextFloat() * 0.006f;
            }
            postInvalidateOnAnimation();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int w = getWidth();
            int h = getHeight();
            for (int i = 0; i < xs.length; i++) {
                ys[i] -= speeds[i];
                if (ys[i] < 0) {
                    ys[i] = 1f;
                    xs[i] = random.nextFloat();
                }
                float cx = xs[i] * w;
                float cy = ys[i] * h;
                paint.setShader(new RadialGradient(cx, cy, rs[i] * 6,
                        Color.parseColor("#66FFFFFF"), Color.TRANSPARENT,
                        Shader.TileMode.CLAMP));
                canvas.drawCircle(cx, cy, rs[i] * 6, paint);
            }
            postInvalidateOnAnimation();
        }
    }
}
