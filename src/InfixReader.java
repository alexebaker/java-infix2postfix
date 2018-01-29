import java.io.*;

public class InfixReader {
    private BufferedReader in;

    InfixReader() {
        this(System.in);
    }

    InfixReader(InputStream inStream) {
        this.in = new BufferedReader(new InputStreamReader(inStream));
    }

    InfixReader(FileReader fReader) {
        this.in = new BufferedReader(fReader);
    }

    public int read() throws IOException {
        int ch = this.in.read();
        while (Character.toString((char) ch).matches("\\s")) {
            ch = this.in.read();
        }
        return ch;
    }

    public int peek() throws IOException {
        return peek(1);
    }

    public int peek(int readAheadLimit) throws IOException {
        this.in.mark(readAheadLimit);

        int ch = read();
        for (int i = 1; i < readAheadLimit; i++) {
            ch = read();
        }

        this.in.reset();
        return ch;
    }

    public boolean markSupported() {
        return this.in.markSupported();
    }

    public void close() throws IOException {
        this.in.close();
    }
}
