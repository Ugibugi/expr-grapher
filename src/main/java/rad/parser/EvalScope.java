package rad.parser;
import java.util.HashMap;
import java.util.Map;

/**
 * Reprezentuje kontekst ewaluacji
 * Przechowuje zdefiniowane zmienne i funkcje pod postacią mapy {@link String} - {@link Expression}
 */
public class EvalScope {
    EvalScope()
    {
        vars = new HashMap<>();
        functions = new HashMap<>();
    }
    public EvalScope(EvalScope s)
    {
        vars = new HashMap<>(s.vars);
        functions = new HashMap<>(s.functions);
    }
    public Expression getf(String name)
    {
        Expression e = functions.get(name);
        if(e == null) throw new ProgramError(Error.FUNC_UNDEFINED,name);
        else return e;
    }
    public Expression getvar(String name)
    {
        Expression e = vars.get(name);
        if(e == null) throw new ProgramError(Error.VAR_UNDEFINED,name);
        else return e;
    }
    public boolean isFunction(String name)
    {
        return functions.containsKey(name);
    }
    public Map<String,Expression> vars;
    public Map<String,Expression> functions;
}
