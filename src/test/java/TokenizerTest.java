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
        tokenizer.defStdTokens();

    }
    @Test
    public void Basic()
    {
        tokenizer.tokenize("x_1=-b+sqrt(b^2-4*a*c)/2a");
        System.out.println(tokenizer.toString());
        Assert.assertTrue(true);
    }
}
