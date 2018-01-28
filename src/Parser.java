import java.io.InputStream;
import java.io.Reader;
import java.util.Scanner;

public class Parser {
    private Scanner scanner;

    public Parser(InputStream stream) {
        this.scanner = new Scanner(stream);
    }

    public boolean parseProgram(Reader in) {
        return true;
    }

    public boolean parseStatement(Reader in) {
        return true;
    }

    public boolean parseProgram(Reader in) {
        return true;
    }
}
