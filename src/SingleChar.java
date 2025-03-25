class SingleChar {
    public String raw;
    public int code;
    public SingleChar(String r, int c) {
        raw = r;
        code = c;
    }
    public String toString() {
        return new String(Character.toChars(code));
    }
}