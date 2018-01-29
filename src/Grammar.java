public class Grammar {
    public boolean isTermOp(int ch) {
        return Character.toString((char) ch).matches("[+-]");
    }

    public boolean isFactorOp(int ch) {
        return Character.toString((char)ch).matches("[*/]");
    }

    public boolean isPreunOp(int ch) {
        return Character.toString((char) ch).matches("[-]");
    }

    public boolean isPostunOp(int ch) {
        return Character.toString((char) ch).matches("[!%]");
    }

    public boolean isID(int ch) {
        return Character.toString((char) ch).matches("[a-zA-Z]");
    }

    public boolean isNum(int ch) {
        return Character.toString((char) ch).matches("[0-9]");
    }
}
