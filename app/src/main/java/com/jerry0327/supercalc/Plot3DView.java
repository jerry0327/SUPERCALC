package com.jerry0327.supercalc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Plot3DView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private double rotX = 35;
    private double rotY = -35;
    private double zoom = 1.0;
    private int functionMode = 0;

    public Plot3DView(Context context) {
        super(context);
        paint.setStrokeWidth(2f);
        paint.setTextSize(28f);
    }

    public void setRotationXValue(double value) { rotX = value; invalidate(); }
    public void setRotationYValue(double value) { rotY = value; invalidate(); }
    public void setZoomValue(double value) { zoom = value; invalidate(); }
    public void setFunctionMode(int mode) { functionMode = mode; invalidate(); }

    public String getFunctionName() {
        switch (functionMode) {
            case 1: return "z = sin(r) / r";
            case 2: return "z = x² - y²";
            case 3: return "z = cos(x) + sin(y)";
            default: return "z = sin(x) * cos(y)";
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.rgb(5, 9, 20));
        int w = getWidth();
        int h = getHeight();
        float cx = w / 2f;
        float cy = h / 2f + 25;
        float scale = Math.min(w, h) / 5.0f * (float) zoom;

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        drawGrid(canvas, cx, cy, scale);

        int steps = 36;
        double range = 6.0;
        for (int i = 0; i <= steps; i++) {
            double x = -range + 2 * range * i / steps;
            drawLineStrip(canvas, true, x, steps, range, cx, cy, scale);
        }
        for (int j = 0; j <= steps; j++) {
            double y = -range + 2 * range * j / steps;
            drawLineStrip(canvas, false, y, steps, range, cx, cy, scale);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0, 229, 255));
        paint.setTextSize(34f);
        canvas.drawText(getFunctionName(), 28, 48, paint);
        paint.setTextSize(24f);
        paint.setColor(Color.rgb(180, 220, 255));
        canvas.drawText("拖動滑桿可旋轉 / 縮放 3D 曲面", 28, 82, paint);
    }

    private void drawGrid(Canvas canvas, float cx, float cy, float scale) {
        paint.setColor(Color.argb(90, 100, 180, 255));
        for (int i = -6; i <= 6; i += 2) {
            float[] a = project(-6, i, 0, cx, cy, scale);
            float[] b = project(6, i, 0, cx, cy, scale);
            canvas.drawLine(a[0], a[1], b[0], b[1], paint);
            float[] c = project(i, -6, 0, cx, cy, scale);
            float[] d = project(i, 6, 0, cx, cy, scale);
            canvas.drawLine(c[0], c[1], d[0], d[1], paint);
        }
        paint.setStrokeWidth(4f);
        paint.setColor(Color.rgb(0, 229, 255));
        axis(canvas, cx, cy, scale, -7, 0, 0, 7, 0, 0, "x");
        paint.setColor(Color.rgb(120, 255, 160));
        axis(canvas, cx, cy, scale, 0, -7, 0, 0, 7, 0, "y");
        paint.setColor(Color.rgb(220, 120, 255));
        axis(canvas, cx, cy, scale, 0, 0, -3, 0, 0, 3, "z");
        paint.setStrokeWidth(2f);
    }

    private void axis(Canvas canvas, float cx, float cy, float scale, double x1, double y1, double z1, double x2, double y2, double z2, String label) {
        float[] a = project(x1, y1, z1, cx, cy, scale);
        float[] b = project(x2, y2, z2, cx, cy, scale);
        canvas.drawLine(a[0], a[1], b[0], b[1], paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(26f);
        canvas.drawText(label, b[0] + 8, b[1] - 8, paint);
        paint.setStyle(Paint.Style.STROKE);
    }

    private void drawLineStrip(Canvas canvas, boolean fixedX, double fixed, int steps, double range, float cx, float cy, float scale) {
        float[] prev = null;
        for (int k = 0; k <= steps; k++) {
            double v = -range + 2 * range * k / steps;
            double x = fixedX ? fixed : v;
            double y = fixedX ? v : fixed;
            double z = f(x, y);
            float[] p = project(x, y, z, cx, cy, scale);
            int color = Color.HSVToColor(new float[]{(float) ((z + 2.5) * 55 % 360), 0.85f, 1.0f});
            paint.setColor(color);
            if (prev != null) canvas.drawLine(prev[0], prev[1], p[0], p[1], paint);
            prev = p;
        }
    }

    private double f(double x, double y) {
        switch (functionMode) {
            case 1:
                double r = Math.sqrt(x * x + y * y);
                if (r < 0.0001) return 1.0;
                return 3 * Math.sin(r) / r;
            case 2:
                return (x * x - y * y) / 18.0;
            case 3:
                return Math.cos(x) + Math.sin(y);
            default:
                return Math.sin(x) * Math.cos(y) * 2.0;
        }
    }

    private float[] project(double x, double y, double z, float cx, float cy, float scale) {
        double ax = Math.toRadians(rotX);
        double ay = Math.toRadians(rotY);
        double y1 = y * Math.cos(ax) - z * Math.sin(ax);
        double z1 = y * Math.sin(ax) + z * Math.cos(ax);
        double x1 = x * Math.cos(ay) + z1 * Math.sin(ay);
        double z2 = -x * Math.sin(ay) + z1 * Math.cos(ay);
        double depth = 10.0 / (10.0 + z2);
        return new float[]{(float) (cx + x1 * scale * depth), (float) (cy - y1 * scale * depth)};
    }
}
