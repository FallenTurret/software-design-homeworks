package ru.itmo.sd;

import ru.itmo.sd.drawing.AwtDrawer;
import ru.itmo.sd.drawing.DrawingApi;
import ru.itmo.sd.drawing.JavaFxDrawer;
import ru.itmo.sd.graph.AdjacencyListBasedGraph;
import ru.itmo.sd.graph.AdjacencyMatrixBasedGraph;
import ru.itmo.sd.graph.Graph;

import java.util.Random;

public class Main {
    private static final int SIZE = 10;
    private static final String TITLE = "Graph";
    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2)
            return;
        DrawingApi drawingApi;
        Graph graph;
        if ("awt".equals(args[0])) {
            drawingApi = new AwtDrawer(TITLE);
        } else {
            drawingApi = JavaFxDrawer.getInstance(TITLE);
        }
        if ("matrix".equals(args[1])) {
            graph = new AdjacencyMatrixBasedGraph(SIZE, drawingApi);
        } else {
            graph = new AdjacencyListBasedGraph(SIZE, drawingApi);
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (RANDOM.nextBoolean()) {
                    graph.addEdge(i, j);
                }
            }
        }
        graph.drawGraph();
    }
}
