package cn.pangm.beijingbus.common;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by pangm on 15-6-6.
 */
public class Station {
    private UnfoldingMap map;
    private Location loc;

    private Route route;
    private Connection connection;
    private float r = 5;
    PApplet ctx;

    public Station(PApplet ctx, UnfoldingMap map) {
        this.ctx = ctx;
        this.map = map;
        this.connection = null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void receiveBus(ScreenBus bus) {
        fire(bus);
        bus.setCurCnt(ctx.floor(ctx.random(0, 50)));
    }

    public void display() {
        ctx.strokeWeight(0.5f);
        // Brightness is mapped to sum
        float b = ctx.map(r, 5, 15, 100, 0);
        ctx.fill(46, 160, 45, b);
        ctx.stroke(46, 160, 45);
        PVector pos = map.getScreenPosition(loc);
        ctx.ellipse(pos.x, pos.y, r, r);

        // Size shrinks down back to original dimensions
        r = ctx.lerp(r, 5, 0.1f);
    }

    private void fire(ScreenBus bus) {
        r = 15;

        if (connection != null){
            connection.receiveBus(bus);
        } else {
            route.buses.remove(bus);
        }
    }
}
