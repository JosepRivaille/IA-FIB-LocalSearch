import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the board with sensors and data centers information.
 */
class SensorsBoard {

    private Double totalCost;
    private Double totalInformation;

    private static final Integer NUMBER_SENSORS = 100;
    private static final Integer NUMBER_CENTERS = 4;
    private static final Integer GENERATOR_SEED = 1234;

    private static final Integer MAX_SENSOR_CONNECTIONS = 3;
    private static final Integer MAX_DATA_CENTER_CONNECTIONS = 25;

    private static List<Sensor> sensorList;
    private static List<Centro> centerList;

    private List<SensorConnections> sensorConnections;

    static Double COST;
    static Double INFORMATION;

    /**
     * Default constructor
     */
    SensorsBoard() {
        sensorList = new ArrayList<>();
        centerList = new ArrayList<>();

        generateBoard();
        sensorConnections = new ArrayList<>(sensorList.size());

        totalCost = 0D;
        totalInformation = 0D;

        //generateEmptyInitialState();
        generateDummyInitialState();
    }

    /**
     * Child state constructor
     *
     * @param board Parent state board.
     */
    SensorsBoard(SensorsBoard board) {

        sensorConnections = new ArrayList<>();
        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            for (int j = 0; j < board.sensorConnections.get(i).inputSensors.size(); j++) {
                inputs.add(board.sensorConnections.get(i).inputSensors.get(j));
            }
            sensorConnections.add(new SensorConnections(board.sensorConnections.get(i).outputSensor, inputs));
        }

