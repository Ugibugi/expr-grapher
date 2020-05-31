import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rad.parser.*;
import rad.parser.Parser;
import rad.parser.Statement;

public class ParserTest {
    Parser parser;
    @Before
    public void init()
    {
        parser = new Parser();
    }
    @Test
    public void basicTest()
    {

        System.out.println(parser.parse("a^2+b^2").toString());
        Assert.assertTrue(true);
    }
    @Test
    public void precendenceTest()
    {
        Statement s = parser.parse("3*(3+3*3)+3");
        s.fixPrecedence();
        System.out.println(s.toString());
        Assert.assertTrue(true);
    }
    @Test
    public void evalTest()
    {

    }
}
