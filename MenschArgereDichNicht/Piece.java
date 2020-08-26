public class Piece {

    private static int num = 0;
    private int pieceNum;
    private int position; // if 0 that means its at home. relative to each color. ranges from 0 to BoardSize + NumberOfPieces
    private Color.COLOR color;

    public Piece(Color.COLOR pColor) {
        color = pColor;
        pieceNum = num % Config.NUM_PIECES + 1;
        num++;
        position = 0;
    }

    public void addPiece() {
        position = 1;
    }

    public void movePiece(int amount) {
        position += amount;
    }

    public int getPosition() {
        return position;
    }

    public Color.COLOR getColor() {
        return color;
    }

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPosition(int pos) {
        position = pos;
    }

    // public boolean isHome() {
    //     return position > w;
    // }

    public int getAbsPosition() {
        return Util.convertToBoardPosition(color, position);
    }

    @Override
    public String toString() {
        return "" + getPosition();
    }
}
