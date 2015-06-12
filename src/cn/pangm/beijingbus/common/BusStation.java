package cn.pangm.beijingbus.common;

import de.fhpotsdam.unfolding.geo.Location;

/**
 * Created by pangm on 6/3/15.
 */
public class BusStation {
    private String lineNum;
    private int seqNum;
    private int type;
    private Location location;

    public BusStation(String line, int type, int seqNum, Location location) {
        this.lineNum = line;
        this.type = type;
        this.seqNum = seqNum;
        this.location = location;
    }

    public BusStation() {

    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
