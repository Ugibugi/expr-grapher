package rad.parser;

public class ProgramError extends RuntimeException {

    Error err;
    public String errstr;
    ProgramError(Error e,String arg)
    {
        err = e;
        switch (e)
        {
            case VAR_UNDEFINED:
                errstr = String.format("Variable %s is not defined in this scope",arg);
                break;
            case VAR_IS_NOT_FUNC:
                errstr = String.format("Identifier %s is expected to be a function in this context.",arg);
                break;
            case FUNC_UNDEFINED:
                errstr = String.format("Function %s is not defined in this scope.",arg);
                break;
            case TERM_EXPECT:
                errstr = String.format("Number or identifier expected. Got: %s",arg);
                break;
            case ASSIGN_ERROR:
                errstr = String.format("Bad assignment: %s",arg);
                break;
            case UNEXPECTED_TOKEN:
                errstr = String.format("Unexpected token: %s",arg);
                break;
            case UNEXPECTED_CHAR:
                errstr = String.format("Unexpected character at: %s, try using diffrent name",arg);
                break;
            case MISSING_SYMBOL:
                errstr = String.format("Expected %s",arg);
                break;
            case TYPE_ERROR:
                errstr = String.format("Type Error: %s",arg);
                break;
            default:
                errstr = "Unknown error";
        }
    }
    public String getMessage()
    {
        return "PROGRAM ERROR: "+errstr;
    }
}
