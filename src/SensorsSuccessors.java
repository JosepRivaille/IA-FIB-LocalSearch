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

        System.out.println(SensorsBoard.COST + " - " + SensorsBoard.INFORMATION + " -> " + board.superHeuristic());

        for (int i = 0; i < board.getSensorsSize(); i++) {
            for (int j = 0; j < board.getProblemSize(); j++) {
                if (i != j) {
                    SensorsBoard auxSensors = new SensorsBoard(board);
                    if (auxSensors.swapConnection(i, j)) {
                        childrenStates.add(new Successor("swap connection " + i + " - " + j, auxSensors));
                    }
                }
            }
        }

        return childrenStates;
    }
}
