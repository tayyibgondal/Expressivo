package expressivo;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

// TODO: change tests to be representation independent
public class ExpressionVariantsTests {
    // Testing Strategy for Expression
    // Partitions for Expression.parse: String -> Expression:
    // For operators + and *, the input string is to contain:
    // one operator,
    // multiple operators of the same type,
    // multiple operators of different types.
    // include inputs with grouping
    //
    // For all variants of Expression: Value, Variable, Addition, Multiplication
    // Partitions for addExpr: Expression x Expression -> Expression
    // - add a variant to itself
    // - Value: 0, > 0
    // - Include floating point numbers and precision tests for Value
    // - For Addition and Multiplication variants, include simplification
    // tests to check if the expression returned is in simple form
    //
    // Partitions for multiplyExpr: Expression x Expression -> Expression
    // - multiply a variant by itself
    // - Value: 0, 0 < Value < 1, 1, > 1
    // - Include floating point numbers and precision tests for Value
    // - For Addition and Multiplication variants, include simplification
    // tests to check if the expression returned is in simple form
    //
    // Partitions for addVariable: Expression x String -> Expression
    // - Value: 0, > 0
    //
    // Partitions for multiplyVariable: Expression x String -> Expression
    // - Value: 0, 0 < Value < 1, 1, > 1
    //
    // Partitions for addConstant: Expression x String -> Expression
    // - Value: 0, 0 < Value < 1, 1, > 1
    //
    // Partitions for appendCoefficient: Expression x String -> Expression
    // - Value: 0, 0 < Value < 1, 1, > 1
    //
    // Partitions for toString: Expression -> String
    // - Value: test numbers correct to 5 decimal places
    //
    // Partitions for equals: Expression x Expression -> boolean
    // - symmetric equality
    // - reflexive equality
    // - transitive equality
    // Include tests for numbers correct to 5 decimal places
    //
    // Partitions for hashCode: Expression -> int
    // - include equal expressions having equal values equal correct
    // to 5 decimal places
    //
    // Partitions for differentiate: Expression x String -> Expression
    // - input string exists in expression; doesn't exist
    // - Expressions contain multiple variables
    //
    // Partitions for substitute: Expression x String -> Expression
    // - variables in the expression but not in the input string
    // - variables in the input string but not in the expression
    // - one variable in both the expression and the input string
    // - multiple variables in both
    //
    // Full Cartesian Coverage of partitions

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // Tests for Expression.parse()
    @Test
    // covers single addition expression
    public void testParse_SingleAddExpr() {
        String input = "2 + x";
        Expression parsed = Expression.parse(input);
        Expression expected = new Addition(new Value(2), new Variable("x"));
        Expression wrongOrder = new Addition(new Variable("x"), new Value(2));

        assertNotNull("Expected non-null expression", parsed);
        assertNotEquals("Expected correct order of elements",
                wrongOrder, parsed);
        assertEquals("Expected correct parse",
                expected, parsed);
    }

    @Test
    // covers multiple additions
    public void testParse_MultipleAddExprs() {
        String input = "(x + x + y)";
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Expression parsed = Expression.parse(input);
        Expression expected = new Addition(
                new Addition(x, x), y);

        assertNotNull("Expected non-null expression", parsed);
        assertEquals("Expected correct parse",
                expected, parsed);
    }

    @Test
    // covers single multiplication expression
    public void testParse_SingleMult() {
        String input = "x*y";
        Expression parsed = Expression.parse(input);
        Expression expected = new Multiplication(new Variable("x"), new Variable("y"));
        Expression wrongOrder = new Multiplication(new Variable("y"), new Variable("x"));

        assertNotNull("Expected non-null expression", parsed);
        assertNotEquals("Expected correct order of elements",
                wrongOrder, parsed);
        assertEquals("Expected correct parse",
                expected, parsed);
    }

    @Test
    // covers multiple multiplication expressions
    public void testParse_MultipleMults() {
        String input = "x*2*x";
        Expression parsed = Expression.parse(input);
        Variable x = new Variable("x");
        Value num = new Value(2);
        Expression expected = new Multiplication(
                new Multiplication(x, num), x);

        assertNotNull("Expected non-null expression", parsed);
        assertEquals("Expected correct parse",
                expected, parsed);
    }

    @Test
    // covers addition, multiplication and grouping
    public void testParse_AddExprMult() {
        String input = "(x + (2.12*x))*y";
        Expression parsed = Expression.parse(input);
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Value num = new Value(2.12);
        Multiplication group1 = new Multiplication(num, x);
        Addition group2 = new Addition(x, group1);
        Expression expected = new Multiplication(group2, y);

        assertNotNull("Expected non-null expression", parsed);
        assertEquals("Expected correct parse",
                expected, parsed);
    }

