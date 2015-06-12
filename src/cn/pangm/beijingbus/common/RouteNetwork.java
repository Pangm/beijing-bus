package cn.pangm.beijingbus.common;

import java.util.ArrayList;

/**
 * Created by pangm on 15-6-6.
 */
public class RouteNetwork {
    // The Route Network has a list of route
    ArrayList<Route> routes;

    ArrayList<Station> stations;

    public RouteNetwork() {
        routes = new ArrayList<Route>();
        stations = new ArrayList<Station>();
    }


}
