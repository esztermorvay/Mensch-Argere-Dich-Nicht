public class Player {

    private Piece[] pieces;
    private Color.COLOR color;

    public Player(Color.COLOR playerColor) {
        color = playerColor;
        pieces = new Piece[Config.NUM_PIECES];
        for (int i = 0; i < Config.NUM_PIECES; i++) {
            pieces[i] = new Piece(color);
        }
    }

    public Color.COLOR getColor() {
        return color;
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public Piece getPieceAt(int pos) {
        for (Piece p : pieces) {
            if (p.getPosition() == pos) {
                return p;
            }
        }
        return null;
    }

    public boolean inWinningState() {
        int sum = 0;
        for (Piece p : pieces) {
            sum += p.getPosition();
        }

        // winning sum is summation{k=0}{NUM_PIECES}{BOARD_SIZE+k}
        if (sum == Config.NUM_PIECES * Config.BOARD_SIZE + (Config.NUM_PIECES * (Config.NUM_PIECES - 1) / 2)) { // sum of all piece positions at winning state
            return true;
        } else {
            return false;
        }
    }
}