    // Tests for variant.addExpr(variant)
    @Test
    // covers value
    public void testAddExpr_Value() {
        Value val1 = new Value(0);
        Value val2 = new Value(3.142);
        Value val3 = new Value(6.284);
        Value val4 = new Value(0.000001);
        Value val5 = new Value(0.000009);
        Value val6 = new Value(1);
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Expression sum = new Addition(x, val2);
        Expression mult = new Multiplication(x, y);

        Expression addExpr1 = val2.addExpr(val2); // 3.142 + 3.142
        Expression addExpr2 = val2.addExpr(sum); // 3.142 + x + 3.142
        Expression addExpr3 = val2.addExpr(mult); // 3.142 + (x * y)
        Expression addExpr4 = val2.addExpr(x);

        assertEquals("Expected equal values correct to 5 decimal points",
                val4, val5.addExpr(val4));
        assertEquals("Expected expression + 0 = expression",
                val6, val1.addExpr(val6));
        assertEquals("Expected expression + 0 = expression",
                val6, val6.addExpr(val1));
        assertEquals("Expected expression + 0 = expression",
                val2, val5.addExpr(val2));
        assertEquals("Expected expression + 0 = expression",
                x, val5.addExpr(x));
        assertEquals("Expected expression + 0 = expression",
                sum, val4.addExpr(sum));
        assertEquals("Expected expression + 0 = expression",
                mult, val1.addExpr(mult));
        assertEquals("Expected simplified expression",
                val3, addExpr1);
        assertEquals("Expected addition expression",
                new Addition(val2, x), addExpr4);
        assertEquals("Expected simplified expression",
                new Addition(x, val3), addExpr2);
        assertEquals("Expected addition expression",
                new Addition(val2, mult), addExpr3);

    }

    @Test
    // covers variable
    public void testAddExpr_Variable() {
        Variable var = new Variable("foo");
        Value val1 = new Value(0);
        Value val2 = new Value(1);
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Expression sum = new Addition(x, val2);
        Expression mult = new Multiplication(x, y);

        Expression addExpr = var.addExpr(val1); // foo + 0
        Expression addExpr1 = var.addExpr(val2); // foo + 1
        Expression addExpr2 = var.addExpr(sum); // foo + x + y
        Expression addExpr3 = var.addExpr(mult); // foo + x*y

        assertEquals("Expected expression + 0 = expression",
                var, addExpr);
        assertEquals("Expected an addition expression",
                new Addition(var, val2), addExpr1);
        assertEquals("Expected addition at the end of var",
                new Addition(var, sum), addExpr2);
        assertEquals("Expected addition at the end of var",
                new Addition(var, mult), addExpr3);
        assertEquals("Expected no simplification",
                new Addition(var, var), var.addExpr(var));
    }

    @Test
    // covers Addition
    public void testAddExpr_Addition() {
        Value val1 = new Value(0);
        Value val2 = new Value(5.10);
        Value val3 = new Value(2);
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Expression sum1 = new Addition(x, y);
        Expression sum2 = new Addition(new Variable("z"), val2);// z + 5.10
        Expression mult = new Multiplication(x, y);
        Expression mult1 = new Multiplication(x, val3);
        Expression mult2 = new Multiplication(y, val3);

        Expression addExpr = sum1.addExpr(val1); // x + y + 0
        Expression addExpr1 = sum1.addExpr(val2); // x + y + 5.10
        Expression addExpr2 = sum1.addExpr(x); // x + y + x
        Expression addExpr3 = sum1.addExpr(sum1); // x + y + x + y
        Expression addExpr4 = addExpr1.addExpr(sum2); // x + y + 5.10 + z + 5.10
        Expression addExpr5 = sum1.addExpr(mult); // x + y + x*y

        assertEquals("Expected expression + 0 = expression",
                sum1, addExpr);
        assertEquals("Expected addition expression",
                new Addition(mult1, y), addExpr2);
        assertEquals("Expected addition expression",
                new Addition(mult1, mult2), addExpr3); // x*2 + y*2
        assertEquals("Expected addition expression",
                new Addition(addExpr1, sum2), addExpr4);
        assertEquals("Expected addition expression",
                new Addition(sum1, mult), addExpr5);
    }

