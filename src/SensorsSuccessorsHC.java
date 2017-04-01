import Utils.OperatorsEnum;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class SensorsSuccessorsHC implements SuccessorFunction {

    static OperatorsEnum CHOSEN_OPERATOR;

    public List getSuccessors(Object state) {
        ArrayList<Successor> childrenStates = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;

        for (int i = 0; i < board.getSensorsSize(); i++) {
            for (int j = 0; j < board.getProblemSize(); j++) {
                if (i != j) {
                    SensorsBoard successorBoard = new SensorsBoard(board);
                    switch (CHOSEN_OPERATOR) {
                        case SWITCH:
                            if (successorBoard.switchConnection(i, j)) {
                                childrenStates.add(new Successor("switch connection " + i + " - " + j, successorBoard));
                            }
                            break;
                        case SWAP:
                            if (j < board.getSensorsSize()) {
                                successorBoard = new SensorsBoard(board);
                                if (successorBoard.swapConnection(i, j)) {
                                    childrenStates.add(new Successor("swap connection " + i + " - " + j, successorBoard));
                                }
                            }
                            break;
                        case BOTH:
                            if (successorBoard.switchConnection(i, j)) {
                                childrenStates.add(new Successor("switch connection " + i + " - " + j, successorBoard));
                            }
                            if (j < board.getSensorsSize()) {
                                successorBoard = new SensorsBoard(board);
                                if (successorBoard.swapConnection(i, j)) {
                                    childrenStates.add(new Successor("swap connection " + i + " - " + j, successorBoard));
                                }
                            }
                            break;
                    }
                }
            }
        }

        return childrenStates;
    }
}
