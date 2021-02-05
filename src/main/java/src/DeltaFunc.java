package src;

import java.util.ArrayList;

public class DeltaFunc {

    private ArrayList<Move> moves;

    public DeltaFunc(ArrayList<String> movesToGenerate) throws IllegalArgumentException, WrongMoveDefinition{
        moves = new ArrayList<>();
        for(String nMove : movesToGenerate){
            String[] moveData = nMove.split(",");
            if(moveData.length != 5)throw new WrongMoveDefinition(moveData.length, nMove);
            int move;
            switch (moveData[4]) {
                case "<":
                case "L":
                    move = -1;
                    break;
                case ">":
                case "R":
                    move = 1;
                    break;
                case "-":
                case "N":
                    move = 0;
                    break;
                default:
                    throw new IllegalArgumentException("Wrong move definition! Expected <,L,>,R,-,N, got: " + moveData[4]);
            }
            moves.add(new Move(Integer.parseInt(moveData[0]),
                                moveData[1].charAt(0),
                                Integer.parseInt(moveData[2]),
                                moveData[3].charAt(0),
                                move));
        }
    }

    public Move getMatchingMove(int currState, char readChar){
        for(Move m : moves){
            if (m.checkMatch(currState, readChar))return m;
        }
        return null;
    }

}
