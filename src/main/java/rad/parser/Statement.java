package rad.parser;

/**
 * Reprezentuje jedno zdanie programowe
 * Może to być jedna z trzech rzeczy - wyrażenie, przypisanie, relacja
 *
 */
public class Statement {
    public boolean isExpression;
    public boolean isAssignment;
    public boolean isRelation;

    public Expression lhs,rhs;
    String rel;

    /**
     * Konstruuje zdanie z pojedyńczego wyrażenia.
     * @param e - wyrażenie z którego jest konstruowane zdanie
     */
    Statement(Expression e)
    {
        isAssignment = false;
        isExpression = true;
        isRelation = false;
        lhs = e;
    }

    /**
     * Konstruuje zdanie relacji lub przypisania
     * @param e1 - lewa strona relacji / wyrażenie do którego przypisujemy
     * @param e2 - prawa strona relacji / wyrażenie przypisywane
     * @param rel - operator relacji
     */
    Statement(Expression e1, Expression e2, String rel)
    {
        // ehhh
        if(rel.equals(":=")) isAssignment = true;
        else isAssignment = false;
        isExpression = false;
        isRelation = true;
        this.rel = rel;
        lhs = e1;
        rhs = e2;
    }

    /**
     * Balansuje drzewo wyrażeń w taki sposób że jest respektowane pierwszeństwo działań
     * dalsza dokumentacja w {@link Expression#fixPrecedence()}
     */
    public void fixPrecedence()
    {
        if(lhs != null)lhs = lhs.fixPrecedence();
        if(rhs != null)rhs = rhs.fixPrecedence();
    }

    /**
     * Określa wartość całego zdania na podstawie jego rodzaju
     * @param scope kontekst wykonywania ewaluacji (zmienne i funkcje)
     * @return obiekt {@link Value} reprezentujący wartość wyrażenia
     */
    public Value eval(EvalScope scope)
    {
        if(isAssignment)
        {
            return new Value(true);
        }
        else if(isRelation)
        {
            //TODO wprowadzić inne relacje oprócz relacji równości
            Value e1 = lhs.eval(scope);
            Value e2 = rhs.eval(scope);
            if(e1.equals(e2)) return new Value(true);
            else return new Value(false);
        }
        else if(isExpression)
        {
            return lhs.eval(scope);
        }
        else
        {
            return new Value();
        }
    }
    @Override
    public String toString()
    {
        if(isExpression) return lhs.toString();
        else if(isAssignment) return lhs.toString() + " ASSIGN " + rhs.toString();
        else if(isRelation) return  lhs.toString() + "REL "+rel+" "+ rhs.toString();
        else return "ERROR toString: " + super.toString();
    }

}
