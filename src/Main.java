import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) throws Exception {
        Long time = System.currentTimeMillis();
        SensorsBoard board = new SensorsBoard();

        // Create the Problem object and instantiate the search algorithm
        Problem p;
        Search alg;
        if (args[0].equals("HC")) {
            p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
            alg = new HillClimbingSearch();
        } else {
            p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());
            alg = new SimulatedAnnealingSearch(20000, 100, 10, 0.001);
        }

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        // We print the results of the search
        //printActions(agent.getActions());
        printInstrumentation(agent.getInstrumentation());

        // Get total time
        time = System.currentTimeMillis() - time;

        // We print cost and information
        System.out.println();
        System.out.println("Total cost -> " + SensorsBoard.COST);
        System.out.println("Total information -> " + SensorsBoard.INFORMATION);
        System.out.println("Total time -> " + time + "ms");
    }

    private static void printInstrumentation(Properties properties) {
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String property = properties.getProperty(key);
            System.out.println(key + " : " + property);
        }

    }

    private static void printActions(List actions) {
        for (Object action1 : actions) {
            String action = (String) action1;
            System.out.println(action);
        }
    }

}
