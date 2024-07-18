package de.rub.bi.inf.nativelib;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class ThreeDTester extends JFrame {
    private static final List<Path2D.Double> shapes = new LinkedList<>();
    private static final List<Point2D.Double> points = new LinkedList<>();
    private static ThreeDTester instance;

    public static void addPoint(Point2D.Double p) {
        points.add(p);
        if (instance != null)
            instance.repaint();
    }

    public static void addShape(Path2D.Double shape) {
        shapes.add(shape);
        if (instance != null)
            instance.repaint();
    }

    public ThreeDTester() {
        super("test");
        setVisible(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        pack();
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        instance = this;
    }

    @Override
    public void paint(Graphics g) {
        final var g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        for (var i = 0; i < shapes.size(); i++) {
            var shape = shapes.get(i);
            g2d.draw(shape);
        }

        for (int i = 0; i < points.size(); i++) {
            var point = points.get(i);
            g2d.fillOval(Math.round((float) point.x), Math.round((float) point.y), 2, 2);
        }
    }
}
