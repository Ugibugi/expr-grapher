package rad.parser;

/**
 * Reprezentuje wartość wyrażeń w programie
 * typ wartośći jest śledzony przez wewnętrzne pola
 * Taka sama funkcjonalność mogła zostać osiągnięta przez dziedziczenie
 */
public class Value {
    public static int ERRTYPE = -1;
    public static int REAL = 0;
    public static int BOOL = 1;
    public static int VECTOR =2;
    public static int STRING = 3;
    //public static int REF =3;
    public int type;
    public double realval;
    public boolean boolval;
    public String stringval;
    //public Expression refval;
    public Value()
    {
        type = ERRTYPE;
    }
    public Value(double f)
    {
        type = Value.REAL;
        realval = f;
    }
    public Value(boolean b)
    {
        type = Value.BOOL;
        boolval = b;
    }
    public Value(String str)
    {
        type = Value.STRING;
        stringval=str;
    }
    /*public Value(Expression r)
    {
        type = Value.REF;
        refval= r;
    }*/
    public boolean equals(Value o)
    {
        if(type == o.type)
        {
            if(type == Value.REAL) return realval == o.realval;
            else if(type == Value.BOOL) return boolval == o.boolval;
            else return false;
        }
        else {
            return false;
        }

    }
    public Value add(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value(realval + b.realval);
        }
        else if(b.type == Value.BOOL && this.type == Value.BOOL)
        {
            return new Value(boolval || b.boolval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot add types");
        }
    }
    public Value sub(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value(realval - b.realval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot substract types");
        }
    }
    Value mul(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value(realval * b.realval);
        }
        else if(b.type == Value.BOOL && this.type == Value.BOOL)
        {
            return new Value(boolval && b.boolval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot multiply types");
        }
    }
    Value div(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value(realval / b.realval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot divide types");
        }
    }
    Value pow(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value((double)Math.pow(realval,b.realval));
        }
        else if(b.type == Value.BOOL && this.type == Value.BOOL)
        {
            return new Value(boolval ^ b.boolval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot pow types");
        }
    }
    Value gt(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value(realval > b.realval);
        }
        else if(b.type == Value.BOOL && this.type == Value.BOOL)
        {
            return new Value(boolval && !b.boolval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot compare types");
        }
    }
    Value lt(Value b)
    {
        if(b.type == Value.REAL && this.type == Value.REAL)
        {
            return new Value(realval < b.realval);
        }
        else if(b.type == Value.BOOL && this.type == Value.BOOL)
        {
            return new Value(!boolval && b.boolval);
        }
        else
        {
            throw new ProgramError(Error.TYPE_ERROR, "cannot compare types");
        }
    }
    public String toString()
    {
        if(type == Value.REAL)
        {
            return Double.toString(realval);
        }
        else if(type == Value.BOOL)
        {
            if(boolval)return "true";
            else return "false";
        }
        else if(type == Value.STRING)
        {
            return "\""+stringval+"\"";
        }
        else if(type == Value.ERRTYPE)
        {
            return "ERRVAL";
        }
        else
        {
            return "(eval error)";
        }
    }
}
