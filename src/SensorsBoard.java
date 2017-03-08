import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SensorsBoard {

    private int totalCost;
    private double totalInformation;

    private static List<Sensor> sensorList;
    private static List<Centro> centroList;

    private int[] connections;

    public Integer getTotalCost() {
        return totalCost;
    }

    public Double getTotalInformation() {
        return totalInformation;
    }

    public SensorsBoard() {
        sensorList = new ArrayList<>();
        centroList = new ArrayList<>();

        generateBoard();
        connections = new int[sensorList.size()];

        totalCost = 0;
        totalInformation = 0D;

        generateInitialState();
    }

    public SensorsBoard(SensorsBoard board) {
        connections = new int[board.getProblemSize()];
        System.arraycopy(board.connections, 0, connections, 0, board.connections.length);
        totalCost = board.getTotalCost();
        totalInformation = board.getTotalInformation();
    }

    private void generateBoard() {

        // TODO: Random
        sensorList.addAll(new Sensores(100, 123));

        // TODO: Random
        centroList.addAll(new CentrosDatos(4, 123));
    }

    private int calculateCost(Sensor sensor1, Sensor sensor2) {
        return (sensor1.getCoordX() - sensor2.getCoordX()) * (sensor1.getCoordX() - sensor2.getCoordX())
                + (sensor1.getCoordY() - sensor2.getCoordY()) * (sensor1.getCoordY() - sensor2.getCoordY());
    }

    private int calculateInformation(Sensor sensor1, Sensor sensor2) {
        return 1;
    }

    /*---- INITIAL STATES ----*/

    private void generateInitialState() {
        for (int i = 0; i < sensorList.size() - 1; i++) {
            connections[i] = i + 1;
            totalCost += calculateCost(sensorList.get(i), sensorList.get(i + 1));
            totalInformation += calculateInformation(sensorList.get(i), sensorList.get(i + 1));
        }
        connections[sensorList.size() - 1] = -1;
    }

    /*---- OPERATORS ----*/

    public void swapConnection(int index) {
        int aux = connections[index];

        if (connections[index] >= 0) {
            int oldCost = calculateCost(sensorList.get(index), sensorList.get(connections[index]));

            connections[index] = connections[(index + 1) % sensorList.size()];
            connections[(index + 1) % sensorList.size()] = aux;

            if (connections[index] >= 0) {
                int newCost = calculateCost(sensorList.get(index), sensorList.get(connections[index]));
                totalCost += (newCost - oldCost);
            }
        }
    }

    /*---- HEURISTICS ----*/

    public int costHeuristic() {
        return totalCost;
    }

    public double informationHeuristic() {
        return 1 / totalInformation;
    }

    public double superHeuristic() {
        return totalCost / totalInformation;
    }

    public boolean isGoal() {
        return false;
    }

    public int getProblemSize() {
        return sensorList.size();
    }

    public int[] getConnections() {
        return connections;
    }
}