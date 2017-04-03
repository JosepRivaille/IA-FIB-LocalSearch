import Utils.InitialStatesEnum;
import Utils.OperatorsEnum;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.framework.SuccessorFunction;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose experiment:");
        System.out.println("1) Operators");
        System.out.println("2) Initial state");
        System.out.println("3) SA parameters");
        System.out.println("4) Incrementation");
        System.out.println("5) Proportions");
        System.out.println("6) Increment Data Centers");
        System.out.println("7) Heuristic");
        System.out.println("*) Manual experiments");

        Integer option = scanner.nextInt();
        switch (option) {
            case 1:
                Experiments.operators();
                break;
            case 2:
                Experiments.initialStates();
                break;
            case 3:
                Experiments.parameters();
                break;
            case 4:
                Experiments.increments();
                break;
            case 5:
                Experiments.proportion();
                break;
            case 6:
                Experiments.dataCenters();
                break;
            case 7:
                Experiments.heuristic();
                break;
            default:
                System.out.println("Manual search will start:");

                System.out.print("Algorithm (HC | SA): ");
                String usedAlgorithm = scanner.next();
                System.out.print("Number of centers: ");
                SensorsBoard.NUMBER_CENTERS = scanner.nextInt();
                System.out.print("Number of sensors: ");
                SensorsBoard.NUMBER_SENSORS = scanner.nextInt();
                System.out.print("Centers seed: ");
                SensorsBoard.SEED_CENTERS = scanner.nextInt();
                System.out.print("Sensor seed: ");
                SensorsBoard.SEED_SENSORS = scanner.nextInt();

                Long time = System.currentTimeMillis();
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                SensorsBoard.INFORMATION_WEIGHT = 2.5;
                SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;

                SuccessorFunction successorFunction = usedAlgorithm.equals("HC")
                        ? new SensorsSuccessorsHC()
                        : new SensorsSuccessorsSA();

                Problem p = new Problem(board, successorFunction, new SensorsGoal(), new SensorsHeuristic());
                Search alg = usedAlgorithm.equals("HC")
                        ? new HillClimbingSearch()
                        : new SimulatedAnnealingSearch(10000, 100, 25, 0.1);

                SearchAgent searchAgent = new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                printActions(searchAgent.getActions());
                printInstrumentation(searchAgent.getInstrumentation());

                // We print cost and information
                System.out.println();
                System.out.println("Total cost -> " + SensorsBoard.TOTAL_COST);
                System.out.println("Total information -> " + SensorsBoard.TOTAL_INFORMATION);
                System.out.println("Total time -> " + time + "ms");
        }
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
