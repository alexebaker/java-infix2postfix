import java.io.*;

public class Infix2Postfix {
    public static boolean debug = false;

    private Parser parser;

    public Infix2Postfix() {
        this.parser = new Parser();
    }

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
