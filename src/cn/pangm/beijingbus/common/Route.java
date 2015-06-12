package cn.pangm.beijingbus.common;

import de.fhpotsdam.unfolding.UnfoldingMap;
import processing.core.PApplet;

import java.util.ArrayList;

/**
 * Created by pangm on 15-6-6.
 */
public class Route {
    private UnfoldingMap map;
    ArrayList<Station> stations;
    ArrayList<Connection> connections;
    ArrayList<ScreenBus> buses;
    PApplet ctx;

    public Route(PApplet ctx, UnfoldingMap map) {
        stations = new ArrayList<Station>();
        connections = new ArrayList<Connection>();
        buses = new ArrayList<ScreenBus>();
        this.ctx = ctx;
        this.map = map;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public void addConnection(Connection con) {
        connections.add(con);
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void receiveBus() {
        Station startStation = stations.get(0);

        ScreenBus bus = new ScreenBus();
        bus.setCurCnt(ctx.floor(ctx.random(0, 50)));
        bus.setLoc(startStation.getLoc());

        startStation.receiveBus(bus);
        this.buses.add(bus);
    }

    public void update() {
        for (Connection cnn : connections) {
            cnn.update();
        }
    }

    public void display() {
        for (Station station : stations) {
            station.display();
        }

        for (Connection c : connections) {
            c.display();
        }
    }

    // We can connection two Neurons
    public void connect(Station a, Station b, float congestion) {
        Connection c = new Connection(ctx, map, a, b, congestion);
        a.setConnection(c);
        // Also add the Connection here
        connections.add(c);
    }

    public ArrayList<ScreenBus> getBuses() {
        return buses;
    }

    public void setBuses(ArrayList<ScreenBus> buses) {
        this.buses = buses;
    }
}
