import Utils.InitialStatesEnum;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

class Experiments {

    static void operators() throws Exception {
        String filePath = "experiments/operators/";
        PrintWriter writerTime, writerCost, writerInfo;

        File fileTime = new File(filePath + "time.txt");
        fileTime.getParentFile().mkdirs();
        fileTime.createNewFile();

        File fileCost = new File(filePath + "cost.txt");
        fileCost.getParentFile().mkdirs();
        fileCost.createNewFile();

        File fileInfo = new File(filePath + "fileInfo.txt");
        fileCost.getParentFile().mkdirs();
        fileCost.createNewFile();

        writerTime = new PrintWriter(fileTime);
        writerCost = new PrintWriter(fileCost);
        writerInfo = new PrintWriter(fileTime);

        Random random = new Random();
        for (int i = 0; i < 1; i++) {
            Integer seedSensors = random.nextInt();
            Integer seedCenters = random.nextInt();

            for (int j = -1; j < 2; j++) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY, seedSensors, seedCenters);

                Problem p = new Problem(board, new SensorsSuccessorsHC(j), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                writerTime.append(time.toString()).append('\t');
                writerCost.append(SensorsBoard.COST.toString()).append('\t');
                writerInfo.append(SensorsBoard.INFORMATION.toString()).append('\t');
            }
            writerTime.append('\n');
            writerCost.append('\n');
            writerInfo.append('\n');
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

}
