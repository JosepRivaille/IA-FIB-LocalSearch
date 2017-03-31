import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class SensorsSuccessorsHC implements SuccessorFunction {

    private int operators;

    SensorsSuccessorsHC(int operators) {
        this.operators = operators;
    }

    public List getSuccessors(Object state) {
        ArrayList<Successor> childrenStates = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;
        SensorsBoard.COST = board.costHeuristic();
        SensorsBoard.INFORMATION = board.informationHeuristic();

        for (int i = 0; i < board.getSensorsSize(); i++) {
            for (int j = 0; j < board.getProblemSize(); j++) {
                if (i != j) {
                    SensorsBoard successorBoard = new SensorsBoard(board);
                    if (operators <= 0 && successorBoard.switchConnection(i, j)) {
                        childrenStates.add(new Successor("switch connection " + i + " - " + j, successorBoard));
                    }
                    if (operators >= 0 && j < board.getSensorsSize()) {
                        successorBoard = new SensorsBoard(board);
                        if (successorBoard.swapConnection(i, j)) {
                            childrenStates.add(new Successor("swap connection " + i + " - " + j, successorBoard));
                        }
                    }
                }
            }
        }

        return childrenStates;
    }
}
