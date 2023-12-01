package expressivo;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * An immutable type representing a non-negative number in an expression
 */
public class Value implements Expression {
    private final double num;
    // Abstraction Function:
    //   represents a nonnegative decimal number as an expression
    //   
    // Representation Invariant:
    //   0 <= num <= Double.MAX_VALUE
    //
    // Safety From Exposure
    //   - num is a private and immutable reference
    //   - num references a primitive type, which are immutable
    
    private void checkRep() {
        assert Double.isFinite(num);
        assert 0 <= num && num <= Double.MAX_VALUE;
    }
    public Value(double num) {
        this.num = num;

        checkRep();
    }
    @Override public Expression addExpr(Expression e) {
        if (e.equals(this)) {
            double newNum = this.num * 2;
            checkRep();
            
            if (Double.isInfinite(newNum)) {
                return new Value(Double.MAX_VALUE);
            }
            return new Value(newNum);
        }
        Value zero = new Value(0.0);
        if (this.equals(zero)) {
            return e;
        }
        if (e.equals(zero)) {
            return this;
        }
        checkRep();
        return e.addConstant(num);
    }
    @Override public Expression multiplyExpr(Expression e) {
        Value zero = new Value(0);
        Value one = new Value(1);
        if (this.equals(zero) || e.equals(zero)) {
            return zero;
        }
        if (this.equals(one)) {
            checkRep();
            return e;
        }
        if (e.equals(one)) {
            return this;
        }
        checkRep();
        return e.appendCoefficient(num);
    }
    @Override public Expression addVariable(String variable) {
        assert variable != null && variable != "";
        if (this.equals(new Value(0))) {
            return new Variable(variable);
        }
        checkRep();
        return new Addition(new Variable(variable), this);
    }
    @Override public Expression multiplyVariable(String variable) {
        assert variable != null && variable != "";
        Value zero = new Value(0);
        if (this.equals(zero)) {
            return zero;
        }
        if (this.equals(new Value(1))) {
            return new Variable(variable);
        }

        checkRep();
        return new Multiplication(new Variable(variable), this);
    }
    @Override public Expression addConstant(double num) {
        double numTruncate = Math.floor(num*100000.0)/100000.0;
        double newNum = numTruncate + this.num;
        checkRep();
        
        if (Double.isInfinite(newNum)) {
            return new Value(Double.MAX_VALUE);
        }

        return new Value(newNum);
    }
    @Override public Expression appendCoefficient(double num) {    
        double numTruncate = Math.floor(num*100000.0)/100000.0;    
        double newNum = numTruncate * this.num;
        checkRep();
        
        if (Double.isInfinite(newNum)) {
            return new Value(Double.MAX_VALUE);
        }

        return new Value(newNum);
    }
    @Override public Expression differentiate(String variable) {
        assert variable != null && variable != "";
        return new Value(0);
    }
    @Override public Expression substitute(Map<String, Double> environment) {
        assert environment != null;
        return this;
    }
    @Override public String toString() {
        DecimalFormat myFormatter = new DecimalFormat("###.#####");
        myFormatter.setRoundingMode(RoundingMode.DOWN);
        String output = myFormatter.format(this.num);

        checkRep();
        return output;
    }
    /** Checks if two Values are equal, correct to 5 decimal places */
    @Override public boolean equals(Object thatObject) {
        if (thatObject == this) {
            return true;
        }
        if (!(thatObject instanceof Value)) {
            return false;
        }
        Value thatValue = (Value) thatObject;
        double thatNum = Double.parseDouble(thatValue.toString());
        final double EPSILON = 0.00001;

        checkRep();
        return Math.abs(this.num - thatNum) < EPSILON;
    }
    @Override public int hashCode() {
        double preciseNum = Double.parseDouble(this.toString());
        final int prime = 37;
        int result = 1;
        long numLong = Double.doubleToLongBits(preciseNum);
        int numHash = (int) (numLong ^ (numLong >>> 32));
        
        result = prime*result + numHash;
        
        checkRep();
        return result;
    }
}
