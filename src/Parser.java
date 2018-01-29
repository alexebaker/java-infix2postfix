/**
 * @author Alexander Baker
 *
 * CS 554
 * Infix to Postfix converter
 *
 * The main class which parses infix to postfix.
 */

import java.io.IOException;
import java.util.Stack;

public class Parser {
    private Grammar grammar;
    private StringBuilder postfixStatement;
    private boolean debug = false;
    private Stack<Integer> opStack;

    public Parser() {
        this.grammar = new Grammar();
        this.postfixStatement = new StringBuilder();
        opStack = new Stack<>();
    }

    /**
     * Function to be called to parse a program. This will parse multiple statements.
     *
     * @param ir input to parse the program from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    public boolean parseProgram(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Program...");
        int ch = ir.peek();
        if (ch == -1) {
            // End of the input stream. If we got this far without an error, then the parse was good
            return true;
        }

        // Re initialize the postfix conversion objs for next statement
        this.postfixStatement = new StringBuilder();
        this.opStack = new Stack<>();
        if (parseStatement(ir)) {
            return parseProgram(ir);
        }
        return false;
    }

    /**
     * Function to be called to parse a statement as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseStatement(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Statement...");
        int ch = ir.peek();
        if (ch == ';') {
            // An empty statement is valid
            ir.read();
            return true;
        }
        else {
            if (parseAsgnExpr(ir)) {
                if (ir.read() == ';') {
                    // Successful statement parse, print out the postfix
                    while (!this.opStack.empty()) {
                        this.postfixStatement.append((char) ((int) this.opStack.pop()));
                    }
                    System.out.println(this.postfixStatement);
                    return true;
                }
            }
        }
        if (debug) System.out.println(this.postfixStatement.append("...false"));
        return false;
    }

    /**
     * Function to be called to parse an AsgnExpr as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseAsgnExpr(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Assign Expression...");
        int chID = ir.peek();
        int chEq = ir.peek(2);
        if (this.grammar.isID(chID) && chEq == '=') {
            if (parseID(ir)) {
                int ch = ir.read();
                if (ch == '=') {
                    parseOp(ch);
                    return parseAsgnExpr(ir);
                }
            }
            return false;
        }
        return parseSimpleExpr(ir);
    }

    /**
     * Function to be called to parse a SimpleExpr as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseSimpleExpr(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Simple Expression...");
        int ch;
        if (parseTerm(ir)) {
            ch = ir.peek();
            if (this.grammar.isTermOp(ch)) {
                if (parseTermOp(ir)) {
                    return parseSimpleExpr(ir);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse a Term as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseTerm(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Term...");
        int ch;
        if (parseFactor(ir)) {
            ch = ir.peek();
            if (this.grammar.isFactorOp(ch)) {
                if (parseFactorOp(ir)) {
                    return parseTerm(ir);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse a Factor as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseFactor(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Factor...");
        int ch = ir.peek();
        if (this.grammar.isPreunOp(ch)) {
            if (parsePreunOp(ir)) {
                return parseFactor(ir);
            }
            return false;
        }
        return parsePostfixExpr(ir);
    }

    /**
     * Function to be called to parse a PostfixExpr as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */

