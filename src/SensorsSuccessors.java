import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class SensorsSuccessors implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> childrenStates = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;
        SensorsBoard.COST = board.costHeuristic();
        SensorsBoard.INFORMATION = board.informationHeuristic();

        for (int i = 0; i < board.getSensorsSize(); i++) {
            for (int j = 0; j < board.getProblemSize(); j++) {
                if (i != j) {
                    SensorsBoard successorBoard = new SensorsBoard(board);
                    if (successorBoard.switchConnection(i, j)) {
                        childrenStates.add(new Successor("switch connection " + i + " - " + j, successorBoard));
                    }
                }
            }
            for (int j = 0; j < board.getSensorsSize(); j++) {
                if (i != j) {
                    SensorsBoard successorBoard = new SensorsBoard(board);
                    if (successorBoard.swapConnection(i, j)) {
                        childrenStates.add(new Successor("swap connection " + i + " - " + j, successorBoard));
                    }
                }
            }
        }

        return childrenStates;
    }
}
