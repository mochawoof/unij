class CharCell {
    public int code;
    public String line;
    public String display;
    public CharCell(int c, String l, String d) {
        code = c;
        line = l;
        display = d;
    }
    public String toString() {
        return display;
    }
}