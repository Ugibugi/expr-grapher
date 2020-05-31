package rad.parser;
import java.util.*;

/**
 * <pre>
 * Reprezentuje wyrażenie programowe.
 * Wyrażenia reprezentują operacje matematyczne przez formę drzewa wyrażeń gdzie
 * każde wyrażenie (oprócz liści) ma swoje podwyrażenia które są połączone przez operator lub funckję.
 *
 * Każde wyrażenie może zostać zewaluowane, wtedy, ewaluowane są dzieci wyrażenia które znowu ewaluują swoje dzieci itd
 * ewaluacja zatrzymuje się na tzw wyrażeniu finalnym, którym może być liczba, lub identyfikator (zmienna lub funkcja)
 * wartości podwyrażeń są łączone, przez zapisany operator wyrażenia, w jedną liczbę.
 * </pre>
 */
public class Expression {

    public static int FUNCTION=1;
    public static int BINARY=2;
    public static int STATIC=3;
    public static int VAR=4;
    public static int REF =5;

    public boolean isFinal;
    public boolean isNumber;
    public boolean isVar;
    public boolean isBinary;
    public boolean isFunction;
    public boolean isReference;

    public boolean modReference=false;
    public boolean precExclusion = false;

    public Value staticval;
    public String varname;
    public ArrayList<Expression> arglist;

    public Expression lhs,rhs;
    public char op;
    public static Map<Character,Integer> precedence;

    static
    {
        Map<Character,Integer> prec = new HashMap<>();
        prec.put('+',1);
        prec.put('-',1);
        prec.put('*',2);
        prec.put('/',2);
        prec.put('^',3);
        prec.put('v',3);
        prec.put('=',0);
        prec.put('<',0);
        prec.put('>',0);
        precedence = Collections.unmodifiableMap(prec);
    }
    public Expression()
    {
        setFlags(STATIC);
        staticval = new Value();
        varname = null;
        lhs = null;
        rhs = null;
        op = ' ';
    }
    /**
     * Konstruktor z wyrażenia z wartości wprost podanej
     * Wartość takiego wyrażenia jest stała
     * @param val obiekt {@link Value} zawierający ststyczną wartość
     * */
    public Expression(Value val)
    {
        setFlags(STATIC);
        staticval = val;
        varname = null;
        lhs = null;
        rhs = null;
        op = ' ';

    }

    /**
     * Konstruktor wyrażenia ze zmiennej
     * Wartość takiego wyrażenia jest określana na podstawie kontekstu
     * @param ident nazwa zmiennej
     */
    public Expression(String ident)
    {
        setFlags(VAR);
        staticval = new Value();
        varname = ident;
        lhs = null;
        rhs = null;
        op = ' ';
        arglist = null;
    }
    /**
     * Konstruktor wyrażenia złożonego
     * Wartość takiego wyrażenia jest określana przez ewaluację podwyrażeń i łączenia ich operacją
     *
     * @param lhs lewa strona wyrażenia
     * @param rhs prawa strona wyrażenia
     * @param oper znak operacji
     * */
    public Expression(Expression lhs,Expression rhs,char oper)
    {
        setFlags(BINARY);
        staticval = new Value();
        varname = null;
        this.lhs = lhs;
        this.rhs = rhs;
        op = oper;
        arglist = null;
    }

    /**
     * Konstruktor wyrażenia funkcji.
     * @param ident nazwa funkcji
     * @param args lista wyrażeń będących argumentami funkcji
     */
    public Expression(String ident,LinkedList<Expression> args)
    {
        setFlags(FUNCTION);
        staticval = new Value();
        varname = ident;
        lhs = null;
        rhs = null;
        op = ' ';
        arglist = new ArrayList<>(args);
    }

    /**
     * Balansuje drzewo wyrażeń w taki sposób że jest respektowane pierwszeństwo działań
     * Działa w sposób rekursywny, wykonując operacje rotacji podrzew w celu
     * wyniesienia wyrażenia o największym priorytecie jak najbliżej korzenia
     * @return nowy korzeń poddrzewa
     */
    public Expression fixPrecedence()
    {
        if(isFinal || precExclusion)return this;

        lhs = lhs.fixPrecedence();
        rhs = rhs.fixPrecedence();

        if(rhs.prec() < this.prec())
        {
            Expression oldrhs = rhs;
            rhs = rhs.lhs;
            oldrhs.lhs = this;
            return oldrhs;
        }
        else if(lhs.prec()<this.prec())
        {
            Expression oldlhs = lhs;
            lhs = lhs.rhs;
            oldlhs.rhs = this;
            return oldlhs;
        }
        else
        {
            return this;
        }
    }

    /**
     * Określa priorytet operacji w wyrażeniu na podstawie
     * statycznej tablicy.
     * @return wartość liczbowa priorytetu
     */
    public int prec()
    {
        if(isFinal || precExclusion) return Integer.MAX_VALUE;
        else return precedence.get(op);
    }

