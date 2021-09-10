package ru.itmo.sd.graph;

import ru.itmo.sd.drawing.DrawingApi;

import java.util.List;

public abstract class Graph {
    protected final int size;
    private final DrawingApi drawingApi;

    public Graph(int size, DrawingApi drawingApi) {
        this.size = size;
        this.drawingApi = drawingApi;
    }

    public abstract void addEdge(int v, int u);

    protected abstract List<Edge> getEdgesToDraw();

    public final void drawGraph() {
        double centerY = (double)drawingApi.getDrawingAreaHeight() / 2;
        double centerX = (double)drawingApi.getDrawingAreaWidth() / 2;
        double ratio = Math.sqrt((double)2 / 15 * (1 - Math.cos(2 * Math.PI / size)));
        double R = Math.min(centerX, centerY) / (1 + ratio);
        double r = R * ratio;
        for (int i = 0; i < size; i++) {
            double angle = 2 * Math.PI * i / size;
            double x = centerX + R * Math.cos(angle);
            double y = centerY + R * Math.sin(angle);
            drawingApi.drawCircle(x, y, r);
        }

        var edges = getEdgesToDraw();
        for (var e: edges) {
            double angle1 = 2 * Math.PI * e.v / size;
            double angle2 = 2 * Math.PI * e.u / size;
            double x1 = centerX + R * Math.cos(angle1);
            double y1 = centerY + R * Math.sin(angle1);
            double x2 = centerX + R * Math.cos(angle2);
            double y2 = centerY + R * Math.sin(angle2);
            if (Math.abs(x1 - x2) < 1e-6) {
                if (y1 < y2) {
                    y1 += r;
                    y2 -= r;
                } else {
                    y1 -= r;
                    y2 += r;
                }
            } else if (Math.abs(y1 - y2) < 1e-6) {
                if (x1 < x2) {
                    x1 += r;
                    x2 -= r;
                } else {
                    x1 -= r;
                    x2 += r;
                }
            } else {
                ratio = Math.abs((y1 - y2) / (x1 - x2));
                double a = r / Math.sqrt(1 + ratio * ratio);
                double b = a * ratio;
                if (x1 < x2) {
                    x1 += a;
                    x2 -= a;
                } else {
                    x1 -= a;
                    x2 += a;
                }
                if (y1 < y2) {
                    y1 += b;
                    y2 -= b;
                } else {
                    y1 -= b;
                    y2 += b;
                }
            }
            drawingApi.drawLine(x1, y1, x2, y2);
        }
    }

    protected static final class Edge {
        public int v;
        public int u;
        Edge(int v, int u) {
            this.v = v;
            this.u = u;
        }
    }
}
