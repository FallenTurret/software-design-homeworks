package ru.itmo.sd.drawing;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class AwtDrawer extends Frame implements DrawingApi {
    private final BufferedImage canvas;

    public AwtDrawer(String title) {
        canvas = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setPaint(Color.white);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
        setSize(1366, 768);
        setTitle(title);
        setVisible(true);
    }

    @Override
    public long getDrawingAreaWidth() {
        return canvas.getWidth();
    }

    @Override
    public long getDrawingAreaHeight() {
        return canvas.getHeight();
    }

    @Override
    public void drawCircle(double x, double y, double r) {
        Graphics2D g = canvas.createGraphics();
        g.setPaint(Color.green);
        g.fill(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        repaint();
    }

    @Override
    public void drawLine(double x1, double y1, double x2, double y2) {
        Graphics2D g = canvas.createGraphics();
        g.setPaint(Color.black);
        g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(canvas, 0, 0, this);
    }
}

