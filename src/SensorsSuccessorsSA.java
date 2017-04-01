import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SensorsSuccessorsSA implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> childrenStates = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;

        Random random = new Random();
        int algorithm = random.nextInt(2);

        if (algorithm == 0) {
            int i = random.nextInt(board.getSensorsSize());
            int j = random.nextInt(board.getProblemSize());

            SensorsBoard successorBoard = new SensorsBoard(board);
            if (i != j && successorBoard.switchConnection(i, j)) {
                childrenStates.add(new Successor("switch connection " + i + " - " + j, successorBoard));
            }
        } else {
            int i = random.nextInt(board.getSensorsSize());
            int j = random.nextInt(board.getSensorsSize());

            SensorsBoard successorBoard = new SensorsBoard(board);
            if (i != j && successorBoard.swapConnection(i, j)) {
                childrenStates.add(new Successor("swap connection " + i + " - " + j, successorBoard));
            }
        }

        return childrenStates;
    }
}
