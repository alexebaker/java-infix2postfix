/**
 * @author Alexander Baker
 *
 * CS 554
 * Infix to Postfix converter
 *
 * This class wraps a BufferedReader to provide helper functions for the parser.
 */

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

    /**
     * Reads a byte from the input.
     *
     * @return the read byte
     * @throws IOException if the byte could not be read
     */
    public int read() throws IOException {
        int ch = this.in.read();
        while (Character.toString((char) ch).matches("\\s")) {
            ch = this.in.read();
        }
        return ch;
    }

    /**
     * Shows the next byte in the input without reading it.
     *
     * @return the next byte in the input
     * @throws IOException if the next byte cannot be read
     */
    public int peek() throws IOException {
        return peek(1);
    }

    /**
     * Gets the nth byte from the current position in the input.
     *
     * @param readAheadLimit number of bytes to read ahead
     * @return the nth byte in the input
     * @throws IOException if the nth byte could not be read
     */
    public int peek(int readAheadLimit) throws IOException {
        this.in.mark(readAheadLimit);

        int ch = read();
        for (int i = 1; i < readAheadLimit; i++) {
            ch = read();
        }

        this.in.reset();
        return ch;
    }

    /**
     * Calls the buildin Reader method, identifies if peek can be used.
     *
     * @return true if mark is supported, false otherwise
     */
    public boolean markSupported() {
        return this.in.markSupported();
    }

    /**
     * Closes the input reader
     *
     * @throws IOException if the reader could not be closed
     */
    public void close() throws IOException {
        this.in.close();
    }
}
