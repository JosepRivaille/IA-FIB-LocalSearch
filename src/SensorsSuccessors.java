import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class SensorsSuccessors implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> childrenStates = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;

        for (int i = 0; i < board.getSensorsSize(); i++) {
            for (int j = 0; j < board.getProblemSize(); j++) {
                if (i != j) {
                    SensorsBoard auxSensors = new SensorsBoard(board);
                    if (auxSensors.addConnection(i, j)) {
                        childrenStates.add(new Successor("add connection " + i + " - " + j, auxSensors));

                        System.out.println(auxSensors.getTotalCost());
                        System.out.println(auxSensors.getTotalInformation());
                        System.out.println();
                    }
                }
            }
        }

        return childrenStates;
    }
}