    /**
     * Ewaluuje wyrażenie w oparciu o jego typ
     * Jeżeli wyrażenie jest statyczne - wtedy zwraca liczbę
     * Jeżeli wyrażenie jest zmienną - wtedy zwraca ewaluację wyrażenia zawartą w kontekście pod nazwą zmienej
     * Jeżeli wyrażenie jest funkcją - wtedy konstruowany jest nowy {@link EvalScope} na podstawie starego i
     * zastępowane są w nim odpowiednie wyrażenia.
     * @param scope kontekst ewaluacji
     * @return wartość wyrażenia
     */
    public Value eval(EvalScope scope)
    {
        /*if(modReference)
        {
            Expression deref = new Expression(this);
            deref.modReference = false;
            return new Value(deref);
        }*/

       if(isNumber) return staticval;
       else if(isVar) return scope.getvar(varname).eval(scope);
       else if(isBinary) return Functions.biops.get(op).apply(lhs.eval(scope),rhs.eval(scope));
       else if(isFunction)
       {
           EvalScope fScope = new EvalScope(scope);
           int i=0;
           LinkedList<String> argnamelist;
           /*Jeżeli jest wywoływana zmienna która nie ma sygnatury funkcji,
           * Automatycznie wygeneruj nazwy funkcji*/
            if(scope.isFunction(varname))
            {
                argnamelist =  scope.getf(varname).getArgNames();
            }
            else
            {
                argnamelist = new LinkedList<>();
                for(int it=0;it<arglist.size();it++)
                {
                    argnamelist.add("_"+it); // nazwy argumentów w formie _0, _1, _2, _3 . . .
                }
            }

           for(String argname : argnamelist)
           {
               Expression arg = arglist.get(i);
               /*jeżeli argument to zdefiniowana funkcja bez argumentów wtedy przekaż samą funkcję
               a nie jej ewaluację*/
               if(scope.isFunction(arg.varname) && arg.arglist == null)
               {
                   fScope.functions.put(argname,scope.getf(arg.varname)); // dodaj deklaracje funkcji
                   fScope.vars.put(argname,scope.getvar(arg.varname)); // i jej definicje
               }
               else if(arg.modReference == true)
               {
                   fScope.vars.put(argname,arg); // wyrażenie i argument jako jego definicje
               }
               else
               {
                   fScope.vars.put(argname,new Expression(arg.eval(scope)));
               }

               i++;
           }
           /*Eval function*/
           return scope.getvar(varname).eval(fScope);
       }
       else return new Value();
    }

    /**
     * Zwraca reprezentacje tekstową wyrażenia
     *
     * @return reprezentacja tekstowa
     */
    public String toString()
    {
       if(isNumber) return staticval.toString();
       else if(isVar) return "VAR:"+varname;
       else if(isBinary) return  "(" + lhs.toString() + op + rhs.toString()+ ")";
       else if(isFunction) return  "FUNC:" + varname;
       else return "ERR toString:" + super.toString();
    }

    /**
     * Sprawdza czy wyrażenie jest poprawną DEKLARACJĄ funkcji.
     * Żeby tak bylo wszystkie argumenty muszą być zmiennymi
     * @return czy jest poprawną deklaracją
     */
    public boolean isFunctionDeclaration()
    {
        if(arglist != null)
        {
            for(Expression arg: arglist)
            {
                if(!arg.isVar)return false;
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Zwraca (jeżeli to możliwe) liste nazw zmiennych w których
     * funkcja będzie oczekiwała swoich argumentów
     *
     * @return lista nazw argumentów lub null jeżeli wyrażenie nie jest poprawną deklaracją funkcji
     */
    public LinkedList<String> getArgNames()
    {
        if(!isFunctionDeclaration())return null;

        LinkedList<String> list = new LinkedList<>();
        for(Expression arg: arglist)
        {
            list.add(arg.varname);
        }
        return list;
    }

    /**
     * Setter maksymalnego proiorytetu
     * używany w przypadku wyrażeń w nawiasach
     */
    public void setPrecExclusion(){ precExclusion = true;}

    /**
     * Ustawia odpowiedznie flagi dla rodzaju wyrażenia
     * STATIC - wyrażenie statyczne znane bez konteksu
     * VAR - wyrażenie zmiennej
     * BINARY - wyrażenie operatora binarnego
     * FUNCTION wyrażenie funkcji
     *
     * @param type rodzaj wyrażenia
     */
    private void setFlags(int type)
    {
        if(type == STATIC)
        {
            isFinal = true;
            isNumber = true;
            isVar = false;
            isBinary = false;
            isFunction = false;
            isReference = false;
        }
        else if(type == VAR)
        {
            isFinal = true;
            isNumber = false;
            isVar = true;
            isBinary = false;
            isFunction = false;
            isReference = false;
        }
        else if(type == BINARY)
        {
            isFinal = false;
            isNumber = false;
            isVar = false;
            isBinary = true;
            isFunction = false;
            isReference = false;
        }
        else if(type == FUNCTION)
        {
            isFinal = true;
            isNumber = false;
            isVar = false;
            isBinary = false;
            isFunction = true;
            isReference = false;
        }

    }

}