    @Test
    // covers multiplication
    public void testAddExpr_Multiplication() {
        Value val1 = new Value(0);
        Value val2 = new Value(2.2);
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Expression sum = new Addition(x, y);
        Expression mult1 = new Multiplication(x, val2);
        Expression mult2 = new Multiplication(x, y);

        Expression addExpr1 = mult1.addExpr(val1); // x*2.2 + 0
        Expression addExpr2 = mult1.addExpr(val2); // x*2.2 + 2.2
        Expression addExpr3 = mult1.addExpr(x); // x*2.2 + x
        Expression addExpr4 = mult1.addExpr(sum); // x*2.2 + x + y
        Expression addExpr5 = mult1.addExpr(mult1); // x*2.2 + x*2.2
        Expression addExpr6 = mult2.addExpr(mult1); // x*y + x*2.2

        assertEquals("Expected expression + 0 = expression",
                mult1, addExpr1);
        assertEquals("Expected addition expression",
                new Addition(mult1, val2), addExpr2);
        assertEquals("Expected addition expression",
                new Addition(mult1, x), addExpr3);
        assertEquals("Expected addition expression",
                new Addition(mult1, sum), addExpr4);
        assertEquals("Expected simplified expression",
                new Multiplication(mult1, new Value(2)), addExpr5);
        assertEquals("Expected addition expression",
                new Addition(mult2, mult1), addExpr6);
    }

    // Tests for variant.multiply(variant)
    @Test
    // covers value
    public void testMultiplyExpr_Value() {
        Value val1 = new Value(0);
        Value val2 = new Value(1);
        Value val3 = new Value(3.5);
        Variable foo = new Variable("foo");
        Expression sum = new Addition(foo, val2);
        Expression mult = new Multiplication(val3, foo);

        Expression multExpr1 = val3.multiplyExpr(foo); // 3.5 * foo
        Expression multExpr2 = val3.multiplyExpr(sum); // 3.5 * (foo + 1)
        Expression multExpr3 = val3.multiplyExpr(mult); // 3.5 * 3.5 * foo
        Expression multExpr4 = val3.multiplyExpr(val3); // 3.5 * 3.5

        assertEquals("Expected 0 * expression = 0",
                val1, val1.multiplyExpr(val2));
        assertEquals("Expected 0 * expression = 0",
                val1, val1.multiplyExpr(val3));
        assertEquals("Expected 0 * expression = 0",
                val1, val1.multiplyExpr(foo));
        assertEquals("Expected 0 * expression = 0",
                val1, val1.multiplyExpr(sum));
        assertEquals("Expected 0 * expression = 0",
                val1, val1.multiplyExpr(mult));
        assertEquals("Expected 1 * expression = expression",
                val2, val2.multiplyExpr(val2));
        assertEquals("Expected 1 * expression = expression",
                val3, val2.multiplyExpr(val3));
        assertEquals("Expected 1 * expression = expression",
                foo, val2.multiplyExpr(foo));
        assertEquals("Expected 1 * expression = expression",
                sum, val2.multiplyExpr(sum));
        assertEquals("Expected 1 * expression = expression",
                mult, val2.multiplyExpr(mult));
        assertEquals("Expected multiplication expression",
                new Multiplication(val3, foo), multExpr1);
        assertEquals("Expected multiplication expression",
                new Addition(multExpr1, val3), multExpr2);
        assertEquals("Expected multiplication expression",
                new Multiplication(val3, mult), multExpr3);
        assertEquals("Expected multiplication expression",
                new Value(12.25), multExpr4);
    }

    // Tests for variant.addVariable()
    @Test
    // covers value
    public void testAddVar_Value() {
        String x = "x";
        Variable varInput = new Variable(x);
        Value valueExpr1 = new Value(0);
        Value valueExpr2 = new Value(1);
        Expression addVar1 = valueExpr1.addVariable(x);
        Expression addVar2 = valueExpr2.addVariable(x);
        Expression expected1 = varInput;
        Expression expected2 = new Addition(varInput, valueExpr2);

        assertNotNull("Expected non-null expresssion", addVar1);
        assertNotNull("Expected non-null expresssion", addVar2);
        assertEquals("Expected sum of a variable and 0 to be the variable",
                expected1, addVar1);
        assertEquals("Expected result 0f 1 + x",
                expected2, addVar2);
    }

    @Test
    // covers variable
    public void testAddVar_Variable() {
        Variable x = new Variable("x");
        Expression addVar1 = x.addVariable("x");
        Expression addVar2 = x.addVariable("foo");
        Expression expected1 = new Addition(x, x);
        Expression expected2 = new Addition(new Variable("foo"), x);

        assertNotNull("Expected non-null expresssion", addVar1);
        assertNotNull("Expected non-null expresssion", addVar2);
        assertEquals("Expected expression not simplified",
                expected1, addVar1);
        assertEquals("Expected result of x + 2",
                expected2, addVar2);
    }

