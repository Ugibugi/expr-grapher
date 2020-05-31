import org.junit.*;
import rad.parser.*;
import rad.parser.Token;
import rad.parser.Tokenizer;

public class TokenizerTest {
    public Tokenizer tokenizer;
    @Before
    public void init()
    {

        tokenizer = new Tokenizer();
        tokenizer.add("\\(",                    Token.OPEN_BRACKET); // open bracket
        tokenizer.add("\\)",                    Token.CLOSE_BRACKET); // close bracket
        //tokenizer.add("[+-]",                   Token.UNARY_OP); // plus or minus
        tokenizer.add("[+-\\*/\\^]",              Token.OP); // plus or minus
        tokenizer.add("[=]",                    Token.REL); // plus or minus
        tokenizer.add("[0-9]+",                 Token.NUMBER); // integer number
        tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*",  Token.IDENTIFIER); // variable

    }
    @Test
    public void Basic()
    {
        tokenizer.tokenize("x_1=-b+sqrt(b^2-4*a*c)/2a");
        System.out.println(tokenizer.toString());
        Assert.assertTrue(true);
    }
}
