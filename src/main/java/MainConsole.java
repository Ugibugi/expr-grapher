import java.util.Scanner;

import rad.parser.*;


public class MainConsole {
    public static void main(String[] args)
    {

       Program p = new Program();
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        p.defFunc("IF(cond,yes,no)",new Expression(){
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
        while(!input.equals("exit"))
        {
            try {
                System.out.println(p.eval(input));
            }
            catch(ProgramError e)
            {
                System.out.println(e.errstr+"\nat:\n");
                e.printStackTrace();
            }
            catch(Exception e)
            {
                System.out.println(e.getMessage()+"\nat:\n");
                e.printStackTrace();
            }
            input = in.nextLine();
        }
    }
}
