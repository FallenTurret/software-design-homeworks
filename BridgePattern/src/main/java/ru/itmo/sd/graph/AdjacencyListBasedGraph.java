package ru.itmo.sd.graph;

import ru.itmo.sd.drawing.DrawingApi;

import java.util.ArrayList;
import java.util.List;

public class AdjacencyListBasedGraph extends Graph {
    private final List<List<Integer>> edges;

    public AdjacencyListBasedGraph(int size, DrawingApi drawingApi) {
        super(size, drawingApi);
        edges = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            edges.add(new ArrayList<>());
        }
    }

    @Override
    public void addEdge(int v, int u) {
        if (v >= size || u >= size || v < 0 || u < 0) {
            throw new IllegalArgumentException("One of given indices does not represent any vertex in the graph");
        }
        edges.get(v).add(u);
        edges.get(u).add(v);
    }

    @Override
    protected List<Edge> getEdgesToDraw() {
        var listOfEdges = new ArrayList<Edge>();
        for (int v = 0; v < size; v++) {
            for (int u: edges.get(v)) {
                if (u > v) {
                    listOfEdges.add(new Edge(v, u));
                }
            }
        }
        return listOfEdges;
    }
}
