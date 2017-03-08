import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

import java.util.ArrayList;
import java.util.List;

public class SensorsSuccessors implements SuccessorFunction {

    public List getSuccessors(Object state) {
        ArrayList<Successor> retval = new ArrayList<>();
        SensorsBoard board = (SensorsBoard) state;

        int min = Integer.MAX_VALUE;

        for (int i = 0; i < board.getProblemSize(); i++) {
            SensorsBoard auxSensors = new SensorsBoard(board);
            auxSensors.swapConnection(i);
            retval.add(new Successor("swap connection " + i, auxSensors));
            if (auxSensors.getTotalCost() < min) {
                min = auxSensors.getTotalCost();
            }
        }

        System.out.println(min);

        return retval;
    }
}
