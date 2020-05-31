package rad.parser;
import java.util.HashMap;
import java.util.LinkedList;
/**
 * Reprezentuje stan całego programu. Wszystkie wyrażenia i funkcje zdefiniowane w programie są pamiętane,
 * aż do jego zniszczenia.
 * */
public class Program {
    LinkedList<Statement> statements;
    Parser parser;
    EvalScope scope;

    /**
     * Domyślny konstruktor Programu
     * Po jego wywołaniu Program jest od razu gotowy do wywołania funkcji eval
     */
    public Program()
    {
        statements = new LinkedList<>();
        scope = new EvalScope();
        parser = new Parser();
    }

    /**
     * Parsuje i jeżeli to możliwe ewaluuje zdanie programu zawarte w linijce
     * Porównania (a = b) zwracają wartość logiczną określającą prawdziwość równania
     * Przypisania (a := b) zwracają wartość logiczną prawdziwą
     * Wyrażenia (a + b) zwracają wartość zmiennoprzecinkową określającą wartość wyrażenia
     *
     * @throws ProgramError w przypadku gdy linijka programowa zawiera jakikolwiek błąd składniowy lub semantyczny
     * @param str zawiera jedną linijkę programu, która powinna być parsowalna do jednego zdania programowego
     * @return obiekt {@link Value} reprezentujący wartość wyrażenia
     */
    public Value eval(String str)
    {
        Statement stmt = parser.parse(str);
        stmt.fixPrecedence();
        if(stmt.isAssignment)
        {
            if(!stmt.lhs.isFinal) throw new ProgramError(Error.ASSIGN_ERROR,"Cannot assign to complex expression.");
            if(stmt.lhs.isNumber) throw new ProgramError(Error.ASSIGN_ERROR,"Cannot assign to constant value");
            if(stmt.lhs.isFunction)
            {
                if(!stmt.lhs.isFunctionDeclaration()) throw new ProgramError(Error.ASSIGN_ERROR,"Function declaration must not contain complex expressions.");
                scope.functions.put(stmt.lhs.varname,stmt.lhs);
            }
            scope.vars.put(stmt.lhs.varname,stmt.rhs);
        }
        statements.add(stmt);
        return stmt.eval(scope);
    }

    public void runStatements(String[] stmts)
    {
        for(String s : stmts)
        {
            eval(s);
        }
    }

    /**
     * Ta metoda pozwala definiowanie specjalnych funkcji wykonujących kod JAVA a nie operacje artmetyczne
     * @param signature deklaracja funkcji - jej nazwa i argumenty
     * @param func potomek Expression implementujący funkcję {@link Expression#eval}
     */
    public void defFunc(String signature, Expression func)
    {
        Expression decl = parser.parse(signature).lhs;
        if(!decl.isFunction || !decl.isFunctionDeclaration()) throw new RuntimeException(signature + "is not proper function signature");

        scope.functions.put(decl.varname,decl);
        scope.vars.put(decl.varname,func);
    }
}
