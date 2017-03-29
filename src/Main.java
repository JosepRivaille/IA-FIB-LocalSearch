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
        Problem p = null;
        Search alg = null;
        switch (args[0]) {
            case "HC":
                p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                alg = new HillClimbingSearch();
                break;
            case "SA":
                p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());
                alg = new SimulatedAnnealingSearch(20000, 100, 50, 0.001);
                break;
            default:
                System.out.println("Use args HC or SA to use HillClimbingSearch or SimulatedAnnealingSearch respectively");
                System.exit(1);
        }

        // Instantiate the SearchAgent object
        SearchAgent agent = new SearchAgent(p, alg);

        // We print the results of the search
        printActions(agent.getActions());
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
            String action = action1.toString();
            System.out.println(action);
        }
    }

}
