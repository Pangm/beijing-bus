package cn.pangm.beijingbus.common;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by pangm on 15-6-7.
 */
public class Path {
    ArrayList<PVector> points;
    ArrayList<BusStation> stations;
    Color color;

    PApplet cxt;
    UnfoldingMap map;
    float radius;

    public Path(
            PApplet cxt,
            UnfoldingMap map,
            ArrayList<BusStation> stations,
            Color color) {
        this.radius = 5;
        this.stations = stations;
        this.cxt = cxt;
        this.map = map;
        this.color = color;
        points = new ArrayList<PVector>();
        for (BusStation station : stations) {
            ScreenPosition v = map.getScreenPosition(station.getLocation());
            points.add(new PVector(v.x, v.y));
        }
    }

    // Add a point to the Path
    public void addPoint(float x, float y) {
        PVector point = new PVector(x, y);
        points.add(point);
    }

    public PVector getStart() {
        return points.get(0);
    }

    public PVector getEnd() {
        return points.get(points.size()-1);
    }


    // Draw the Path
    public void display() {
        points.clear();
        // Draw thick line for radius
        for (BusStation station : stations) {
            ScreenPosition v = map.getScreenPosition(station.getLocation());
            points.add(new PVector(v.x, v.y));
        }
        cxt.stroke((color.getRGB()));
        cxt.strokeWeight(radius * 2);
        cxt.noFill();
//        cxt.beginShape();
//        for (PVector v : points) {
//            cxt.vertex(v.x, v.y);
//        }
//        cxt.endShape();
//        // Draw thin line for center of Path
//        cxt.stroke(0);
//        cxt.strokeWeight(1);
//        cxt.noFill();
//        cxt.beginShape();
//        for (PVector v : points) {
//            cxt.vertex(v.x, v.y);
//        }
//        cxt.endShape();
    }

}
