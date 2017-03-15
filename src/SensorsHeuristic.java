import aima.search.framework.HeuristicFunction;

public class SensorsHeuristic implements HeuristicFunction {

    public double getHeuristicValue(Object n) {
        return ((SensorsBoard) n).superHeuristic();
    }
}
