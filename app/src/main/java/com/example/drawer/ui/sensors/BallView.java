package com.example.drawer.ui.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BallView extends View {

    private float ballX, ballY; // Ball position
    private float ballRadius = 50f; // Ball radius
    private Paint ballPaint;

    private float screenWidth, screenHeight; // Screen dimensions

    public BallView(Context context) {
        super(context);
        init();
    }

    public BallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ballPaint = new Paint();
        ballPaint.setColor(Color.BLUE);
        ballPaint.setStyle(Paint.Style.FILL);
        ballX = 0;
        ballY = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the ball
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
    }

    public void updateBallPosition(float x, float y) {
        // Ensure the ball stays within the screen borders
        ballX = Math.max(ballRadius, Math.min(x, screenWidth - ballRadius));
        ballY = Math.max(ballRadius, Math.min(y, screenHeight - ballRadius));
        invalidate(); // Redraw the view
    }

    public void setScreenDimensions(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }
}