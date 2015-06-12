package cn.pangm.beijingbus.client;

import cn.pangm.beijingbus.common.*;
import cn.pangm.beijingbus.utils.LocReformer;
import com.csvreader.CsvReader;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import org.gicentre.utils.stat.BarChart;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

import java.awt.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.List;

/**
 * Created by pangm on 15-5-27.
 */
public class BeijingBusClient extends PApplet {
    UnfoldingMap map;
//    Location beijingLocation = new Location(39.915156f, 116.403981f);
    Location beijingLocation = new Location(40.0f, 116.403981f);
    int initZoomLevel = 13;
    PShape busLogo;
    Image icon;
    int loadCnt = 0;
    boolean isLoadData = true;
    Path busPathA;
    Path busPathB;
    Bus bus;

    BarChart barChart;
    PFont titleFont,smallFont;

    ArrayList<BusStation> line944A;
    ArrayList<BusStation> line944B;

    Route route;

    public static void main(String[] agrs) {
        PApplet.main(new String[] { "cn.pangm.beijingbus.client.BeijingBusClient" });
    }

    public void setup() {
//        size(1000, 600, OPENGL);
        size(displayWidth, displayHeight, P2D);
        frame.setResizable(true);
        String mbTilesString = sketchPath("./data/map/Beijing-blue-8-16.mbtiles");

        map = new UnfoldingMap(this, new MBTilesMapProvider(mbTilesString));

        map.zoomToLevel(initZoomLevel);
        map.panTo(beijingLocation);
        map.setZoomRange(10, 16); // prevent zooming too far out
//        map.setPanningRestriction(beijingLocation, 50);
        smooth();

        MapUtils.createDefaultEventDispatcher(this, map);

        loadCnt = (int) (frameRate * 6);

        busLogo = loadShape("./data/bus_logo.svg");
        Toolkit tk = Toolkit.getDefaultToolkit();
        icon = tk.createImage("./data/bus-red-icon.png");
        this.frame.setTitle("Beijing Bus");
        this.frame.setIconImage(icon);

        // the chart bar
        titleFont = loadFont("Helvetica-22.vlw");
        smallFont = loadFont("Helvetica-12.vlw");
        textFont(smallFont);

        barChart = new BarChart(this);
        barChart.setBarColour(color(46, 160, 45, 100));
        barChart.setBarGap(2);
        barChart.setAxisLabelColour(color(200));
        barChart.setAxisColour(200);
        barChart.setAxisValuesColour(200);
        barChart.setValueFormat("人###,###");
        barChart.showValueAxis(true);
        barChart.showCategoryAxis(true);
        barChart.setMaxValue(50);
        barChart.setMinValue(0);
    }

    public void draw() {
        background(0);
        smooth();
        map.draw();

        if (loadCnt != 0 || isLoadData) {
            fill(255);
            rect(0, 0, width, height);
            shape(busLogo, width / 2 - width / 16f, height / 2 - 1.225f * width
                    / 16, width / 8f, 1.225f * width / 8);
            if (isLoadData) {
                try {
                    line944A = new ArrayList<BusStation>();
                    String path = "./data/data/944_0_fixed.csv";
                    loadData(line944A, path);

                    line944B = new ArrayList<BusStation>();
                    path = "./data/data/944_1_fixed.csv";
                    loadData(line944B, path);
                    isLoadData = false;

                    busPathA = new Path(this, map, line944A, new Color(46, 160, 45));
                    busPathB = new Path(this, map, line944B, new Color(255, 217, 14));

//                    bus = new Bus(this, busPathA.getStart().get(), 2f, 0.04f);

                    route = new Route(this, map);

                    for (BusStation s : line944A) {
                        Station station = new Station(this, map);

                        station.setRoute(route);
                        station.setLoc(s.getLocation());

                        route.addStation(station);
                    }

                    if (route.getStations().size() > 2) {
                        ArrayList<Station> stations = route.getStations();
                        Station cur = stations.get(0);
                        int index = 1;
                        while (index < stations.size()) {
                            Station last = cur;
                            cur = stations.get(index);

                            route.connect(last, cur, random(1, 4));

                            index++;

                        }
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            loadCnt--;
        } else {
//            colorMode(HSB);

            busPathA.display();

            if (frameCount % 60 == 0) {
                route.receiveBus();
            }

            route.update();
            route.display();

            float[] barValues = new float[route.getBuses().size()];
            int index = 0;
            for (ScreenBus bus : route.getBuses()) {
                barValues[index++] = bus.getCurCnt();
            }
            barChart.setData(barValues);

            float barWidth = 25 * route.getBuses().size() + 25;
            barChart.draw((width - barWidth) / 2, height * 11 / 16, barWidth, height / 4);
            //barChart.draw(width * 1/ 8, height * 11 / 16,width * 3 / 4, height / 4);
            fill(200);
            if (!route.getBuses().isEmpty()) {
                textFont(titleFont);
                textSize(15);
                text("公交路线 944",
                        (width - barWidth) / 2 + 45, height * 11 / 16);
                float textHeight = textAscent();
                textFont(smallFont);
                textSize(12);
                text("公交车上实时人数",
                        (width - barWidth) / 2 + 45, height * 11 / 16 + textHeight);
            }
        }
    }

    private void loadData(List list, String path) throws FileNotFoundException {
        loadData(list, path, false);
    }

    private void loadData(List list, String path, boolean isReform) throws FileNotFoundException {
        File stationFile = new File(path);
        InputStream stationInputStream = new FileInputStream(stationFile);

        CsvReader stationReader = new CsvReader(
                stationInputStream,
                ',',
                Charset.forName("gbk"));

        try {
            stationReader.readHeaders();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (stationReader.readRecord()) {
                String lineNum = stationReader.get("lineNum");
                int type = Integer.parseInt(stationReader.get("type"));
                int seqNum = Integer.parseInt(stationReader.get("seqNum"));
                float longitude = Float.parseFloat(stationReader.get("fixedlong"));
                float latitude = Float.parseFloat(stationReader.get("fixedlat"));
                Location loc;

                if (isReform) {
                    loc = LocReformer.reform(new Location(latitude, longitude));
                    System.out.print("Before: "
                                    + longitude
                                    + "---"
                                    + latitude
                    );

                    System.out.println("---After: "
                                    + loc.getLon()
                                    + "---"
                                    + loc.getLat()
                    );
                } else {
                    loc = new Location(latitude, longitude);
                }

                BusStation station = new BusStation(
                        lineNum,
                        type,
                        seqNum,
                        loc
                );

                list.add(station);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}