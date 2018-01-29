import java.io.IOException;
import java.util.Stack;

public class Parser {
    private Grammar grammar;
    private StringBuilder postfixStatement;
    private boolean debug = true;
    private Stack<Integer> opStack;

    public Parser() {
        this.grammar = new Grammar();
        this.postfixStatement = new StringBuilder();
        opStack = new Stack<>();
    }

    public boolean parseProgram(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Program...");
        int ch = ir.peek();
        if (ch == -1) {
            return true;
        }
        else {
            this.postfixStatement = new StringBuilder();
            this.opStack = new Stack<>();
            if (parseStatement(ir)) {
                System.out.println(this.postfixStatement);
                return parseProgram(ir);
            }
        }
        return false;
    }

    private boolean parseStatement(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Statement...");
        if (parseAsgnExpr(ir)) {
            if (ir.read() == ';') {
                while (!this.opStack.empty()) {
                    this.postfixStatement.append((char) ((int) this.opStack.pop()));
                }
                return true;
            }
        }
        if (debug) System.out.println(this.postfixStatement.append("...false"));
        return false;
    }

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

    private boolean parsePostfixExpr(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Postfix Expression...");
        int ch;
        if (parsePrimaryExpr(ir)) {
            ch = ir.peek();
            if (this.grammar.isPostunOp(ch)) {
                if (parsePostunOp(ir)) {
                    return parsePostfixExpr(ir);
                }
                return false;
            }
            return true;
        }
        return false;
    }

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
            ch = ir.read();
            if (ch == '(') {
                this.opStack.push(ch);
                if (parseAsgnExpr(ir)) {
                    if (ir.read() == ')') {
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

    private boolean parsePreunOp(InfixReader ir) throws IOException {
        if (debug) System.out.println("Parsing Preun Operator...");
        int ch = ir.read();
        if (this.grammar.isPreunOp(ch)) {
            if (debug) System.out.println((char) ch);
            parseOp(ch);
            return true;
        }
        return false;
    }

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

    private void parseOp(int ch) {
        while (!this.opStack.empty() && opPrec(ch) <= opPrec(this.opStack.peek())) {
            this.postfixStatement.append((char) ((int) this.opStack.pop()));
        }
        this.opStack.push(ch);
        return;
    }

    private int opPrec(int ch) {
        if (this.grammar.isTermOp(ch)) {
            return 1;
        }
        else if (this.grammar.isFactorOp(ch)) {
            return 2;
        }
        return -1;
    }
}
