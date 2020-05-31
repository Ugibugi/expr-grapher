import javax.swing.*;
import rad.gui.*;
import rad.parser.EvalScope;
import rad.parser.Expression;
import rad.parser.Program;
import rad.parser.Value;

import java.awt.*;
import java.util.function.Function;

public class MainGui {
    public static Program program;
    public static String[] stddefs = {
            "E := 174/64",
            "e := 174/64",
            "PI := 355/113",
            "__EPSILON := 1/10000",
            "deriv(__dfunc,x) := (__dfunc(x+__EPSILON)-__dfunc(x))/__EPSILON",
            "fib(n) := IF(&(n<3),1,&(fib(n-1)+fib(n-2)))",
            "min(__a,__b) := IF(&(__a<__b),&(__a),&(__b))"
            };
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Grafer funkcji");
        GraphPanel plane = new GraphPanel();
       // plane.functions.add(x -> (x-2)*(x+2)*x/3);

        program = new Program();
        program.defFunc("DRAW(__func)",new Expression(){
            @Override
            public Value eval(EvalScope scope)
            {
                //????
               plane.functions.add((Double f)->{
                    EvalScope fscope = new EvalScope(scope);
                    fscope.vars.put("x",new Expression(new Value(f)));
                    Value val = scope.getvar("__func").eval(fscope);
                    if(val.type == Value.BOOL) val.realval = val.boolval ? 1.0f : 0.0f;
                    plane.updateUI();
                    return val.realval;
                });
               return new Value(true);
            }
        });
        program.defFunc("IF(__cond,__yes,__no)",new Expression(){
            public Value eval(EvalScope scope)
            {
                Value v = scope.getvar("__cond").eval(scope);
                if(v.type != Value.BOOL) throw new RuntimeException("Pierwszy argument intstrukcji IF musi być relacją!");
                if(v.boolval)
                {
                    return scope.getvar("__yes").eval(scope);
                }
                else
                {
                    return scope.getvar("__no").eval(scope);
                }
            }
        });
        program.runStatements(stddefs);
        InputPanel inpanel = new InputPanel(program);
        frame.getContentPane().add(plane, BorderLayout.CENTER);
        frame.getContentPane().add(inpanel, BorderLayout.EAST);
        frame.getContentPane().add(new StatusPanel(plane),BorderLayout.SOUTH);
        frame.setSize(1280,1080);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
