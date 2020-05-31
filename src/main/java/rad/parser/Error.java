package rad.parser;

/**
 * Typy błędów zwracanych przez parser.
 */
public enum Error
{
    VAR_UNDEFINED,
    VAR_IS_NOT_FUNC,
    FUNC_UNDEFINED,
    TERM_EXPECT,
    ASSIGN_ERROR,
    UNEXPECTED_TOKEN,
    UNEXPECTED_CHAR,
    MISSING_SYMBOL,
    TYPE_ERROR
}