    @Test
    // covers addition
    public void testAddVar_Addition() {
        String foo = "foo";
        Expression addExpr = new Addition(new Value(2), new Variable("y"));
        Expression addVar = addExpr.addVariable(foo);
        Expression expected = new Addition(new Variable(foo), addExpr);

        assertNotNull("Expected non-null expresssion", addVar);
        assertEquals("Expected an addition expression",
                expected, addVar);
    }

    @Test
    // covers multiplication
    public void testAddVar_Mult() {
        String foo = "foo";
        Expression multExpr = new Multiplication(new Value(1.2), new Variable("y"));
        Expression addVar = multExpr.addVariable(foo);
        Expression expected = new Addition(new Variable(foo), multExpr);

        assertNotNull("Expected non-null expresssion", addVar);
        assertEquals("Expected an addition expression",
                expected, addVar);
    }

    // Tests for variant.multiplyVariable()
    @Test
    // covers value
    public void testMultiplyVar_Value() {
        String input = "x";
        Value valueExpr1 = new Value(0);
        Value valueExpr2 = new Value(1);
        Value valueExpr3 = new Value(3.142);
        Expression multVar1 = valueExpr1.multiplyVariable(input);
        Expression multVar2 = valueExpr2.multiplyVariable(input);
        Expression multVar3 = valueExpr3.multiplyVariable(input);
        Expression expected1 = valueExpr1;
        Expression expected2 = new Variable("x");
        Expression expected3 = new Multiplication(new Variable(input), valueExpr3);

        assertNotNull("Expected non-null", multVar1);
        assertNotNull("Expected non-null", multVar2);
        assertNotNull("Expected non-null", multVar3);
        assertEquals("Expected product of 0 to be 0",
                expected1, multVar1);
        assertEquals("Expected product of 1 to be the variable",
                expected2, multVar2);
        assertEquals("Expected result of 3.142*x",
                expected3, multVar3);
    }

    @Test
    // covers variable
    public void testMultiplyVar_Variable() {
        String input = "width";
        Variable varExpr = new Variable("length");
        Expression multVar = varExpr.multiplyVariable(input);
        Expression expected = new Multiplication(new Variable(input), varExpr);

        assertNotNull("Expected non-null expresssion", multVar);
        assertEquals("Expected a multiplication expression",
                expected, multVar);
    }

    @Test
    // covers addition
    public void testMultiplyVar_Addition() {
        String input = "y";
        Expression addExpr = new Addition(new Variable("x"), new Value(3));
        Expression multVar = addExpr.multiplyVariable(input);
        Expression expected = new Multiplication(new Variable(input), addExpr);

        assertNotNull("Expected non-null expresssion", multVar);
        assertEquals("Expected a multiplication expression",
                expected, multVar);
    }

    @Test
    // covers multiplication
    public void testMultiplyVar_Mult() {
        String input = "height";
        Expression multExpr = new Multiplication(new Variable("length"), new Variable("width"));
        Expression multVar = multExpr.multiplyVariable(input);
        Expression expected = new Multiplication(new Variable(input), multExpr);

        assertNotNull("Expected non-null expresssion", multVar);
        assertEquals("Expected a multiplication expression",
                expected, multVar);
    }

    // Tests for variant.addConstant(num)
    @Test
    // covers value
    public void testAddConstant_Value() {
        double input = 11.12;
        Value zero = new Value(0);
        Value eleven = new Value(input);

        Expression result1 = zero.addConstant(input);
        Expression result2 = eleven.addConstant(input);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertEquals("Expected result of 0 + 11.12",
                eleven, result1);
        assertEquals("Expected result of 11.12 + 11.12",
                new Value(input + input), result2);
    }

    @Test
    // covers variable
    public void testAddConstant_Variable() {
        Variable x = new Variable("x");

        Expression result1 = x.addConstant(0);
        Expression result2 = x.addConstant(11);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertEquals("Expected result of x + 0 to be x",
                x, result1);
        assertEquals("Expected result of x + 11",
                new Addition(new Value(11), x), result2);
    }

    @Test
    // covers addition
    public void testAddConstant_Addition() {
        Addition addExpr = new Addition(new Variable("x"), new Variable("y"));

        Expression result1 = addExpr.addConstant(0);
        Expression result2 = addExpr.addConstant(11);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertEquals("Expected addition + 0 to be the addition",
                addExpr, result1);
        assertEquals("Expected addition + 11",
                new Addition(new Value(11), addExpr), result2);
    }

    @Test
    // covers multiplication
    public void testAddConstant_Mult() {
        Multiplication multExpr = new Multiplication(new Variable("x"), new Variable("y"));

        Expression result1 = multExpr.addConstant(0);
        Expression result2 = multExpr.addConstant(11);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertEquals("Expected mult + 0 to be mult",
                multExpr, result1);
        assertEquals("Expected mult + 11",
                new Addition(new Value(11), multExpr), result2);
    }