    private boolean parsePostfixExpr(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Postfix Expression...");
        int ch = ir.peek();
        if (this.grammar.isPostunOp(ch)) {
            if (parsePostunOp(ir)) {
                return parsePostfixExpr(ir);
            }
            return false;
        }
        else if (this.grammar.isID(ch) || this.grammar.isNum(ch) || ch == '(') {
            if (parsePrimaryExpr(ir)) {
                ch = ir.peek();
                if (this.grammar.isPostunOp(ch)) {
                    // If the primary expression is followed by a PostunOp, we have to evaluate a PostfixExpr again
                    return parsePostfixExpr(ir);
                }
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Function to be called to parse a PrimaryExpr as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parsePrimaryExpr(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Primary Expression...");
        int ch = ir.peek();
        if (this.grammar.isID(ch)) {
            return parseID(ir);
        }
        else if (this.grammar.isNum(ch)) {
            return parseNum(ir);
        }
        else if (ch == '(') {
            // Handle parenthesis
            ch = ir.read();
            if (ch == '(') {
                this.opStack.push(ch);
                if (parseAsgnExpr(ir)) {
                    if (ir.read() == ')') {
                        // make sure the parenthesis is closed
                        // Resource: https://www.geeksforgeeks.org/stack-set-2-infix-to-postfix/
                        while (!this.opStack.empty() && this.opStack.peek() != '(') {
                            this.postfixStatement.append((char) ((int) this.opStack.pop()));
                        }
                        if (this.opStack.empty() || this.opStack.peek() != '(') {
                            return false;
                        }
                        this.opStack.pop();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Function to be called to parse a TermOp as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseTermOp(InfixReader ir) throws IOException {
       if (debug) System.out.println("Parsing Term Operator...");
        int ch = ir.read();
        if (this.grammar.isTermOp(ch)) {
            if (debug) System.out.println((char) ch);
            parseOp(ch);
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse a FactorOp as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseFactorOp(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Factor Operator...");
        int ch = ir.read();
        if (this.grammar.isFactorOp(ch)) {
            if (debug) System.out.println((char) ch);
            parseOp(ch);
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse a PreunOp as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parsePreunOp(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Preun Operator...");
        int ch = ir.read();
        if (this.grammar.isPreunOp(ch)) {
            if (debug) System.out.println((char) ch);
            parseOp((int) '_');
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse a PostunOp as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parsePostunOp(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Postun Operator...");
        int ch = ir.read();
        if (this.grammar.isPostunOp(ch)) {
            if (debug) System.out.println((char) ch);
            parseOp(ch);
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse an ID as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseID(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing ID...");
        int ch = ir.read();
        if (this.grammar.isID(ch)) {
            if (debug) System.out.println((char) ch);
            this.postfixStatement.append((char) ch);
            return true;
        }
        return false;
    }

    /**
     * Function to be called to parse a Num as defined in the grammar.
     *
     * @param ir input to parse from
     * @return true if the parse was successful, false otherwise
     * @throws IOException if the input could not be read
     */
    private boolean parseNum(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Number...");
        int ch = ir.read();
        if (this.grammar.isNum(ch)) {
            if (debug) System.out.println((char) ch);
            this.postfixStatement.append((char) ch);
            return true;
        }
        return false;
    }

    /**
     * Evaluate the opStack when an operator has been seen
     *
     * Resource: https://www.geeksforgeeks.org/stack-set-2-infix-to-postfix/
     *
     * @param ch operator to evaluate on the opStack
     */
    private void parseOp(int ch) {
        /**
         * @todo cleanup this if statement
         *
         * Can't figure out how to separate out this logic in one while loop cleanly
         */
        if (ch == '=' || ch == '_') {
            while (!this.opStack.empty() && opPrec(ch) < opPrec(this.opStack.peek())) {
                this.postfixStatement.append((char) ((int) this.opStack.pop()));
            }
        }
        else {
            while (!this.opStack.empty() && opPrec(ch) <= opPrec(this.opStack.peek())) {
                this.postfixStatement.append((char) ((int) this.opStack.pop()));
            }
        }
        this.opStack.push(ch);
    }

    /**
     * Determine the precedence of the operator
     *
     * Resource: https://www.geeksforgeeks.org/stack-set-2-infix-to-postfix/
     *
     * @param ch operator to get the precedence for
     * @return precedence of the operator, or -1 if invalid operator
     */
    private int opPrec(int ch) {
        if (ch == '=') {
            return 0;
        }
        else if (this.grammar.isTermOp(ch)) {
            return 1;
        }
        else if (this.grammar.isFactorOp(ch)) {
            return 2;
        }
        else if (this.grammar.isPreunOp(ch) || ch == '_') {
            return 3;
        }
        else if (this.grammar.isPostunOp(ch)) {
            return 4;
        }
        return -1;
    }
}