        totalCost = board.getTotalCost();
        totalInformation = board.getTotalInformation();
    }

    /**
     * Generates random board with N sensors and N data centers given a seed.
     */
    private void generateBoard() {
        sensorList.addAll(new Sensores(NUMBER_SENSORS, GENERATOR_SEED));
        centerList.addAll(new CentrosDatos(NUMBER_CENTERS, GENERATOR_SEED));
    }

    /*---- INITIAL STATES ----*/

    /**
     * Initial state generation without connections.
     */
    private void generateEmptyInitialState() {
        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            sensorConnections.add(new SensorConnections(null, inputs));
        }
    }

    /**
     * Initial state generation with random sequential connections.
     */
    private void generateDummyInitialState() {
        for (int i = 0; i < sensorList.size() - 1; i++) {
            List<Integer> inputs = new ArrayList<>();
            if (i > 0) {
                inputs.add(i - 1);
            }
            sensorConnections.add(new SensorConnections(i + 1, inputs));
            totalCost += calculateCost(i, sensorList.get(i), sensorList.get(i + 1));
        }
        List<Integer> inputs = new ArrayList<>();
        inputs.add(sensorList.size() - 2);
        sensorConnections.add(new SensorConnections(sensorList.size(), inputs));

        for (int i = 0; i < centerList.size(); i++) {
            inputs = new ArrayList<>();
            if (i == 0) {
                inputs.add(sensorList.size() - 1);
            }
            sensorConnections.add(new SensorConnections(null, inputs));
        }

        totalCost += calculateCostDataCenters(sensorList.size() - 1, sensorList.get(sensorList.size() - 1), centerList.get(0));

        totalInformation += calculateInformation();
    }

    /*---- OPERATORS ----*/

    /**
     * Adds a connection between two sensors.
     *
     * @param first  Sensor from that will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If connection has been added.
     */
    boolean addConnection(int first, int second) {
        if (isAllowedConnection(first, second)) {
            sensorConnections.get(second).inputSensors.add(first);
            sensorConnections.get(first).outputSensor = second;

            totalCost += calculatePartialCost(first, second);
            totalInformation = calculateInformation();

            return true;
        }
        return false;
    }

    /**
     * Removes a connection between two sensors.
     *
     * @param first  Sensor from that we wil will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If connection has been removed.
     */
    boolean removeConnection(int first, int second) {
        for (int i = 0; i < sensorConnections.get(second).inputSensors.size(); ++i) {
            if (sensorConnections.get(second).inputSensors.get(i) == first) {

                totalCost -= calculatePartialCost(first, second);

                sensorConnections.get(second).inputSensors.remove(i);
                sensorConnections.get(first).outputSensor = null;
                return true;
            }
        }
        return false;
    }

    private Double calculatePartialCost(int first, int second) {
        return second < sensorList.size()
                ? calculateCost(first, sensorList.get(first), sensorList.get(second))
                : calculateCostDataCenters(first, sensorList.get(first), centerList.get(second - sensorList.size()));
    }

    /**
     * Swaps output connection from first sensor to a second one.
     *
     * @param first  Sensor from that we will swap its output connection.
     * @param second Sensor that will receive the new input connection.
     * @return If connection has been swapped.
     */
    boolean swapConnection(int first, int second) {
        if (sensorConnections.get(first).outputSensor != null) {
            Integer oldEndConnection = sensorConnections.get(first).outputSensor;
            if (second == oldEndConnection) {
                return false;
            } else {
                for (int i = 0; i < sensorConnections.get(oldEndConnection).inputSensors.size(); i++) {
                    if (sensorConnections.get(oldEndConnection).inputSensors.get(i) == first) {
                        sensorConnections.get(oldEndConnection).inputSensors.remove(i);
                        Double oldCost = calculatePartialCost(first, oldEndConnection);
                        if (addConnection(first, second)) {
                            totalCost += (calculatePartialCost(first, second) - oldCost);
                            totalInformation = calculateInformation();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*-------------- HEURISTICS --------------*/

    /**
     * Heuristic based on cost.
     *
     * @return Heuristic value.
     */
    Double costHeuristic() {
        return totalCost;
    }

    /**
     * Heuristic based on information.
     *
     * @return Heuristic value.
     */
    double informationHeuristic() {
        return totalInformation;
    }

    /**
     * Heuristic based on cost and information.
     *
     * @return Heuristic value.
     */
    double superHeuristic() {
        return totalCost - Math.pow(totalInformation, 4);
    }

    /*-------------- Goal functions --------------*/

    /**
     * Checks if it's a goal solution state.
     *
     * @return Is a goal state.
     */
    boolean isGoal() {
        return false;
    }

    /*-------------- Auxiliary classes and functions --------------*/

    /**
     * Representation of a sensor input and output connections.
     */
    private class SensorConnections {

        private Integer outputSensor;
        private List<Integer> inputSensors;

        SensorConnections(Integer outputSensor, List<Integer> inputSensors) {
            this.outputSensor = outputSensor;
            this.inputSensors = inputSensors;
        }
    }

    /**
     * Calculates the Euclidean distance between two sensors.
     *
     * @param first   First sensor id.
     * @param sensor1 First sensor with its coordinates and capacity.
     * @param sensor2 Second sensor with its coordinates and capacity.
     * @return Distance cost.
     */
    private Double calculateCost(int first, Sensor sensor1, Sensor sensor2) {
        return Math.sqrt((sensor1.getCoordX() - sensor2.getCoordX()) * (sensor1.getCoordX() - sensor2.getCoordX())
                + (sensor1.getCoordY() - sensor2.getCoordY()) * (sensor1.getCoordY() - sensor2.getCoordY()))
                * (sensor1.getCapacidad() + Math.min(sensor1.getCapacidad(), recursiveCalculateInformation(sensorConnections.get(first).inputSensors)));
    }

    /**
     * Calculates  the Euclidean distance between a sensor and a data center.
     *
     * @param first      Sensor id.
     * @param sensor     Sensor with its coordinates and capacity.
     * @param dataCenter Data center with its coordinates
     * @return Distance cost.
     */
    private Double calculateCostDataCenters(int first, Sensor sensor, Centro dataCenter) {
        return Math.sqrt((sensor.getCoordX() - dataCenter.getCoordX()) * (sensor.getCoordX() - dataCenter.getCoordX())
                + (sensor.getCoordY() - dataCenter.getCoordY()) * (sensor.getCoordY() - dataCenter.getCoordY()))
                * (sensor.getCapacidad() + Math.min(sensor.getCapacidad(), recursiveCalculateInformation(sensorConnections.get(first).inputSensors)));
    }

    /**
     * Calculates information cost between two sensors.
     *
     * @return Information cost.
     */
    private Double calculateInformation() {
        Double totalInformation = 0D;
        for (int i = sensorList.size(); i < sensorConnections.size(); i++) {
            List<Integer> inputSensors = sensorConnections.get(i).inputSensors;
            totalInformation += recursiveCalculateInformation(inputSensors);
        }
        return totalInformation;
    }

    /**
     * Recursive calculation of input sensors information.
     *
     * @param inputSensors List of input sensors to iterate.
     * @return Accumulated information value for each input sensor.
     */
    private Double recursiveCalculateInformation(List<Integer> inputSensors) {
        Double information = 0D;
        for (Integer sensorID : inputSensors) {
            Double capacity = sensorList.get(sensorID).getCapacidad();
            List<Integer> nextInputSensors = sensorConnections.get(sensorID).inputSensors;
            if (inputSensors.isEmpty()) {
                information += capacity;
            } else {
                information += capacity + Math.min(2 * capacity, recursiveCalculateInformation(nextInputSensors));
            }
        }
        return information;
    }

    /**
     * Checks if a a connection between two sensors can be established.
     *
     * @param first  Sensor from that will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If the connection can be performed.
     */
    private boolean isAllowedConnection(int first, int second) {
        Integer maxConnections = second < sensorList.size() ? MAX_SENSOR_CONNECTIONS : MAX_DATA_CENTER_CONNECTIONS;
        if (sensorConnections.get(first).outputSensor != null || sensorConnections.get(second).inputSensors.size() >= maxConnections) {
            return false;
        }

        // Already connected second -> first (cycle)
        Integer nextSensor = sensorConnections.get(second).outputSensor;
        while (nextSensor != null) {
            if (nextSensor == first) {
                return false;
            }
            nextSensor = sensorConnections.get(nextSensor).outputSensor;
        }

        return true;
    }

    /**
     * Gets the problem size.
     *
     * @return Number of sensors.
     */
    int getSensorsSize() {
        return sensorList.size();
    }

    int getProblemSize() {
        return sensorList.size() + centerList.size();
    }

    /**
     * Gets problem distance cost.
     *
     * @return Cost of the current configuration.
     */
    private Double getTotalCost() {
        return totalCost;
    }

    /**
     * Gets problem information cost.
     *
     * @return Information value of the current configuration.
     */
    private Double getTotalInformation() {
        return totalInformation;
    }

}
