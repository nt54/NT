package com.example.hello;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardPopup {

    public static void show(Context context, View parent) {
        final FrameLayout root = (FrameLayout) parent;

        final LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(60, 60, 60, 60);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#00E5FF"), Color.parseColor("#7C4DFF")});
        bg.setCornerRadius(48f);
        card.setBackground(bg);

        TextView title = new TextView(context);
        title.setText("Advanced UI");
        title.setTextColor(Color.WHITE);
        title.setTextSize(22);
        card.addView(title);

        TextView sub = new TextView(context);
        sub.setText("Material • Motion • Depth");
        sub.setTextColor(Color.parseColor("#CCFFFFFF"));
        sub.setTextSize(13);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.topMargin = 24;
        card.addView(sub, p);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        root.addView(card, params);

        card.setScaleX(0.3f);
        card.setScaleY(0.3f);
        card.setAlpha(0f);
        card.animate().scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator(2f))
                .start();

        ObjectAnimator floatAnim = ObjectAnimator.ofFloat(card, "translationY", -12f, 12f);
        floatAnim.setDuration(1800);
        floatAnim.setRepeatCount(ObjectAnimator.INFINITE);
        floatAnim.setRepeatMode(ObjectAnimator.REVERSE);
        floatAnim.start();

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate().scaleX(0f).scaleY(0f).alpha(0f)
                        .setDuration(300)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                root.removeView(card);
                            }
                        }).start();
            }
        });
    }
}
