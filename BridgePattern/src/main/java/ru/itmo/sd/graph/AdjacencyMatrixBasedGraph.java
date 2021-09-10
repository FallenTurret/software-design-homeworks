package ru.itmo.sd.graph;

import ru.itmo.sd.drawing.DrawingApi;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyMatrixBasedGraph extends Graph {
    private final boolean[][] matrix;

    public AdjacencyMatrixBasedGraph(int size, DrawingApi drawingApi) {
        super(size, drawingApi);
        matrix = new boolean[size][size];
    }

    @Override
    public void addEdge(int v, int u) {
        if (v >= size || u >= size || v < 0 || u < 0) {
            throw new IllegalArgumentException("One of given indices does not represent any vertex in the graph");
        }
        matrix[v][u] = true;
        matrix[u][v] = true;
    }

    @Override
    protected List<Edge> getEdgesToDraw() {
        var edges = new ArrayList<Edge>();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (matrix[i][j]) {
                    edges.add(new Edge(i, j));
                }
            }
        }
        return edges;
    }
}
