package com.jerry0327.supercalc;

import java.util.Locale;

public class CalculatorEngine {
    private String input;
    private int pos;
    private boolean degreeMode = true;

    public void setDegreeMode(boolean degreeMode) {
        this.degreeMode = degreeMode;
    }

    public String calculate(String expression) {
        try {
            double value = evaluate(expression);
            if (Double.isNaN(value) || Double.isInfinite(value)) return "錯誤";
            if (Math.abs(value - Math.rint(value)) < 0.0000000001) {
                return String.format(Locale.US, "%.0f", value);
            }
            String out = String.format(Locale.US, "%.10f", value);
            while (out.contains(".") && out.endsWith("0")) out = out.substring(0, out.length() - 1);
            if (out.endsWith(".")) out = out.substring(0, out.length() - 1);
            return out;
        } catch (Exception ex) {
            return "錯誤";
        }
    }

    public double evaluate(String expression) {
        input = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("−", "-")
                .replace("π", "pi")
                .replace("√", "sqrt")
                .replace(" ", "")
                .toLowerCase(Locale.US);
        pos = -1;
        nextChar();
        double x = parseExpression();
        if (pos < input.length()) throw new RuntimeException("Unexpected: " + input.charAt(pos));
        return x;
    }

    private int ch;

    private void nextChar() {
        pos++;
        ch = pos < input.length() ? input.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private double parseExpression() {
        double x = parseTerm();
        for (;;) {
            if (eat('+')) x += parseTerm();
            else if (eat('-')) x -= parseTerm();
            else return x;
        }
    }

    private double parseTerm() {
        double x = parsePower();
        for (;;) {
            if (eat('*')) x *= parsePower();
            else if (eat('/')) x /= parsePower();
            else if (eat('%')) x %= parsePower();
            else return x;
        }
    }

    private double parsePower() {
        double x = parseFactor();
        if (eat('^')) x = Math.pow(x, parsePower());
        return x;
    }

    private double parseFactor() {
        if (eat('+')) return parseFactor();
        if (eat('-')) return -parseFactor();

        double x;
        int startPos = this.pos;

        if (eat('(')) {
            x = parseExpression();
            if (!eat(')')) throw new RuntimeException("Missing )");
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(input.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = input.substring(startPos, this.pos);
            if (func.equals("pi")) x = Math.PI;
            else if (func.equals("e")) x = Math.E;
            else {
                x = parseFactor();
                x = applyFunction(func, x);
            }
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        while (true) {
            if (eat('!')) x = factorial(x);
            else if (eat('%')) x = x / 100.0;
            else break;
        }
        return x;
    }

    private double applyFunction(String func, double x) {
        switch (func) {
            case "sqrt": return Math.sqrt(x);
            case "sin": return Math.sin(angle(x));
            case "cos": return Math.cos(angle(x));
            case "tan": return Math.tan(angle(x));
            case "asin": return invAngle(Math.asin(x));
            case "acos": return invAngle(Math.acos(x));
            case "atan": return invAngle(Math.atan(x));
            case "log": return Math.log10(x);
            case "ln": return Math.log(x);
            case "abs": return Math.abs(x);
            case "floor": return Math.floor(x);
            case "ceil": return Math.ceil(x);
            case "round": return Math.rint(x);
            default: throw new RuntimeException("Unknown function: " + func);
        }
    }

    private double angle(double value) {
        return degreeMode ? Math.toRadians(value) : value;
    }

    private double invAngle(double value) {
        return degreeMode ? Math.toDegrees(value) : value;
    }

    private double factorial(double value) {
        if (value < 0 || Math.abs(value - Math.rint(value)) > 0.0000001) throw new RuntimeException("Bad factorial");
        int n = (int) Math.rint(value);
        double result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
}
