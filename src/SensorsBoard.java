import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.util.ArrayList;
import java.util.List;

public class SensorsBoard {

    private Integer totalCost;
    private Integer totalInformation;

    private List<Sensor> sensorList;
    private List<Centro> centroList;

    private List<List<Integer>> connections;

    public SensorsBoard() {
        sensorList = new ArrayList<>();
        centroList = new ArrayList<>();
        connections = new ArrayList<>();
    }

    private void generateBoard() {

        // TODO: Random
        sensorList.addAll(new Sensores(100, 123));

        // TODO: Random
        centroList.addAll(new CentrosDatos(4, 123));
    }

    private Integer calculateCost(Sensor sensor1, Sensor sensor2) {
        return (sensor1.getCoordX() - sensor2.getCoordX()) * (sensor1.getCoordX() - sensor2.getCoordX())
                + (sensor1.getCoordY() - sensor2.getCoordY()) * (sensor1.getCoordY() - sensor2.getCoordY());
    }

    private Integer calculateInformation(Sensor sensor1, Sensor sensor2) {
        return 0;
    }

    private void generateInitialState() {
        for (int i = 0; i < sensorList.size() - 1; i++) {
            connections.get(i).add(i + 1);
            totalCost += calculateCost(sensorList.get(i), sensorList.get(i + 1));
            totalInformation += calculateInformation(sensorList.get(i), sensorList.get(i + 1));
        }
        connections.get(sensorList.size() - 1).add(-1);
    }

    /*---- OPERATORS ----*/

    public void switchConnection(int sensor, int oldConnection, int newConnection) {
        for (int i : connections.get(sensor)) {
            if (i == oldConnection) {
                connections.get(sensor).set(i, newConnection);
                break;
            }
        }
    }

    /*---- HEURISTICS ----*/

    public Integer costHeuristic() {
        return totalCost;
    }

    public Integer informationHeuristic() {
        return totalInformation;
    }

    public Integer superHeuristic() {
        return totalCost / totalInformation;
    }

}