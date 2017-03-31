import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception {
        Long time = System.currentTimeMillis();
        SensorsBoard board;

        System.out.println("Choose experiment:");
        System.out.println("1) Operators");
        System.out.println("2) Initial state");
        System.out.println("3) SA parameters");
        System.out.println("4) Incrementation");

        Integer option = new Scanner(System.in).nextInt();
        switch (option) {
            case 1:
                Experiments.operators();
            case 2:

        }

        // Create the Problem object and instantiate the search algorithm
        /*Problem p = null;
        Search alg = null;
        if (args.length == 0) {
            System.out.println("Use args HC or SA to use HillClimbingSearch or SimulatedAnnealingSearch respectively");
            System.exit(1);
        } else {
            /*System.out.print("Choose initial state generator (DS | SG | DG): ");
            switch (args[0]) {
                case "HC":
                    board = new SensorsBoard(new Scanner(System.in).next());
                    p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                    alg = new HillClimbingSearch();
                    break;
                case "SA":
                    board = new SensorsBoard(new Scanner(System.in).next());
                    p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());
                    alg = new SimulatedAnnealingSearch(20000, 100, 50, 0.001);
                    break;
                default:
                    System.out.println("Use args HC or SA to use HillClimbingSearch or SimulatedAnnealingSearch respectively");
                    System.exit(1);
            }
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
        System.out.println("Total time -> " + time + "ms");*/
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
