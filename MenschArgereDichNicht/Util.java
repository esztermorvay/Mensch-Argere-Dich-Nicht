public class Util {

    public static int diceRoll() {
        return (int) (6 * Math.random()) + 1;
    }

    public static int convertToBoardPosition(Color.COLOR color, int position) {
        switch (color) {
        case BLUE:
            return position;
        case YELLOW:
            return (position + Config.BOARD_SIZE / 4) % Config.BOARD_SIZE; // division should always be by 4 even for less players
        case GREEN:
            return (position + 2 * Config.BOARD_SIZE / 4) % Config.BOARD_SIZE;
        case RED:
            return (position + 3 * Config.BOARD_SIZE / 4) % Config.BOARD_SIZE;
        default:
            return 0;
        }
    }
}
