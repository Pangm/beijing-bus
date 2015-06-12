package cn.pangm.beijingbus.common;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PVector;

/**
 * Created by pangm on 15-6-9.
 */
public class ScreenBus {
    private Location loc;
    private Connection connection;
    private int curCnt;        // the number of people

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc = loc;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int getCurCnt() {
        return curCnt;
    }

    public void setCurCnt(int curCnt) {
        this.curCnt = curCnt;
    }
}
