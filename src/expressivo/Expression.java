/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import expressivo.parser.ExpressionLexer;
import expressivo.parser.ExpressionMainVisitor;
import expressivo.parser.ExpressionParser;

import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression { 
    
    // Datatype definition:
    //   Expression = Value(num:double)
    //                + Variable(id:String)
    //                + Addition(left:Expression, right:Expression)
    //                + Multiplication(left:Expression, right:Expression)
    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout.
     * @return expression AST for the input, simplified as much as possible
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) {
        assert input != null && input != "";
        try {
            CharStream inputStream = CharStreams.fromString(input);
            ExpressionLexer lexer = new ExpressionLexer(inputStream);
            lexer.reportErrorsAsExceptions();
            
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExpressionParser parser = new ExpressionParser(tokens);
            parser.reportErrorsAsExceptions();
            
            parser.setBuildParseTree(true);
            ParseTree parseTree = parser.root();
            
            ExpressionMainVisitor exprVisitor = new ExpressionMainVisitor();
            Expression expr = exprVisitor.visit(parseTree);
            
            return expr;
        } catch (ParseCancellationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /** Creates an empty expression such that Expression.parse("0").equals(emptyExpression())  */
    public static Expression emptyExpression() {
        return new Value(0.0);
    }
    /**
     * Appends an expression at the end of this with an addition
     * 
     * If e equals Expression.emptyExpression(), correct to 5 decimal places, 
     * the empty expression is returned;
     * If e equals this, an expression equivalent to
     *      this * 2 is returned
     *      
     * @param e a non-null non-empty string of a valid expression
     *          syntax
     * @return a simplified expression equivalent to:
     *           this + e
     *      this and e are not modified
     */
    public Expression addExpr(Expression e);
    /**
     * Appends an expression at the end of this with a multiplication
     * 
     * If e equals Expression.emptyExpression(), correct to 5 decimal places, 
     * the empty expression is returned;
     * If e equals Expression.parse("1"), correct to 5 decimal places, this
     * expression is returned
     * The product of any other expression except the two above is not simplified,
     * the resulting expression being equivalent to:
     *      (this)*(e)
     * Note: This is not the case when parsing, where an expression is simplified
     * as much as possible
     * 
     * @param e a non-null non-empty string of a valid expression
     *          syntax
     * @return a new expression equivalent to:
     *           this * e
     *      The returned expression is NOT simplified
     *      this and e are not modified
     */
    public Expression multiplyExpr(Expression e);
    /**
     * Appends a variable at the start of this expression with an addition
     * 
     * @param variable non-null non-empty case-sensitive string of letters, a-zA-Z
     * @return a new expression as a result of inserting a variable at the start
     *         of this expression with an addition.
     *         The expression is not simplified
     *                
     */
    public Expression addVariable(String variable);
    /**
     * Appends a variable as a multiplicative factor to start of this expression
     * 
     * @param variable non-null non-empty case-sensitive string of letters, a-zA-Z
     * @return the product expression of this and variable, variable being at
     *         the head of the expression. The expression is not simplified
     */
    public Expression multiplyVariable(String variable);
    /**
     * Adds a number at the start of this expression
     * 
     * @param num nonnegative integer or floating-point number
     * @return the result adding num at the start of this expression.
     *      If e equals Expression.emptyExpression(), correct to 5 decimal places, 
     *      the empty expression is returned;
     *      The expression is not simplified 
     */
    public Expression addConstant(double num);
    /**
     * Appends a number as a multiplicative factor at the start of this expression
     * 
     * @param num nonnegative integer or floating-point number
     * @return the product expression where num is this expression's coefficient,
     *         placed at the start of this expression.
     *      - If e equals Expression.emptyExpression(), correct to 5 decimal places, 
     *        the empty expression is returned;
     *      - If e equals Expression.parse("1"), correct to 5 decimal places, this
     *        expression is returned
     *      The expression is simplified
     */
    public Expression appendCoefficient(double num);
    /**
     * Substitutes a variable in this expression with a number
     * 
     * The set of variables in the environment can contain variables not
     * in this expression:
     *  - Any variables in the expression but not the environment 
     *    remain as variables in the substituted polynomial. 
     *  - Any variables in the environment but not the expression are simply ignored.
     * If the substituted polynomial is a constant expression, with no variables remaining, 
     * then simplification reduces it to a single number, with no operators remaining.
     * 
     * @param environment maps variables to values.  Variables are required to be case-sensitive nonempty 
     *         strings of letters.  The set of variables in environment is allowed to be different than the 
     *         set of variables actually found in expression.  Values must be nonnegative numbers.
     * @return an expression equal to the input, but after substituting every variable v that appears in both
     *         the expression and the environment with its value, environment.get(v).  If there are no
     *         variables left in this expression after substitution, it must be evaluated to a single number.
     */
    public Expression substitute(Map<String,Double> environment);
    /**
     * Produces an expression with the derivative of this expression 
     * with respect to an input variable
     * 
     * @param variable non-null non-empty case-sensitive string of letters, a-zA-Z
     * @return the derivative of this expression with respect
     *         to variable. The returned expression is equal to the derivative,
     *         simplified as much as possible.
     */
    public Expression differentiate(String variable);
    /**
     * Returns a string representation of this expression
     * 
     * The string returned is such that:
     *   - for additions, exactly one space exists between
     *     operand and the operator:
     *          operand + operand 
     *   - for multiplications, no space exists between operands
     *     and the operator, and operands are inside parentheses:
     *          (factor)*(factor)
     *     Factors of products are grouped from left to right by default:
     *          x*x*x -> ((x)*(x))*(x)
     * Numbers in the string are truncated and correct to 5 decimal places
     * 
     * @return a parsable representation of this expression, such that
     *         for all e:Expression, e.equals(Expression.parse(e.toString())).
     */
    @Override public String toString();
    /**
     * Checks if an object is equal to this addition expression
     * Two expressions are equal if and only if: 
     *   - The expressions contain the same variables, numbers, and operators;
     *   - those variables, numbers, and operators are in the same order, read left-to-right;
     *   - and they are grouped in the same way.
     * Two sums are equal if having different groupings with 
     * the same mathematical meaning. For example, 
     *     (3 + 4) + 5 and 3 + (4 + 5) are equal.
     * However, two products are NOT equal if they have different groupings regardless
     * of mathematical meaning. For example:
     *     x*(2*y) is not equal to (x*2)*y 
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS3 handout.
     */
    @Override
    public boolean equals(Object thatObject);  
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     */
    @Override
    public int hashCode();    
}