    // Tests for variant.appendCoefficient(num)
    @Test
    // covers value
    public void testAppendCoefficient_Value() {
        double input = 11;
        Value zero = new Value(0);
        Value one = new Value(1);
        Value eleven = new Value(input);

        Expression result1 = zero.appendCoefficient(input);
        Expression result2 = one.appendCoefficient(input);
        Expression result3 = eleven.appendCoefficient(input);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertNotNull("Expected non-null expression", result3);
        assertEquals("Expected result of 0*11",
                zero, result1);
        assertEquals("Expected result of 1*11",
                eleven, result2);
        assertEquals("Expected result of 11*11",
                new Value(121), result3);
    }

    @Test
    // covers variable
    public void testAppendCoefficient_Variable() {
        Variable x = new Variable("x");

        Expression result1 = x.appendCoefficient(0);
        Expression result2 = x.appendCoefficient(1);
        Expression result3 = x.appendCoefficient(11);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertNotNull("Expected non-null expression", result3);
        assertEquals("Expected result of x*0 to be zero",
                new Value(0), result1);
        assertEquals("Expected result of x*1 to be x",
                x, result2);
        assertEquals("Expected result of x*11",
                new Multiplication(new Value(11), x), result3);
    }

    @Test
    // covers addition
    public void testAppendCoefficient_Addition() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Value eleven = new Value(11);
        Addition addExpr = new Addition(x, y);

