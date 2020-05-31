import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import rad.parser.*;
import rad.parser.Program;
import rad.parser.Value;

public class ProgramTest {
    Program program;
    @Before
    public void init()
    {
        program = new Program();
    }
    @Test
    public void EvalTest()
    {
        System.out.println("EVAL TEST");
        Value e = program.eval("5");
        //System.out.println(e);
        Assert.assertTrue("Simple evaluation test", e.realval == 5.0);

        e = program.eval("5^2");
       // System.out.println(e);
        Assert.assertTrue("Simple evaluation test", e.realval == 25.0);

        e = program.eval("2*5^2");
        //System.out.println(e);
        Assert.assertTrue("Simple evaluation test", e.realval == 50.0);

        e = program.eval("1+2*5^2");
        //System.out.println(e);
        Assert.assertTrue("Simple evaluation test", e.realval == 51.0);

        e=program.eval("1+2");
        //System.out.println(e);
        Assert.assertTrue("Advanced evaluation test", e.realval == 3.0);

        e=program.eval("1+2*3");
       // System.out.println(e);
        Assert.assertTrue("Advanced evaluation test", e.realval == 7.0);

        e=program.eval("1+2*3^4");
        //System.out.println(e);
        Assert.assertTrue("Advanced evaluation test", e.realval == 163.0);
    }
    @Test
    public void relationTest()
    {
        System.out.println("RELATION TEST");
        Assert.assertEquals("Simple relation test",program.eval("0 = 0").boolval, true);
        Assert.assertEquals("Simple relation test",program.eval("1 > 0").boolval, true);
        Assert.assertEquals("Simple relation test",program.eval("1 < 2").boolval, true);
        Assert.assertEquals("Simple relation faliure test",program.eval("1 = 2").boolval, false);
        Assert.assertEquals("Simple relation faliure test",program.eval("1 > 2").boolval, false);
        Assert.assertEquals("Simple relation faliure test",program.eval("3 < 2").boolval, false);
        Assert.assertEquals("Advanced relation test",program.eval("3*2 = 2*3").boolval, true);
    }
    @Test
    public void precedenceTest()
    {
        System.out.println("PRECEDENCE TEST");
        Value e = program.eval("3*3+3");
        //System.out.println(e);
        Assert.assertTrue("Simple Precedence test",e.realval == 12.0);

        e = program.eval("3*(3+3*3)+3");
        //System.out.println(e);
        Assert.assertTrue("Advanced Precedence test",e.realval == 39.0);

        e=program.eval("3^(3*3+3^(2*2-4)) = 3^10");
        //System.out.println(e);
        Assert.assertTrue("Complex Precedence test", e.boolval);

    }
    @Test
    public void assignTest() {
        System.out.println("ASSIGNMENT TEST");
        Assert.assertTrue(program.eval("fun(x) := x^2+x+1").boolval == true);
        Assert.assertTrue(program.eval("fun(1) = 3").boolval == true);
        Assert.assertTrue(program.eval("x := 2").boolval == true);
        Assert.assertTrue(program.eval("fun(x) = 7").boolval == true);
        Assert.assertTrue(program.eval("fun(22/7) = 7").boolval == false);
    }
    @Test
    public void functionTest() {
        System.out.println("FUNCTION ARGUMENT TEST");
        program.eval("fun(g,x) := g(x)");
        program.eval("h(x) := 4*x");
        Assert.assertTrue(program.eval("fun(h,2) = 8").boolval == true);
    }
    @Test
    public void compositionTest() {
        System.out.println("FUNCTION COMPOSITION TEST");
        program.eval("mul(a,b) := a*b");
        program.eval("add(a,b) := a+b");
        program.eval("com(f,g) := f(g(a,b),g(a,b))");
        program.eval("def(f,a1,a2,a,b) := f(a1,a2)");

        Assert.assertTrue(program.eval("def(com,add,mul,5,3) = 30").boolval == true);
    }
   @Test
    public void refTest() {
        System.out.println("REFERENCE TEST");
        program.defFunc("IF(cond,yes,no)",new Expression(){
            public Value eval(EvalScope scope)
            {
               Value v = scope.getvar("cond").eval(scope);
               if(v.type != Value.BOOL) throw new RuntimeException("Pierwszy argument makra IF musi być relacją!");
               if(v.boolval)
               {
                   return scope.getvar("yes").eval(scope);
               }
               else
               {
                   return scope.getvar("no").eval(scope);
               }
            }
        });
        program.eval("fib(n) := IF(&(n<3),1,&(fib(n-1)+fib(n-2)))");
        Assert.assertTrue(program.eval("fib(15)").realval == 610);
    }

}
