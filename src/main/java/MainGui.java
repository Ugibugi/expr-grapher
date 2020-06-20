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
            "__EPSILON := 1/100",
            "deriv(__dfunc,x) := (__dfunc(x+__EPSILON)-__dfunc(x))/__EPSILON",
            "fib(__n) := IF(&(__n<3),1,&(fib(__n-1)+fib(__n-2)))",
            "min(__a,__b) := IF(&(__a<__b),&(__a),&(__b))"
            };
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Grafer funkcji");
        GraphPanel plane = new GraphPanel();

        program = new Program();
        program.defFunc("DRAW(__func)",new Expression(){
            @Override
            public Value eval(EvalScope scope)
            {

               plane.functions.add((Double f)->{
                    EvalScope fscope = new EvalScope(scope);
                    //nazwa x oraz nazwa domyślna
                    fscope.vars.put("x",new Expression(new Value(f)));
                    fscope.vars.put("_0",new Expression(new Value(f)));
                    Value val = scope.getvar("__func").eval(fscope);
                    if(val.type == Value.BOOL) val.realval = val.boolval ? 1.0f : 0.0f;
                    return val.realval;
                });
               plane.repaint();
               return new Value(true);
            }
            public String toString() {
                return "<INTERNAL>";
            }
        }
        );
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
            public String toString() {
                return "<INTERNAL>";
            }
        });
        program.defFunc("SHOW(__expr)",new Expression(){
            public Value eval(EvalScope scope)
            {
                return new Value(scope.getvar("__expr").toString());
            }
            public String toString() {
                return "<INTERNAL>";
            }
        });
        program.defFunc("INTEGRATE(__func,__a,__b)",new Expression(){
            public Value eval(EvalScope scope)
            {
                EvalScope fscope = new EvalScope(scope);
                double begin = scope.getvar("__a").eval(scope).realval;
                double end = scope.getvar("__b").eval(scope).realval;
                double sign = 1.0;
                if(begin > end){
                    double buff = begin;
                    begin = end;
                    end = buff;
                    sign=-1.0;
                }
                double result=0.0;
                for(;begin<end;begin+=0.0001)
                {
                    fscope.vars.put("x",new Expression(new Value(begin)));
                    fscope.vars.put("_0",new Expression(new Value(begin)));
                    result += scope.getvar("__func").eval(fscope).realval*0.0001;
                }
                return new Value(sign*result);
            }
        });

        program.runStatements(stddefs);
        InputPanel inpanel = new InputPanel(program);
        frame.getContentPane().add(plane, BorderLayout.CENTER);
        frame.getContentPane().add(inpanel, BorderLayout.EAST);
        frame.getContentPane().add(new StatusPanel(plane),BorderLayout.SOUTH);
        frame.setSize(1600,1000);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
