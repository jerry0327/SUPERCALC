package com.jerry0327.supercalc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BG = Color.rgb(5, 9, 20);
    private final int PANEL = Color.rgb(13, 24, 38);
    private final int PANEL_2 = Color.rgb(20, 34, 52);
    private final int CYAN = Color.rgb(0, 229, 255);
    private final int PURPLE = Color.rgb(177, 92, 255);
    private final int TEXT = Color.rgb(235, 246, 255);
    private final int MUTED = Color.rgb(160, 180, 200);

    private LinearLayout root;
    private LinearLayout content;
    private TextView expressionView;
    private TextView resultView;
    private boolean degreeMode = true;
    private final CalculatorEngine engine = new CalculatorEngine();
    private final ArrayList<String> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadHistory();
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        setContentView(root);
        buildHeader();
        buildNav();
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(18, 18, 18, 28);
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        showCalculator();
    }

    private void buildHeader() {
        TextView title = label("SUPERCALC 超級計算機", 26, CYAN, true);
        title.setGravity(Gravity.CENTER);
        title.setPadding(8, 22, 8, 4);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));
        TextView sub = label("科學運算 · 3D PLOT · 生活工具 · 貪吃蛇彩蛋", 14, MUTED, false);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(8, 0, 8, 16);
        root.addView(sub, new LinearLayout.LayoutParams(-1, -2));
    }

    private void buildNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setPadding(12, 8, 12, 8);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(Color.rgb(7, 14, 26));
        root.addView(nav, new LinearLayout.LayoutParams(-1, -2));
        nav.addView(navButton("計算機", new View.OnClickListener() { public void onClick(View v) { showCalculator(); }}), weightParams());
        nav.addView(navButton("3D Plot", new View.OnClickListener() { public void onClick(View v) { showPlot(); }}), weightParams());
        nav.addView(navButton("工具箱", new View.OnClickListener() { public void onClick(View v) { showTools(); }}), weightParams());
        nav.addView(navButton("歷史", new View.OnClickListener() { public void onClick(View v) { showHistory(); }}), weightParams());
    }

    private Button navButton(String text, View.OnClickListener listener) {
        Button b = button(text, CYAN, Color.rgb(12, 25, 40));
        b.setTextSize(13);
        b.setOnClickListener(listener);
        return b;
    }

    private LinearLayout.LayoutParams weightParams() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(4, 0, 4, 0);
        return lp;
    }

    private Button button(String text, int fg, int bg) {
        Button b = new Button(this);
        b.setAllCaps(false);
        b.setText(text);
        b.setTextColor(fg);
        b.setTextSize(16);
        GradientDrawable d = new GradientDrawable();
        d.setColor(bg);
        d.setCornerRadius(18);
        d.setStroke(2, Color.argb(110, Color.red(fg), Color.green(fg), Color.blue(fg)));
        b.setBackground(d);
        return b;
    }

    private TextView label(String text, int size, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(color);
        tv.setPadding(6, 8, 6, 8);
        if (bold) tv.setTypeface(Typeface.DEFAULT_BOLD);
        return tv;
    }

    private LinearLayout panel() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(18, 18, 18, 18);
        GradientDrawable d = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{PANEL, Color.rgb(7, 16, 30)});
        d.setCornerRadius(26);
        d.setStroke(2, Color.argb(120, 0, 229, 255));
        box.setBackground(d);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, 18);
        box.setLayoutParams(lp);
        return box;
    }

    private void showCalculator() {
        content.removeAllViews();
        LinearLayout display = panel();
        display.addView(label("科學計算機", 18, PURPLE, true));
        expressionView = label("", 24, TEXT, false);
        expressionView.setGravity(Gravity.RIGHT);
        resultView = label("0", 40, CYAN, true);
        resultView.setGravity(Gravity.RIGHT);
        display.addView(expressionView);
        display.addView(resultView);

        LinearLayout mode = new LinearLayout(this);
        mode.setOrientation(LinearLayout.HORIZONTAL);
        Button deg = button("DEG", degreeMode ? Color.BLACK : CYAN, degreeMode ? CYAN : PANEL_2);
        Button rad = button("RAD", !degreeMode ? Color.BLACK : CYAN, !degreeMode ? CYAN : PANEL_2);
        deg.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { degreeMode = true; engine.setDegreeMode(true); showCalculator(); }});
        rad.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { degreeMode = false; engine.setDegreeMode(false); showCalculator(); }});
        mode.addView(deg, weightParams());
        mode.addView(rad, weightParams());
        display.addView(mode);
        content.addView(display);

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(5);
        String[] keys = {
                "AC", "DEL", "(", ")", "%",
                "sin", "cos", "tan", "log", "ln",
                "π", "e", "x²", "xʸ", "√",
                "abs", "mod", "!", "÷", "×",
                "7", "8", "9", "−", "+",
                "4", "5", "6", ".", "=",
                "1", "2", "3", "0", "Ans"
        };
        for (String key : keys) addCalcKey(grid, key);
        content.addView(grid);
        content.addView(label("提示：mod 是取餘數，例如 10 mod 3 = 1；% 是百分比，例如 50% = 0.5。", 14, MUTED, false));
    }

    private void addCalcKey(GridLayout grid, final String key) {
        int fg = TEXT;
        int bg = PANEL_2;
        if (key.equals("=")) { fg = Color.BLACK; bg = CYAN; }
        if (key.equals("AC")) { fg = Color.WHITE; bg = Color.rgb(150, 50, 50); }
        if (isFunction(key)) fg = Color.rgb(165, 255, 190);
        Button b = button(key, fg, bg);
        b.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { handleKey(key); }});
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = 0;
        lp.height = 72;
        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        lp.setMargins(5, 5, 5, 5);
        grid.addView(b, lp);
    }

    private boolean isFunction(String key) {
        return key.equals("sin") || key.equals("cos") || key.equals("tan") || key.equals("log") || key.equals("ln") || key.equals("√") || key.equals("abs") || key.equals("x²") || key.equals("xʸ") || key.equals("π") || key.equals("e") || key.equals("mod");
    }

    private void handleKey(String key) {
        String exp = expressionView.getText().toString();
        if (key.equals("AC")) { expressionView.setText(""); resultView.setText("0"); return; }
        if (key.equals("DEL")) { if (exp.length() > 0) expressionView.setText(exp.substring(0, exp.length() - 1)); return; }
        if (key.equals("=")) {
            String result = engine.calculate(exp);
            resultView.setText(result);
            if (!exp.trim().isEmpty()) addHistory(exp + " = " + result);
            return;
        }
        if (key.equals("Ans")) expressionView.append(resultView.getText().toString());
        else if (key.equals("sin") || key.equals("cos") || key.equals("tan") || key.equals("log") || key.equals("ln") || key.equals("abs")) expressionView.append(key + "(");
        else if (key.equals("√")) expressionView.append("√(");
        else if (key.equals("x²")) expressionView.append("^2");
        else if (key.equals("xʸ")) expressionView.append("^");
        else if (key.equals("mod")) expressionView.append(" mod ");
        else expressionView.append(key);
    }

    private void showPlot() {
        content.removeAllViews();
        final Plot3DView plot = new Plot3DView(this);
        LinearLayout box = panel();
        box.addView(label("3D PLOT 繪圖", 18, PURPLE, true));
        final TextView functionName = label(plot.getFunctionName(), 20, CYAN, true);
        box.addView(functionName);
        box.addView(plot, new LinearLayout.LayoutParams(-1, 620));

        GridLayout functions = new GridLayout(this);
        functions.setColumnCount(2);
        String[] names = {"sin(x)*cos(y)", "sin(r)/r", "x²-y²", "cos(x)+sin(y)"};
        for (int i = 0; i < names.length; i++) {
            final int mode = i;
            Button b = button(names[i], CYAN, PANEL_2);
            b.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { plot.setFunctionMode(mode); functionName.setText(plot.getFunctionName()); }});
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = 64;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(5, 5, 5, 5);
            functions.addView(b, lp);
        }
        box.addView(functions);
        box.addView(slider("旋轉 X", 67, new SeekAction() { public void change(int value) { plot.setRotationXValue(value - 50); }}));
        box.addView(slider("旋轉 Y", 15, new SeekAction() { public void change(int value) { plot.setRotationYValue(value - 50); }}));
        box.addView(slider("縮放", 45, new SeekAction() { public void change(int value) { plot.setZoomValue(0.5 + value / 50.0); }}));
        content.addView(box);
    }

    private interface SeekAction { void change(int value); }

    private LinearLayout slider(String title, int progress, final SeekAction action) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.addView(label(title, 14, MUTED, false));
        SeekBar seek = new SeekBar(this);
        seek.setMax(100);
        seek.setProgress(progress);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean fromUser) { action.change(p); }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });
        box.addView(seek);
        return box;
    }

    private void showTools() {
        content.removeAllViews();
        content.addView(label("多功能工具箱", 22, CYAN, true));
        addUnitTool();
        addBmiTool();
        addLoanTool();
        addDiscountTool();
        addSnakeTool();
    }

    private EditText input(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextColor(TEXT);
        e.setHintTextColor(Color.rgb(105, 130, 150));
        e.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        e.setSingleLine(true);
        return e;
    }

    private void addUnitTool() {
        LinearLayout p = panel();
        p.addView(label("單位換算", 18, PURPLE, true));
        final EditText value = input("輸入數值，例如 1.5");
        final TextView out = label("結果會顯示在這裡", 16, CYAN, false);
        p.addView(value);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        Button cm = button("公尺→公分", CYAN, PANEL_2);
        Button kg = button("公斤→磅", CYAN, PANEL_2);
        Button temp = button("°C→°F", CYAN, PANEL_2);
        cm.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { out.setText(fmt(num(value) * 100) + " cm"); }});
        kg.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { out.setText(fmt(num(value) * 2.20462262) + " lb"); }});
        temp.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { out.setText(fmt(num(value) * 9 / 5 + 32) + " °F"); }});
        row.addView(cm, weightParams()); row.addView(kg, weightParams()); row.addView(temp, weightParams());
        p.addView(row); p.addView(out); content.addView(p);
    }

    private void addBmiTool() {
        LinearLayout p = panel();
        p.addView(label("BMI 計算", 18, PURPLE, true));
        final EditText height = input("身高 cm");
        final EditText weight = input("體重 kg");
        final TextView out = label("BMI 結果", 16, CYAN, false);
        p.addView(height); p.addView(weight);
        Button calc = button("計算 BMI", Color.BLACK, CYAN);
        calc.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
            double h = num(height) / 100.0;
            double w = num(weight);
            if (h <= 0) { out.setText("請輸入身高"); return; }
            double bmi = w / (h * h);
            String level = bmi < 18.5 ? "過輕" : bmi < 24 ? "正常" : bmi < 27 ? "過重" : "肥胖";
            out.setText("BMI = " + fmt(bmi) + "，狀態：" + level);
        }});
        p.addView(calc); p.addView(out); content.addView(p);
    }

    private void addLoanTool() {
        LinearLayout p = panel();
        p.addView(label("貸款月付試算", 18, PURPLE, true));
        final EditText principal = input("本金，例如 1000000");
        final EditText rate = input("年利率 %，例如 2");
        final EditText years = input("年數，例如 20");
        final TextView out = label("月付款結果", 16, CYAN, false);
        p.addView(principal); p.addView(rate); p.addView(years);
        Button calc = button("試算月付款", Color.BLACK, CYAN);
        calc.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
            double p0 = num(principal);
            double r = num(rate) / 100.0 / 12.0;
            int n = (int)(num(years) * 12);
            if (p0 <= 0 || n <= 0) { out.setText("請輸入本金與期數"); return; }
            double monthly = r == 0 ? p0 / n : p0 * r * Math.pow(1 + r, n) / (Math.pow(1 + r, n) - 1);
            out.setText("每月約：" + fmt(monthly) + " 元，總付款：" + fmt(monthly * n) + " 元");
        }});
        p.addView(calc); p.addView(out); content.addView(p);
    }

    private void addDiscountTool() {
        LinearLayout p = panel();
        p.addView(label("折扣 / 稅率試算", 18, PURPLE, true));
        final EditText price = input("原價，例如 1200");
        final EditText discount = input("折扣 %，例如 20");
        final EditText tax = input("稅率 %，例如 5");
        final TextView out = label("折扣後金額", 16, CYAN, false);
        p.addView(price); p.addView(discount); p.addView(tax);
        Button calc = button("計算折扣含稅", Color.BLACK, CYAN);
        calc.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
            double base = num(price);
            double after = base * (1 - num(discount) / 100.0);
            double total = after * (1 + num(tax) / 100.0);
            out.setText("折扣後：" + fmt(after) + "，含稅：" + fmt(total));
        }});
        p.addView(calc); p.addView(out); content.addView(p);
    }

    private void addSnakeTool() {
        LinearLayout p = panel();
        p.addView(label("隱藏彩蛋：貪吃蛇", 18, PURPLE, true));
        final SnakeView snake = new SnakeView(this);
        p.addView(snake, new LinearLayout.LayoutParams(-1, 500));
        GridLayout controls = new GridLayout(this);
        controls.setColumnCount(3);
        String[] keys = {" ", "↑", " ", "←", "重開", "→", "暫停", "↓", "開始"};
        for (final String s : keys) {
            Button b = button(s, CYAN, PANEL_2);
            b.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
                if (s.equals("↑")) snake.changeDirection(0, -1);
                else if (s.equals("↓")) snake.changeDirection(0, 1);
                else if (s.equals("←")) snake.changeDirection(-1, 0);
                else if (s.equals("→")) snake.changeDirection(1, 0);
                else if (s.equals("重開")) { snake.reset(); snake.start(); }
                else if (s.equals("暫停")) snake.pause();
                else if (s.equals("開始")) snake.start();
            }});
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = 66;
            lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            lp.setMargins(5, 5, 5, 5);
            controls.addView(b, lp);
        }
        p.addView(controls); content.addView(p);
    }

    private void showHistory() {
        content.removeAllViews();
        LinearLayout p = panel();
        p.addView(label("歷史紀錄", 20, CYAN, true));
        if (history.isEmpty()) p.addView(label("目前沒有紀錄。", 16, MUTED, false));
        for (int i = history.size() - 1; i >= 0; i--) {
            final String item = history.get(i);
            Button row = button(item, TEXT, PANEL_2);
            row.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            row.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {
                showCalculator();
                int cut = item.lastIndexOf(" = ");
                expressionView.setText(cut > 0 ? item.substring(0, cut) : item);
            }});
            p.addView(row, new LinearLayout.LayoutParams(-1, -2));
        }
        Button clear = button("清除全部歷史", Color.WHITE, Color.rgb(150, 50, 50));
        clear.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { history.clear(); saveHistory(); showHistory(); }});
        p.addView(clear);
        content.addView(p);
    }

    private void addHistory(String item) {
        history.add(item);
        while (history.size() > 40) history.remove(0);
        saveHistory();
    }

    private void loadHistory() {
        SharedPreferences sp = getSharedPreferences("supercalc", MODE_PRIVATE);
        String raw = sp.getString("history", "");
        history.clear();
        if (!raw.isEmpty()) {
            String[] lines = raw.split("\\n");
            for (String line : lines) if (!line.trim().isEmpty()) history.add(line);
        }
    }

    private void saveHistory() {
        StringBuilder sb = new StringBuilder();
        for (String item : history) sb.append(item).append('\n');
        getSharedPreferences("supercalc", MODE_PRIVATE).edit().putString("history", sb.toString()).apply();
    }

    private double num(EditText editText) {
        try { return Double.parseDouble(editText.getText().toString().trim()); }
        catch (Exception ignored) { return 0; }
    }

    private String fmt(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.000001) return String.format(Locale.US, "%.0f", value);
        return String.format(Locale.US, "%.2f", value);
    }
}
