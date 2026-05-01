import com.jerry0327.supercalc.CalculatorEngine;

public class CalculatorEngineSmokeTest {
    public static void main(String[] args) {
        CalculatorEngine engine = new CalculatorEngine();
        assertEquals("7", engine.calculate("1+2*3"));
        assertEquals("120", engine.calculate("(25+15)*3"));
        assertEquals("120", engine.calculate("5!"));
        assertEquals("1", engine.calculate("10 mod 3"));
        assertEquals("0.5", engine.calculate("50%"));
        assertNear(18.70710678, Double.parseDouble(engine.calculate("sin(45)+log(100)+√256")), 0.000001);
        engine.setDegreeMode(false);
        assertNear(1.0, Double.parseDouble(engine.calculate("sin(pi/2)")), 0.000001);
        System.out.println("CalculatorEngine smoke test passed");
    }

    private static void assertEquals(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertNear(double expected, double actual, double tolerance) {
        if (Math.abs(expected - actual) > tolerance) {
            throw new AssertionError("Expected about " + expected + " but got " + actual);
        }
    }
}
