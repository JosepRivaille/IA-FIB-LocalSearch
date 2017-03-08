import aima.search.framework.GoalTest;

public class SensorsGoal implements GoalTest {

    public boolean isGoalState(Object state) {
        return ((SensorsBoard) state).isGoal();
    }
}