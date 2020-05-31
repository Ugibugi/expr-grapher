package rad.parser;
import java.util.LinkedList;
import java.util.regex.*;

/**
 * Produkuje listę {@link Token} z listy znaków, używając wyrażeń regularnych
 */
public class Tokenizer {
    LinkedList<TokenInfo> tokenInfos;
    LinkedList<Token> tokens;

    /**
     * Konstruuje tokenizer bez żadnych definicji.
     * Przed rozpoczęciem działania trzeba dodać definicje funkcją {@link #add} lub {@link #defStdTokens}
     */
    public Tokenizer()
    {
        tokenInfos = new LinkedList<>();
        tokens = new LinkedList<>();
    }

    /**
     * defiuniuje wyrażenia regularne do rozpoznawania tokenów w ciągu znaków
     */
    public void defStdTokens()
    {
        add("\\(",                      Token.OPEN_BRACKET); // open bracket
        add("\\)",                      Token.CLOSE_BRACKET); // close bracket
        add("[+\\*/\\^=><-]",              Token.OP);
        add(":=",                       Token.ASGN);
        add("-?[0-9]+",                   Token.NUMBER); // integer number
        add("[a-zA-Z_][a-zA-Z0-9_]*",    Token.IDENTIFIER); // variable
        add(",",                        Token.COMA);
        add("&",                       Token.MOD);


    }

    /**
     * Dodaje definicję tokenu {@link TokenInfo } do tokenizera
     *
     * @param regex wyrażenie regularne do rozpoznania tokenu
     * @param num liczba identyfikująca typ tokenu
     */
    public void add(String regex,int num)
    {
        tokenInfos.add(
                new TokenInfo(Pattern.compile("^(" + regex + ")"), num)
        );
    }

    /**
     * Konsumuje cały podany string i wypełnia listę tokenów
     * @param str ciąg znaków do zamienienia na tokeny
     */
    public void tokenize(String str)
    {

        String s = new String(str);
        while(!s.equals(""))
        {
            boolean match = false;
            for(TokenInfo info : tokenInfos)
            {
                Matcher m = info.regex.matcher(s);
                //pattern zawsze pasuje na początku stringa albo wcale
                if(m.find())
                {
                    match = true;

                    String tok = m.group().trim();
                    tokens.add(new Token(info.token,tok));

                    s = m.replaceFirst("").trim();
                    break;
                }
            }
            if(!match) throw new ProgramError(Error.UNEXPECTED_CHAR,s);
        }

    }

    /**
     * Funkcja do debugowania
     *
     * @return reprezentacja tekstowa wszystkich tokenów
     */
    public String toString()
    {
        String s = "{";
        for(Token t : tokens)
        {
            s+=t.toString();
        }
        s+="}";
        return s;
    }

    /**
     * Zwraca listę wygenerowanych tokenów
     * @return lista tokenów
     */
    public LinkedList<Token> getTokens(){
        return tokens;
    }

    /**
     * resetuje stan tokenizera czyszcząc listę tokenów
     */
    public void clear(){tokens.clear();}

    /**
     *  Reprezentuje definicje tokenu według której token jest wykrywany i tworzony
     */
    private static class TokenInfo
    {
        public Pattern regex;
        public int token;

        /**
         * Konstruuje definicje tokenu z wyrażenie regularnego i nr identyfikacji
         * @param regex wyrażenie regularne znajdujące token
         * @param token nr typu tokenu
         */
        public TokenInfo(Pattern regex,int token)
        {
            this.regex = regex;
            this.token = token;
        }
    }

}
