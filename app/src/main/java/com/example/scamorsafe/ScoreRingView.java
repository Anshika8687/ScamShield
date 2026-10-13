package com.example.scamorsafe;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScoreRingView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint smallPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private float shown = 0f;

    public ScoreRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        float d = getResources().getDisplayMetrics().density;

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(11 * d);
        trackPaint.setColor(0x33FFFFFF);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(11 * d);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        numberPaint.setColor(0xFFFFFFFF);
        numberPaint.setTextSize(34 * d);
        numberPaint.setFakeBoldText(true);
        numberPaint.setTextAlign(Paint.Align.CENTER);

        smallPaint.setColor(0xFFB8B0E8);
        smallPaint.setTextSize(12 * d);
        smallPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setScore(int score) {
        float target = Math.max(0, Math.min(100, score));
        ValueAnimator a = ValueAnimator.ofFloat(shown, target);
        a.setDuration(800);
        a.addUpdateListener(v -> {
            shown = (float) v.getAnimatedValue();
            invalidate();
        });
        a.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float stroke = trackPaint.getStrokeWidth();
        float size = Math.min(getWidth(), getHeight());
        float left = (getWidth() - size) / 2f + stroke / 2f;
        float top = (getHeight() - size) / 2f + stroke / 2f;
        rect.set(left, top, left + size - stroke, top + size - stroke);

        if (shown < 40) arcPaint.setColor(0xFFEF4444);
        else if (shown < 70) arcPaint.setColor(0xFFF59E0B);
        else arcPaint.setColor(0xFF22C55E);

        canvas.drawArc(rect, 0, 360, false, trackPaint);
        canvas.drawArc(rect, -90, 360f * shown / 100f, false, arcPaint);

        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float baseline = cy + numberPaint.getTextSize() * 0.15f;
        canvas.drawText(String.valueOf(Math.round(shown)), cx, baseline, numberPaint);
        canvas.drawText("/100", cx, baseline + smallPaint.getTextSize() * 1.4f, smallPaint);
    }
}