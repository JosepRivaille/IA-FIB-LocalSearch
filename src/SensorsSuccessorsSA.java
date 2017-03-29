import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SensorsSuccessorsSA implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> childrenStates = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;
        SensorsBoard.COST = board.costHeuristic();
        SensorsBoard.INFORMATION = board.informationHeuristic();

        Random random = new Random();
        int i = random.nextInt(board.getSensorsSize());
        int j = random.nextInt(board.getProblemSize());

        SensorsBoard successorBoard = new SensorsBoard(board);
        if (i != j && successorBoard.switchConnection(i, j)) {
            childrenStates.add(new Successor("switch connection " + i + " - " + j, successorBoard));
        }

        return childrenStates;
    }
}
