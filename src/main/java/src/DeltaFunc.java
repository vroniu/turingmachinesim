package src;

import java.util.ArrayList;
import java.util.Arrays;

public class DeltaFunc {

    private ArrayList<Move> moves;

    public DeltaFunc(String[] movesToGenerate) throws WrongMoveDefinition{
        moves = new ArrayList<>();
        for(String nMove : movesToGenerate){
            String[] moveData = nMove.split(",");
            if(moveData.length != 5)throw new WrongMoveDefinition(moveData.length, nMove);
            int move = 0;
            if(moveData[4].equals("<") || moveData[4].equals("L"))move = -1;
            else if(moveData[4].equals(">") || moveData[4].equals("R"))move = 1;
            System.out.println(moveData[4]);
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
