package de.rub.bi.inf.nativelib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;

public class ThreeDTester extends JFrame {
    private static final List<Shape> shapes = new LinkedList<>();
    private static final List<Point2D.Double> points = new LinkedList<>();
    private static final List<Shape> clonedShapes = new LinkedList<>();
    private int offsetX = 0;
    private int offsetY = 0;
    private float scale = 1.0f; // Start-Skalierung
    private int dragStartX;
    private int dragStartY;
    private int mouseX;
    private int mouseY;

    public static void addPoint(Point2D.Double p) {
        points.add(p);
    }

    public static void removePoint(Point2D.Double p) {
        points.remove(p);
    }

    public static void addShape(Shape shape) {
        shapes.add(shape);
    }

    public static void removeShape(Shape shape) {
        shapes.remove(shape);
    }

    public ThreeDTester() {
        super("test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(2000, 1000);
        setLocationRelativeTo(null);

//        addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                clonedShapes.clear();
//                offsetX = Math.round((e.getX() - dragStartX) / scale);
//                offsetY = Math.round((e.getY() - dragStartY) / scale);
//                AffineTransform transform = new AffineTransform();
//                transform.translate(e.getX() - dragStartX, e.getY() - dragStartY);
//                clonedShapes.addAll(shapes.stream().map(transform::createTransformedShape).toList());
//                repaint();
//            }
//
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                mouseX = e.getX();
//                mouseY = e.getY();
//
//                repaint();
//            }
//
//
//        });
//
//        // Mouse wheel listener for zoom
        addMouseWheelListener(e -> {
            if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
                scale += (e.getWheelRotation() * 0.1f);
                scale = Math.max(0.1f, scale); // Minimum scale of 0.1
                repaint();
            }
        });
//
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dragStartX = getX();
                dragStartY = getY();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
//
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        //g2d.setStroke(new BasicStroke(2.0f)); // Set the stroke to 2px
        //g2d.scale(15, 15); // Apply scaling


        try {
            // Draw shapes with offset
            final var transform = new AffineTransform();
            transform.translate(-20 * scale, 250 * scale);
            transform.scale(scale, scale);
            for (Shape shape : shapes.stream().map(transform::createTransformedShape).toList()) {
                g2d.draw(shape);
            }

            // Draw points with offset
            for (Point2D.Double point : points) {
                g2d.fillOval(
                        Math.round((float) point.x) + offsetX,
                        Math.round((float) point.y) + offsetY,
                        2, 2
                );
            }
        } catch (ConcurrentModificationException e) {
            // ignore me
        }

        // Display coordinates at mouse position
        g2d.setColor(Color.RED);
//        g2d.drawString(
//                "X: " + (int) ((mouseX - offsetX) / scale) + ", Y: " + (int) ((mouseY - offsetY) / scale),
//                mouseX, mouseY
//        );
    }
}
