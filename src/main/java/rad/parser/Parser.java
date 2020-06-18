package rad.parser;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * <pre>
 * Parser gramatyki LL(1) programu
 *
 * Każdemu symbolowi nieterminalnemu odpowiada funkcja parse_SYMBOL
 * która zwraca obiekt reprezentujący, niekoniecznie unikalny dla symbolu
 * np.
 * Stmt - parse_Stmt zwraca {@link Statement}
 * Expr - parse_Expr zwraca {@link Expression}
 * Term - parse_Term zwraca {@link Expression}
 *
 * Zasady gramatyki:
 *{@code
 * Stmt -> Expr Stmt1
 * Stmt1 -> asgn Expr
 * Stmt1 -> ε
 *
 * Expr -> Term Expr1
 * Expr -> mod Expr
 * Expr1 -> op Expr
 * Expr1 -> ε
 *
 * Term -> num
 * Term -> ( Expr )
 * Term -> ident ArgList
 *
 * ArgList -> ( List )
 * ArgList -> ε
 *
 * List -> Expr List1
 * List1 -> , List
 * List1 -> ε
 * }
 * </pre>
 */
public class Parser {

    Tokenizer tokenizer;
    LinkedList<Token> tokens;
    Token current;
    Token next;
    boolean success = false;

    /**
     * Konstruuje parser bez stanu, ze standardowym tokenizerm
     */
    public Parser()
    {
        tokenizer = new Tokenizer();
        tokenizer.defStdTokens();
    }

    /**
     * Parsuje cały string i zwraca obiekt reprezentujący narwyższy symbol nieterminalny
     * @param str linijka programu do parsowania
     * @return obiekt {@link Statement}
     */
    public Statement parse(String str) {
        /*Reset state*/
        current = null;
        next = null;
        tokenizer.clear();
        tokens = null;

        tokenizer.tokenize(str);
        tokens = tokenizer.getTokens();
        try
        {
            current = tokens.removeFirst();
        }catch (NoSuchElementException e)
        {
            throw new ProgramError(Error.MISSING_SYMBOL,"Statement");
        }
        next = tokens.peekFirst();
        return parse_Stmt();
    }

    /**
     * sprawdza czy obecny token jest danego typu i go zwraca,
     * @throws ProgramError jeżeli token jest innego typu niż spodziewany
     * @param token_type spodziewany typ tokenu
     * @return token o spodziewanym typie
     */
    public Token get(int token_type)
    {
        if(current.token != token_type)
        {
            throw new ProgramError(Error.UNEXPECTED_TOKEN,current.toString());
        }
        else
        {
            Token ret = current;
            nextToken();
            return ret;
        }
    }

    /**
     * pobiera następny token i ustawia look-ahead
     */
    public void nextToken() {
        if(next == null)
        {
            current = null;
        }
        else
        {
            current = tokens.removeFirst();
        }
        next = tokens.peekFirst();
    }
    private Statement parse_Stmt()
    {
        /*Stmt -> Expr Stmt1*/
        Statement ret = new Statement(parse_Expr());
        ret = parse_Stmt1(ret);
        return ret;
    }
    private Statement parse_Stmt1(Statement lhs)
    {
        if(current == null)
        {
            /*Stmt1 -> null*/
            return lhs;
        }
        else if(current.token == Token.ASGN)
        {
            /*Stmt1 -> asgn Expr*/
            String rel = get(Token.ASGN).seq;
            Expression rhs = parse_Expr();
            return new Statement(lhs.lhs,rhs,rel);
        }
        else
        {
            /*Stmt1 -> null*/
            return lhs;
        }

    }
    private LinkedList<Expression> parse_Arglist()
    {
        if(current == null || current.token != Token.OPEN_BRACKET)
        {
            /*Arglist -> null*/
            return null;
        }
        else
        {
            /*Arglist -> ( List )*/
            get(Token.OPEN_BRACKET);
            LinkedList<Expression> list = parse_List();
            get(Token.CLOSE_BRACKET);
            return list;
        }
    }
    private LinkedList<Expression> parse_List()
    {
        /*List -> Expr List1*/
        Expression e = parse_Expr();
        LinkedList<Expression> list = parse_List1();
        if(list == null)
        {
            list = new LinkedList<>();
        }
        list.push(e);
        return list;
    }
    private LinkedList<Expression> parse_List1()
    {
        /*List1 -> null*/
        if(current == null || current.token != Token.COMA) return null;
        /*List1 -> , List*/
        get(Token.COMA);
        return parse_List();
    }
    private Expression parse_Expr(){

        if(current.token == Token.MOD)
        {
            /*Expr -> mod Expr*/
            Token mod = get(Token.MOD);
            Expression ret = parse_Expr();
            switch (mod.seq)
            {
                case "&":
                    ret.modReference=true;
                    break;
            }
            return ret;
        }
        else
        {
            /*Expr -> Term Expr1*/
            Expression ret = parse_Term();
            ret = parse_Expr1(ret);
            return ret;
        }
    }
    private Expression parse_Expr1(Expression lhs){
        /*Expr1 -> null*/
        if(current == null) return lhs;
        if(current.token != Token.OP) return lhs;

        /*Expr1 -> op Expr*/
        char op = get(Token.OP).seq.charAt(0);
        Expression rhs = parse_Expr();
        return new Expression(lhs,rhs,op);
    }
    private Expression parse_Term()
    {
        if(current.token == Token.NUMBER)
        {
            /*Term -> num*/
            return new Expression(new Value(Integer.parseInt(get(Token.NUMBER).seq)));
        }
        else if(current.token == Token.IDENTIFIER)
        {
            /*Term -> ident Arglist*/
            String ident = get(Token.IDENTIFIER).seq;
            LinkedList<Expression> list = parse_Arglist();
            if(list == null) return new Expression(ident);
            else return new Expression(ident,list);

        }
        else if(current.token == Token.OPEN_BRACKET)
        {
            /*Term -> ( Expr )*/
            get(Token.OPEN_BRACKET);
            Expression e = parse_Expr();
            e = e.fixPrecedence();
            e.setPrecExclusion();
            get(Token.CLOSE_BRACKET);
            return e;
        }
        else
        {
           throw new ProgramError(Error.TERM_EXPECT,current.toString());
        }
    }


}
