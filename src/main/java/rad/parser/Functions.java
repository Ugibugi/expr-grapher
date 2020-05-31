package rad.parser;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * Singleton -
 * Przypisuje symbolom operacji ich funkcje
 */
public class Functions {
    public static Map<Character, BinaryOperator<Value>> biops;
    static
    {
        Map<Character, BinaryOperator<Value>> temp = new HashMap<>();
        temp.put('+',Functions::add);
        temp.put('-',Functions::sub);
        temp.put('*',Functions::mul);
        temp.put('/',Functions::div);
        temp.put('^',Functions::pow);
        temp.put('=',Functions::eq);
        temp.put('>',Functions::gt);
        temp.put('<',Functions::lt);
        biops = Collections.unmodifiableMap(temp);
    }
    public static Value add(Value a,Value b){
        return a.add(b);
    }
    public static Value sub(Value a,Value b){
        return a.sub(b);
    }
    public static Value mul(Value a,Value b){
        return a.mul(b);
    }
    public static Value div(Value a,Value b){
        return a.div(b);
    }
    public static Value pow(Value a,Value b) { return a.pow(b); }
    public static Value eq(Value a,Value b) { return new Value(a.equals(b)); }
    public static Value gt(Value a,Value b) { return a.gt(b); }
    public static Value lt(Value a,Value b) { return a.lt(b); }

}