        Expression result1 = addExpr.appendCoefficient(0);
        Expression result2 = addExpr.appendCoefficient(1);
        Expression result3 = addExpr.appendCoefficient(11);
        Expression expanded = new Addition(
                new Multiplication(eleven, x),
                new Multiplication(eleven, y));

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertNotNull("Expected non-null expression", result3);
        assertEquals("Expected addition*0 to be zero",
                new Value(0), result1);
        assertEquals("Expected addition*1 to be the addition",
                addExpr, result2);
        assertEquals("Expected expanded expression",
                expanded, result3);
    }

    @Test
    // covers multiplication
    public void testAppendCoefficient_Mult() {
        Multiplication multExpr = new Multiplication(new Variable("x"), new Variable("y"));

        Expression result1 = multExpr.appendCoefficient(0);
        Expression result2 = multExpr.appendCoefficient(1);
        Expression result3 = multExpr.appendCoefficient(11);

        assertNotNull("Expected non-null expression", result1);
        assertNotNull("Expected non-null expression", result2);
        assertNotNull("Expected non-null expression", result3);
        assertEquals("Expected mult*0 to be zero",
                new Value(0), result1);
        assertEquals("Expected mult*1 to be the addition",
                multExpr, result2);
        assertEquals("Expected result of addition*11",
                new Multiplication(new Value(11), multExpr), result3);
    }

    // Tests for variant.toString()
    @Test
    // covers value
    public void testToString_Value() {
        Value val1 = new Value(0.000001);
        Value val2 = new Value(0.000009);
        Value val3 = new Value(0);
        Value val4 = new Value(2.0);
        Value val5 = new Value(0.00001);

        assertEquals("Expected correct string rep",
                "2", val4.toString());
        assertEquals("Expected string correct to 5 decimal places",
                val3.toString(), val1.toString());
        assertEquals("Expected string correct to 5 decimal places",
                val3.toString(), val2.toString());
        assertNotEquals("Expected string correct to 5 decimal places",
                val2.toString(), val5.toString());
        assertEquals("Expected parsable string rep",
                val3, Expression.parse(val2.toString()));
        assertEquals("Expected parsable string rep",
                val1, Expression.parse(val2.toString()));
    }

    @Test
    // covers variable
    public void testToString_Variable() {
        Variable varExpr = new Variable("Foo");

        assertEquals("Expected correct string rep",
                "Foo", varExpr.toString());
        assertEquals("Expected parsable string rep",
                varExpr, Expression.parse(varExpr.toString()));
    }

    @Test
    // covers addition
    public void testToString_Addition() {
        Addition sumExpr = new Addition(new Variable("x"), new Value(2.000009));
        Multiplication multExpr = new Multiplication(new Variable("x"), new Variable("y"));
        Expression addition = sumExpr.addExpr(multExpr);

        String sumString = addition.toString();
        String expected = "x + 2 + (x)*(y)";

        assertEquals("Expected correct string rep",
                expected, sumString);
        assertEquals("Expected parsable string rep",
                addition, Expression.parse(addition.toString()));
    }

    @Test
    // covers multiplication
    public void testToString_Mult() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Addition leftSumExpr = new Addition(x, new Value(2.000009));
        Addition rightSumExpr = new Addition(x, y);
        Expression multExpr1 = new Multiplication(leftSumExpr, rightSumExpr);
        Expression multExpr2 = new Multiplication(x, y);
        Expression multExpr3 = new Multiplication(leftSumExpr, multExpr2);

        String multString1 = multExpr1.toString();
        String multString2 = multExpr2.toString();
        String multString3 = multExpr3.toString();
        String expected1 = "(x + 2)*(x + y)";
        String expected2 = "(x)*(y)";
        String expected3 = "(x + 2)*((x)*(y))";

        assertEquals("Expected correct string rep",
                expected1, multString1);
        assertEquals("Expected correct string rep",
                expected2, multString2);
        assertEquals("Expected correct string rep",
                expected3, multString3);
        assertEquals("Expected parsable string rep",
                multExpr1, Expression.parse(multExpr1.toString()));
        assertEquals("Expected parsable string rep",
                multExpr2, Expression.parse(multExpr2.toString()));
        assertEquals("Expected parsable string rep",
                multExpr3, Expression.parse(multExpr3.toString()));
    }

    // Tests for variant.equals(variant)
    @Test
    // covers value
    public void testEquals_Value() {
        Value value1 = new Value(1);
        Value value2 = new Value(1.000001);
        Value value3 = new Value(1.000009);
        Value value4 = new Value(1.00002);

        assertFalse("Expected equality in precision",
                value4.equals(value1));
        assertTrue("Expected reflexive equality",
                value1.equals(value1));
        assertTrue("Expected reflexive equality",
                value2.equals(value2));
        assertTrue("Expected symmetric equality",
                value1.equals(value2) && value2.equals(value1));
        assertTrue("Expected symmetric equality",
                value1.equals(value3) && value3.equals(value1));
        if (value1.equals(value3) && value1.equals(value2)) {
            assertTrue("Expected transitive equality",
                    value3.equals(value2));
        }
    }

    @Test
    // covers variable
    public void testEquals_Variable() {
        Variable var1 = new Variable("Foo");
        Variable var2 = new Variable("Foo");
        Variable var3 = new Variable("Foo");
        Variable var4 = new Variable("foo");

        assertFalse("Expected variable to be case-sensitive",
                var4.equals(var1));
        assertTrue("Expected reflexive equality",
                var1.equals(var1));
        assertTrue("Expected reflexive equality",
                var2.equals(var2));
        assertTrue("Expected symmetric equality",
                var1.equals(var2) && var2.equals(var1));
        assertTrue("Expected symmetric equality",
                var1.equals(var3) && var3.equals(var1));
        if (var1.equals(var3) && var1.equals(var2)) {
            assertTrue("Expected transitive equality",
                    var3.equals(var2));
        }
    }

    @Test
    // covers Addition
    public void testEquals_Addition() {
        // x + x*y == x + x*y
        // x + x*y != x + y*x
        // x + x*y != x*y + x
        // 1 + 2.0 != 2 + 1
        // x + x*y != (x + x)*y
        // (x + y) + x == x + (y + x)
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Multiplication mult1 = new Multiplication(x, y);// x*y
        Multiplication mult2 = new Multiplication(y, x);// y*x
        Multiplication mult3 = new Multiplication(new Addition(x, x), y);// (x + x)*y
        Addition add1 = new Addition(x, mult1);// x + x*y
        Addition add2 = new Addition(x, mult1);// x + x*y
        Addition add3 = new Addition(x, mult2);// x + y*x
        Addition add4 = new Addition(mult1, x);// x*y + x
        Addition add5 = new Addition(new Value(1), new Value(2.0));// 1 + 2.0
        Addition add6 = new Addition(new Value(2), new Value(1));// 2 + 1
        Addition add7 = new Addition(
                new Addition(x, y), x);// (x + y) + x
        Addition add8 = new Addition(
                x, new Addition(y, x));// x + (y + x)

        assertTrue("Expected reflexive equality",
                add1.equals(add1));
        assertTrue("Expected symmetric equality",
                add1.equals(add2) && add2.equals(add1));
        assertFalse("Expected equal sums to have same order of variables",
                add1.equals(add3));
        assertFalse("Expected equal sums to have same order of operators",
                add1.equals(add4));
        assertFalse("Expected equal sums to have same order of numbers",
                add5.equals(add6));
        assertFalse("Expected equal sums to have same grouping",
                add1.equals(mult3));
        assertTrue("Expected sums with same meaning to be equal",
                add7.equals(add8));
    }

    @Test
    // covers multiplication
    public void testEquals_Multiplication() {
        // x*(x + y) == x*(x + y)
        // x*(x + y) != x*(y + x)
        // x*(x + y) != (x + y)*x
        // 1*2.0 != 2*1
        // x*(x + y) != x*x + y
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Addition add1 = new Addition(x, y);// x + y
        Addition add2 = new Addition(y, x);// y + x
        Addition add3 = new Addition(new Multiplication(x, x), y);// x*x + y
        Multiplication mult1 = new Multiplication(x, add1);// x*(x + y)
        Multiplication mult2 = new Multiplication(x, add1);// x*(x + y)
        Multiplication mult3 = new Multiplication(x, add2);// x*(y + x)
        Multiplication mult4 = new Multiplication(add1, x);// (x + y)*x
        Multiplication mult5 = new Multiplication(new Value(1), new Value(2.0));// 1*2.0
        Multiplication mult6 = new Multiplication(new Value(2), new Value(1));// 2*1

        assertTrue("Expected reflexive equality",
                mult1.equals(mult1));
        assertTrue("Expected symmetric equality",
                mult1.equals(mult2) && mult2.equals(mult1));
        assertFalse("Expected equal products to have same order of variables",
                mult1.equals(mult3));
        assertFalse("Expected equal products to have same order of operators",
                mult1.equals(mult4));
        assertFalse("Expected equal products to have same order of numbers",
                mult5.equals(mult6));
        assertFalse("Expected equal products to have same grouping",
                mult1.equals(add3));
    }

    // Tests for variant.hashCode()
    @Test
    // covers value
    public void testHashCode_Value() {
        Value value1 = new Value(123);
        Value value2 = new Value(123.000009);

        assertTrue("Expected values to be equal",
                value1.equals(value2));
        assertEquals("Expected equal values to have equal hashcodes",
                value1.hashCode(), value2.hashCode());
    }

    @Test
    // covers variable
    public void testHashCode_Variable() {
        Variable var1 = new Variable("Foobar");
        Variable var2 = new Variable("Foobar");

        assertTrue("Expected variables to be equal",
                var1.equals(var2));
        assertEquals("Expected equal variables to have equal hashcodes",
                var1.hashCode(), var2.hashCode());
    }

    @Test
    // covers addition
    public void testHashCode_Addition() {
        Variable x = new Variable("x");
        Value val = new Value(2);
        Multiplication mult = new Multiplication(x, val);// x*2
        Addition add1 = new Addition(x, mult);// x + x*2
        Addition add2 = new Addition(x, mult);// x + x*2

        assertTrue("Expected sums to be equal",
                add1.equals(add2));
        assertEquals("Expected equal sums to have equal hashcodes",
                add1.hashCode(), add2.hashCode());
    }

    @Test
    // covers multiplication
    public void testHashCode_Multiplication() {
        Variable x = new Variable("x");
        Value val = new Value(2);
        Addition add = new Addition(x, val);// x + 2
        Multiplication mult1 = new Multiplication(x, add);// x*(x + 2)
        Multiplication mult2 = new Multiplication(x, add);// x*(x + 2)

        assertTrue("Expected products to be equal",
                mult1.equals(mult2));
        assertEquals("Expected equal products to have equal hashcodes",
                mult1.hashCode(), mult2.hashCode());
    }

    // Tests for variant.differentiate(inputVariable)
    @Test
    // covers value
    public void testDifferentiate_Value() {
        Value valueExpr = new Value(2.1);
        Expression deriv = valueExpr.differentiate("x");
        Expression expected = new Value(0);

        assertNotNull("Expected non-null expression", deriv);
        assertEquals("Expected derivative of a constant to be 0",
                expected, deriv);
    }

    @Test
    // covers variable
    public void testDifferentiate_Variable() {
        Variable valueExpr = new Variable("foo");
        Expression deriv1 = valueExpr.differentiate("foo");
        Expression deriv2 = valueExpr.differentiate("bar");
        Expression expected1 = new Value(1);
        Expression expected2 = new Value(0);

        assertNotNull("Expected non-null expression", deriv1);
        assertNotNull("Expected non-null expression", deriv2);
        assertEquals("Expected derivative with repect to itself to be 1",
                expected1, deriv1);
        assertEquals("Expected derivative with repect to another variable to be 0",
                expected2, deriv2);
    }

    @Test
    // covers addition
    public void testDifferentiate_Addition() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Multiplication mult = new Multiplication(x, y);

        Addition addExpr = new Addition(
                new Addition(x, new Value(3.142)), y);
        Addition addExpr1 = new Addition(
                new Addition(mult, x), x);

        Expression deriv = addExpr.differentiate("y");
        Expression deriv1 = addExpr1.differentiate("x");
        Expression expected = new Value(1);
        Expression expected1 = new Addition(y, new Value(2));

        assertNotNull("Expected non-null expression", deriv);
        assertEquals("Expected derivative of an addition",
                expected, deriv);
        assertEquals("Expected simplified derivative of an addition",
                expected1, deriv1);
    }

    @Test
    // covers multiplication
    public void testDifferentiate_Multiplication() {
        Variable x = new Variable("x");
        Multiplication multExpr = new Multiplication(
                new Multiplication(x, x),
                x);
        Expression deriv = multExpr.differentiate("x");
        Expression expected = Expression.parse("x*(x*1 + x*1) + x*x*1");

        assertNotNull("Expected non-null expression", expected);
        assertEquals("Expected derivative of a multiplication",
                expected, deriv);
    }

    // Tests for variant.substitute()
    @Test
    // covers value
    public void testSubstitute_Value() {
        Value one = new Value(1.1);
        Map<String, Double> env = new HashMap<>();
        env.put("PI", 3.142);
        env.put("radius", 12.0);

        assertEquals("Expected no change in value",
                one, one.substitute(env));
    }

    @Test
    // covers variable
    public void testSubstitute_Variable() {
        Variable x = new Variable("x");
        Map<String, Double> env1 = new HashMap<>();
        Map<String, Double> env2 = new HashMap<>();
        Map<String, Double> env3 = new HashMap<>();
        env1.put("x", 2.2);
        env2.putAll(env1);
        env2.put("z", 2.2);
        env2.put("y", 1.0);
        env3.put("foo", 1.2);
        env3.put("bar", 420.0);

        Expression expected = new Value(2.2);
        Expression actual = x.substitute(env1);
        Expression actual2 = x.substitute(env2);
        Expression actual3 = x.substitute(env3);

        assertEquals("Expected value substituted", expected, actual);
        assertEquals("Expected value substituted", expected, actual2);
        assertEquals("Expected not value substituted", x, actual3);
    }

    @Test
    // covers addition
    public void testSubstitute_Addition() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Variable foo = new Variable("foo");

        Expression sum1 = new Addition(x, y);
        Expression sum2 = new Addition(sum1, foo);

        Map<String, Double> env1 = new HashMap<>();
        Map<String, Double> env2 = new HashMap<>();
        Map<String, Double> env3 = new HashMap<>();
        env1.put("PI", 3.142);
        env1.put("radius", 12.0);
        env2.put("length", 2.2);
        env2.put("width", 1.0);
        env2.put("x", 14.0);
        env3.putAll(env1);
        env3.put("x", 0.0);
        env3.put("y", 1.2);

        Expression actual1 = sum1.substitute(env1);
        Expression actual2 = sum1.substitute(env2);
        Expression actual3 = sum2.substitute(env3);
        Expression expected1 = sum1;
        Expression expected2 = new Addition(new Value(14), y);
        Expression expected3 = new Addition(new Value(1.2), foo);

        assertEquals("Expected no change", expected1, actual1);
        assertEquals("Expected x substituted", expected2, actual2);
        assertEquals("Expected simplified expression", expected3, actual3);
    }

    @Test
    // covers multiplication
    public void testSubstitute_Mult() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Variable foo = new Variable("foo");

        Multiplication mult1 = new Multiplication(x, y);
        Multiplication mult2 = new Multiplication(foo, mult1);
        Multiplication mult3 = new Multiplication(mult1, mult1);

        Map<String, Double> env1 = new HashMap<>();
        Map<String, Double> env2 = new HashMap<>();
        Map<String, Double> env3 = new HashMap<>();
        Map<String, Double> env4 = new HashMap<>();
        env1.put("PI", 3.142);
        env1.put("radius", 12.0);
        env2.put("length", 2.2);
        env2.put("width", 1.0);
        env2.put("x", 1.000009);
        env3.putAll(env1);
        env3.put("x", 0.0);
        env4.put("x", 2.0);
        env4.put("y", 2.0);

        Expression actual1 = mult1.substitute(env1);
        Expression actual2 = mult1.substitute(env2);
        Expression actual3 = mult2.substitute(env3);
        Expression actual4 = mult3.substitute(env4);
        Expression expected1 = mult1;
        Expression expected2 = y;
        Expression expected3 = new Value(0);
        Expression expected4 = new Value(16);

        assertEquals("Expected no change", expected1, actual1);
        assertEquals("Expected simplified expression, product of 1",
                expected2, actual2);
        assertEquals("Expected simplified expression, product of 0",
                expected3, actual3);
        assertEquals("Expected simplified expression, other variables ignored",
                expected4, actual4);
    }
}