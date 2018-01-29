/**
 * @author Alexander Baker
 *
 * CS 554
 * Infix to Postfix converter
 *
 * This is tha main class to run the converstion which reades from stdin
 * and write to stdout. If there is a parse error, then this exists with
 * status code 1.
 */

import java.io.*;

public class Infix2Postfix {
    public static boolean debug = false;

    private Parser parser;

    public Infix2Postfix() {
        this.parser = new Parser();
    }

    /**
     * Converts the given Reader to postfix and writes to stdout.
     *
     * @param ir Instance of InfixReader to read bytes from.
     * @return true if the input was successfully parsed, false otherwise
     * @throws IOException if input was unable to be read
     */
    public boolean convert2Postfix(InfixReader ir) throws IOException {
        if (debug) System.out.println("Beginning Parse...");
        return this.parser.parseProgram(ir);
    }

    public static void main(String[] args) throws IOException {
        InfixReader ir;
        if (args.length >= 1) {
            ir = new InfixReader(new FileReader(args[0]));
        }
        else {
            ir = new InfixReader();
        }

        Infix2Postfix i2c = new Infix2Postfix();
        if (!i2c.convert2Postfix(ir)) {
            ir.close();
            System.exit(1);
        }
        else {
           ir.close();
        }
    }
}
