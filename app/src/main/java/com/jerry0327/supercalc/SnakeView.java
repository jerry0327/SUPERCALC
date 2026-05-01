package com.jerry0327.supercalc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class SnakeView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler handler = new Handler();
    private final Random random = new Random();
    private final ArrayList<int[]> snake = new ArrayList<>();
    private int cols = 24;
    private int rows = 16;
    private int dirX = 1;
    private int dirY = 0;
    private int foodX = 8;
    private int foodY = 8;
    private int score = 0;
    private boolean running = false;
    private boolean gameOver = false;

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (running) {
                step();
                invalidate();
                handler.postDelayed(this, 140);
            }
        }
    };

    public SnakeView(Context context) {
        super(context);
        reset();
    }

    public void start() {
        if (!running) {
            running = true;
            handler.postDelayed(tick, 140);
        }
    }

    public void pause() {
        running = false;
    }

    public void reset() {
        snake.clear();
        snake.add(new int[]{6, 8});
        snake.add(new int[]{5, 8});
        snake.add(new int[]{4, 8});
        dirX = 1;
        dirY = 0;
        score = 0;
        gameOver = false;
        spawnFood();
        invalidate();
    }

    public void changeDirection(int x, int y) {
        if (snake.size() > 1 && x == -dirX && y == -dirY) return;
        dirX = x;
        dirY = y;
        start();
    }

    public int getScore() { return score; }

    private void step() {
        if (gameOver || snake.isEmpty()) return;
        int[] head = snake.get(0);
        int nx = head[0] + dirX;
        int ny = head[1] + dirY;
        if (nx < 0 || nx >= cols || ny < 0 || ny >= rows || hitsSelf(nx, ny)) {
            gameOver = true;
            running = false;
            return;
        }
        snake.add(0, new int[]{nx, ny});
        if (nx == foodX && ny == foodY) {
            score += 10;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private boolean hitsSelf(int x, int y) {
        for (int[] part : snake) {
            if (part[0] == x && part[1] == y) return true;
        }
        return false;
    }

    private void spawnFood() {
        do {
            foodX = random.nextInt(cols);
            foodY = random.nextInt(rows);
        } while (hitsSelf(foodX, foodY));
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(2, 12, 10));
        int w = getWidth();
        int h = getHeight();
        float cell = Math.min(w / (float) cols, h / (float) rows);
        float left = (w - cols * cell) / 2f;
        float top = (h - rows * cell) / 2f;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f);
        paint.setColor(Color.argb(70, 0, 255, 120));
        for (int c = 0; c <= cols; c++) canvas.drawLine(left + c * cell, top, left + c * cell, top + rows * cell, paint);
        for (int r = 0; r <= rows; r++) canvas.drawLine(left, top + r * cell, left + cols * cell, top + r * cell, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(255, 80, 80));
        canvas.drawRect(left + foodX * cell + 4, top + foodY * cell + 4, left + (foodX + 1) * cell - 4, top + (foodY + 1) * cell - 4, paint);

        paint.setColor(Color.rgb(100, 255, 90));
        for (int i = 0; i < snake.size(); i++) {
            int[] p = snake.get(i);
            paint.setColor(i == 0 ? Color.rgb(170, 255, 120) : Color.rgb(80, 230, 75));
            canvas.drawRoundRect(left + p[0] * cell + 3, top + p[1] * cell + 3, left + (p[0] + 1) * cell - 3, top + (p[1] + 1) * cell - 3, 6, 6, paint);
        }

        paint.setColor(Color.rgb(120, 255, 160));
        paint.setTextSize(28f);
        canvas.drawText("SNAKE   SCORE: " + score, 20, 36, paint);
        if (gameOver) {
            paint.setTextSize(42f);
            paint.setColor(Color.rgb(255, 90, 90));
            canvas.drawText("GAME OVER", w / 2f - 115, h / 2f, paint);
        } else if (!running) {
            paint.setTextSize(28f);
            paint.setColor(Color.rgb(180, 255, 210));
            canvas.drawText("按方向鍵開始", w / 2f - 85, h - 28, paint);
        }
    }
}
