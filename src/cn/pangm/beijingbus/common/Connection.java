package cn.pangm.beijingbus.common;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

/**
 * Created by pangm on 15-6-6.
 */
public class Connection {
    private UnfoldingMap map;
    //connection is from bus station A to bus station B;
    Station from; // A
    Station to; // B

    PApplet ctx;

    ArrayList<ScreenBus> buses;

    // the trafic jamming degree
    float congestion;

    public Connection(
            PApplet cxt,
            UnfoldingMap map,
            Station from,
            Station to,
            float congestion) {
        this.ctx = cxt;
        this.map = map;
        this.from = from;
        this.to = to;
        this.congestion = congestion;
        buses = new ArrayList<ScreenBus>();
    }

    public void receiveBus(ScreenBus bus) {
        bus.setConnection(this);
        bus.setLoc(from.getLoc());
        // TODO : init the bus.
        buses.add(bus);
    }

    void update() {
        ArrayList<ScreenBus> busToDelete = new ArrayList<ScreenBus>();
        if (!buses.isEmpty()) {
            for (ScreenBus bus : buses) {
                Location loc = new Location(0, 0);
                float v = 0.1f;

                loc.setLat(ctx.lerp(bus.getLoc().getLat(),
                        to.getLoc().getLat(),
                        v));
                loc.setLon(ctx.lerp(bus.getLoc().getLon(),
                        to.getLoc().getLon(),
                        v));

                bus.setLoc(loc);

                float d = PVector.dist(map.getScreenPosition(bus.getLoc()),
                        map.getScreenPosition(to.getLoc()));

                if (d < 2) {
                    // the bus go to next connection
                    //buses.remove(bus);
                    busToDelete.add(bus);
                    to.receiveBus(bus);
                }
            }
            buses.removeAll(busToDelete);
        }
    }

    // Draw line and bus
    void display() {
        ctx.stroke(0);
//        ctx.strokeWeight(1 + congestion * 4);
        ctx.strokeWeight(1);

        PVector posFrom = map.getScreenPosition(from.getLoc());
        PVector posTo = map.getScreenPosition(to.getLoc());

        ctx.stroke(46, 160, 45, 80);
//        ctx.fill(46, 160, 45, 20);
        ctx.strokeWeight(3);
        ctx.line(posFrom.x, posFrom.y,
                posTo.x, posTo.y);

        if (!buses.isEmpty()) {
            for (ScreenBus bus : buses) {
//                ctx.fill(240);
//                ctx.strokeWeight(1);
//                PVector pos = map.getScreenPosition(bus.getLoc());
//                ctx.ellipse(pos.x, pos.y, 16, 16);
                ctx.fill(240);
                ctx.stroke(240);
                ctx.strokeWeight(5);
                PVector pos = map.getScreenPosition(bus.getLoc());

                float len = 0.2f;
                PVector tailPos = new PVector();
                PVector direction = PVector.sub(posTo, posFrom);
                direction.normalize();
                PVector dis = PVector.mult(direction, 10);
//                tailPos.x = ctx.lerp(pos.x, posFrom.x, len);
//                tailPos.y = ctx.lerp(pos.y, posFrom.y, len);
                tailPos = PVector.sub(pos, dis);
                ctx.line(tailPos.x, tailPos.y,
                        pos.x, pos.y);
            }
        }
    }
}
