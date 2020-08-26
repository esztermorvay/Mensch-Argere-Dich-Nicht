import java.util.Scanner;

public class Board {

    // Player[] players;
    Player[] players;
    int turnNumber = 0;
    Scanner in;

    public Board() {
        players = new Player[Config.NUM_PLAYERS];
        for (int i = 0; i < Config.NUM_PLAYERS; i++) {
            players[i] = new Player(Color.COLOR.values()[i]);
        }

        in = new Scanner(System.in);

    }

    public void startGame() {
        System.out.println("Welcome to Mensch Argere Dich Nicht!");
        System.out.println("You will take turns rolling dice to move your pieces across the circular board.");
        System.out.println("The aim of the game is to get all your pieces HOME first!");
        System.out.println("Your pieces start trapped at START, but you can roll a 6 to set them free.");
        System.out.println("But be careful, in the open other players can capture you and send you back to START!");
        System.out.println("Capture other players' pieces by landing on the tiles they occupy!");
        System.out.println("The game has been set to starting with " + Config.NUM_PLAYERS + " players each with " + Config.NUM_PIECES + " pieces on a " + Config.BOARD_SIZE + " tile board (see config file to change)"); // i.e. Game starting with 4 players each with 4 pieces on a 40 tile board
        System.out.println("Begin!");
        Player winner = null;
        while (winner == null) {
            int k = turnNumber % Config.NUM_PLAYERS;
            turn(players[k]);
            turnNumber++;
            winner = checkForWinner();
        }
        System.out.println("Congratulations! The winner is " + winner.getColor() +"!");
    }

    public Player getPlayerbyColor(Color.COLOR playerColor) {
        for (int i = 0; i < players.length; i++) {
            if (players[i].getColor() == playerColor) {
                return players[i];
            }
        }
        return null;
    }

    public String positionToString(Piece p) {
        return positionToString(p, 0);
    }

    public String positionToString(Piece p, int dist) {
        if (isOnBoard(p.getPosition())) {
            return "tile " + (p.getAbsPosition() + dist);
        } else if (p.getPosition() + dist == 0) {
            return "start";
        } else {
            return "home " + (p.getPosition() + dist - Config.BOARD_SIZE);
        }
    }

    public boolean isOnBoard(int ix) {
        return ix != 0 && ix <= Config.BOARD_SIZE;
    }

    public void turn(Player player) {

        System.out.println();
        System.out.println("--------------------\nTurn " + turnNumber + ": " + player.getColor() + " to move"); // i.e. red to move

        System.out.print("Hit enter to roll die: ");
        in.nextLine();

        int die = Util.diceRoll();
        System.out.println("You rolled a " + die + "!");

        // if a 6 is rolled and there are pieces in start and there are no pieces in pos 1, give option to advance piece
        int exitCode = 1;
        if (die == 6) {
            exitCode = offerToBringPieceIntoPlay(player); // 1 --> saved 6, 0 --> brought into play
        }

        if (exitCode == 1) {

            String output = "";
            Piece[] p = player.getPieces();
            String possibleChoices = ""; // can this be an array?
            for (int i = 0; i < p.length; i++) {
                output += "\n  - piece #" + p[i].getPieceNum() + ": the piece on " + positionToString(p[i]);

                if (isPossibleToMove(p[i], die)) {
                    possibleChoices += p[i].getPieceNum();
                    output += " to " + positionToString(p[i], die);

                    if (isOnBoard(p[i].getPosition())) {
                        output += ", " + (Config.BOARD_SIZE - p[i].getPosition() - die + 1) + " spaces away from home";
                    }

                    Piece pieceAtTarget = getPieceByPos(p[i].getAbsPosition() + die);
                    if (pieceAtTarget != null) {
                        output += ", will knock out " + pieceAtTarget.getColor();
                    }

                } else {
                    output += " (can not be moved)";
                }
            }
            if (possibleChoices.length() == 0) {
                System.out.println("Sorry, you can't move any pieces.");
            } else {
                System.out.print("These are your options for which pieces to move:" + output + "\nWhich piece would you like to move? Enter the piece #: ");

                int val = -1;
                boolean awaitInput = true;
                do {
                    if (in.hasNextInt()) {
                        val = in.nextInt();
                        if (possibleChoices.indexOf(val + "") > -1) { 
                            awaitInput = false;
                        } else {
                            System.out.print("You must enter a valid number from the selection. Try again: ");
                        }
                    } else {
                        // wacky buffer clearing
                        in.nextLine();
                        System.out.print("You must enter a number, not a string. Try again: ");
                    }
                } while (awaitInput);
                moveForward(player.getPieces()[val - 1], die);
            }
        }

        if (die == 6) {
            System.out.println("Since you rolled a SIX, you get an extra turn!");
            turn(player); // nice recursive call for extra turn
        }
    }

