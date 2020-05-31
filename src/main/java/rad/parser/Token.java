package rad.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Token - Reprezentuje najmniejszą, niepodzielną część programu
 */
public class Token {

    public static int OPEN_BRACKET = 1;
    public static int CLOSE_BRACKET = 2;
    public static int OP = 3;
    public static int REL = 4;
    public static int NUMBER =5;
    public static int IDENTIFIER = 6;
    public static int UNARY_OP = 7;
    public static int ASGN = 8;
    public static int COMA = 9;
    public static final int MOD = 12;
//    public static int L_PAREN =10;
//    public static int R_PAREN = 11;
    public static final Map<Integer,String> tokenNames;
    static {
        Map<Integer, String> names;
        names = new HashMap<>();
        names.put(OPEN_BRACKET,"OPEN_BRACKET");
        names.put(CLOSE_BRACKET,"CLOSE_BRACKET");
        names.put(UNARY_OP,"UNARY_OP");
        names.put(OP,"OP");
        names.put(REL,"REL");
        names.put(NUMBER,"NUMBER");
        names.put(IDENTIFIER,"IDENTIFIER");
        names.put(ASGN,"ASGN");
        names.put(COMA,"','");
        names.put(MOD,"MOD");
//        names.put(L_PAREN,"'('");
//        names.put(R_PAREN,"')'");
        tokenNames = Collections.unmodifiableMap(names);

    }
    public int token;
    public String seq;

    /**
     * Konstruuje token z typu tokena i właśniwej sekwencji znaków którą reprezentuje
     * @param token typ tokenu
     * @param seq sekwencja tworząca
     */
    public Token(int token, String seq) {
        this.token = token;
        this.seq = seq;
    }

    /**
     * Funkcja do debugowania
     * @return interpretacja tekstowa tokenu
     */
    @Override
    public String toString() {
        return "<" + tokenNames.get(token)   + " " + seq +
                '>';
    }
}