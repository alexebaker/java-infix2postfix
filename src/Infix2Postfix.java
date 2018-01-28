import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Infix2Postfix {
    public static boolean debug = true;

    private Parser parser;
    private Grammar grammer;

    public Infix2Postfix() {
        this(System.in);
    }

    public Infix2Postfix(InputStream stream) {
        this.parser = new Parser(stream);
        this.grammer = new Grammar();
    }

    public void convert2Postfix() {
        while (!this.parser.isEmpty()) {
            String statement = this.parser.getStatement();
            System.out.println(statement);
        }
        return;
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 1) {
            System.setIn(new FileInputStream(args[1]));
        }

        Infix2Postfix converter = new Infix2Postfix();
        converter.convert2Postfix();
    }
}
