/**
 * @author Alexander Baker
 *
 * CS 554
 * Infix to Postfix converter
 *
 * This class defines characters which are in the specified grammar.
 */

public class Grammar {
    /**
     * Determines in the given char is a Term Op from the grammar
     *
     * @param ch character to test
     * @return true if ch is a Term Op, false otherwise
     */
    public boolean isTermOp(int ch) {
        return Character.toString((char) ch).matches("[+-]");
    }

    /**
     * Determines in the given char is a Factor Op from the grammar
     *
     * @param ch character to test
     * @return true if ch is a Factor Op, false otherwise
     */
    public boolean isFactorOp(int ch) {
        return Character.toString((char)ch).matches("[*/]");
    }

    /**
     * Determines in the given char is a Preun Op from the grammar
     *
     * @param ch character to test
     * @return true if ch is a Preun Op, false otherwise
     */
    public boolean isPreunOp(int ch) {
        return Character.toString((char) ch).matches("[-]");
    }

    /**
     * Determines in the given char is a Postun Op from the grammar
     *
     * @param ch character to test
     * @return true if ch is a Postun Op, false otherwise
     */
    public boolean isPostunOp(int ch) {
        return Character.toString((char) ch).matches("[!%]");
    }

    /**
     * Determines in the given char is an ID from the grammar
     *
     * @param ch character to test
     * @return true if ch is an ID, false otherwise
     */
    public boolean isID(int ch) {
        return Character.toString((char) ch).matches("[a-zA-Z]");
    }

    /**
     * Determines in the given char is a Number from the grammar
     *
     * @param ch character to test
     * @return true if ch is a Number, false otherwise
     */
    public boolean isNum(int ch) {
        return Character.toString((char) ch).matches("[0-9]");
    }
}