    public int offerToBringPieceIntoPlay(Player player) {

        // if possible to bring piece into play, ask player
        if (player.getPieceAt(0) == null) {
            System.out.println("Sorry, you have no pieces to bring into play");
            return -1;
        } else if (player.getPieceAt(1) != null) {
            System.out.println("Sorry, you have a piece blocking the entrance");
            return -1;
        } else {
            do {
                System.out.print("Do you want to bring a new piece into play? Type yes or no: ");
            } while (!in.hasNext());

            // if decided to bring piece into play
            if (in.next().toLowerCase().equals("yes")) {
                moveForward(player.getPieceAt(0), -1); //-1 special value for bringing pieceinto play
                System.out.println("Piece moved sucessfully!");
                return 0; // chose to move piece
            } else {
                System.out.println("Alright, you may move any active pieces 6 tiles.");
                return 1; // chose to not move piece
            }
        }
    }

    public void moveForward(Piece piece, int dist) {
        if (isPossibleToMove(piece, dist)) { // all illegal moves are handeled here
            if (dist == -1) { // move piece into play special code correction
                dist = 1;
            }
            if (isOnBoard(piece.getPosition())) { // if piece is not in home stretch
                int targetPos = piece.getAbsPosition() + dist;
                if (getPieceByPos(targetPos) != null) { // if the target square occupied
                    getPieceByPos(targetPos).setPosition(0); // sends piece to start
                }
            }
            piece.setPosition(piece.getPosition() + dist); // moves the piece to the target
        }
    }

    public Piece getPieceByPos(int pos) {
        for (Player player : players) {
            for (Piece p : player.getPieces()) {
                if (p.getAbsPosition() == pos) {
                    return p;
                }
            }
        }
        return null;
    }

    // public Piece getPiece(int pos, Color.COLOR playerColor) {
    //     for (Piece p : pieces) {
    //         if (p.getPosition() == pos && p.getColor() == playerColor) {
    //             return p;
    //         }
    //     }
    //     return null;
    // }

    // public Piece getPieceByNum(int num) {
    //     for (Piece p : pieces) {
    //         if (p.getPieceNum() == num) {
    //             return p;
    //         }
    //     }
    //     return null;
    // }

    // public void setPos(int pos, Piece p) {
    //     pieces[pos] = p;
    // }

    public boolean isPossibleToMove(Piece piece, int amount) {
        if (piece == null) {
            return false;
        }

        if (piece.getPosition() == 0 && amount != -1) {
            return false;
        }

        if (amount == -1) { // for bring piece into play to bypass illegal to move from home
            amount = 1;
        }

        if (piece.getPosition() + amount >= Config.BOARD_SIZE + Config.NUM_PIECES) {
            return false;
        }

        Piece pieceAtTarget;
        if (isOnBoard(piece.getPosition() + amount)) { // on normal board
            int targetIx = piece.getAbsPosition() + amount;
            pieceAtTarget = getPieceByPos(targetIx);
        } else { // in home stretch
            int targetIx = piece.getPosition() + amount;
            pieceAtTarget = getPlayerbyColor(piece.getColor()).getPieceAt(targetIx);
        }

        if (pieceAtTarget != null && pieceAtTarget.getColor() == piece.getColor()) {
            return false;
        }

        return true;

    }

    public Player checkForWinner() {
        for (Player p : players) {
            if (p.inWinningState()) {
                return p;
            }
        }
        return null;
    }
}
