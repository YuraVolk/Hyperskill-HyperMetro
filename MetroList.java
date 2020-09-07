
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import data.MetroInputPoint;
import data.MetroStation;
import data.MetroStationList;
import data.MetroTransfer;
import parsing.MapToClassParser;
import parsing.MetroNameComparator;
import structure.Graph;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MetroList {
    private Map<String, Object> pointsMap = new LinkedHashMap<>();
    private MetroStationList metroStationList;

    private boolean fileExists = true;

    private void parseMapIntoClassSystem(Map<String, Object> map) {
        MapToClassParser parser = new MapToClassParser();
        metroStationList = parser.parse(map);
    }

    public MetroList(String filename) {
        try {

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map2 = new HashMap<String,Object>();
            map2 = mapper.readValue(String.join("", Files
                    .readAllLines(new File(filename).toPath())),
                    new TypeReference<HashMap>(){});

            parseMapIntoClassSystem(map2);
        } catch (IOException e) {
            System.out.println("err");
            e.printStackTrace();
            fileExists = false;
        }
    }

    public void createRoute(String station, String line, String targetStation, String targetLine, boolean print) {
        Graph graph = new Graph();

        Map<String, MetroStation> stationMap = metroStationList.getStationMap();


        for (Map.Entry<String, MetroStation> entry : stationMap.entrySet()) {
            for (Map.Entry<String, MetroInputPoint> pointEntry : entry.getValue().getValues().entrySet()) {
                List<String> next = pointEntry.getValue().getNextPoints();
                for (String nextPoint : next) {
                    graph.add(pointEntry.getValue().getName() + "~" + pointEntry.getValue().stationName,
                            nextPoint + "~" + pointEntry.getValue().stationName, pointEntry.getValue().time);
                }

                List<String> prev = pointEntry.getValue().getPreviousPoints();
                for (String prevPoint : prev) {
                    graph.add( prevPoint + "~" + pointEntry.getValue().stationName,
                            pointEntry.getValue().getName() + "~" + pointEntry.getValue().stationName,
                            pointEntry.getValue().time);
                }

                List<MetroTransfer> transfers = pointEntry.getValue().getTransfer();

                for (MetroTransfer transfer : transfers) {
                    graph.add(pointEntry.getValue().getName() + "~" + pointEntry.getValue().stationName,
                            transfer.getStation() + "~" + transfer.getLine(), 5);
                }
            }
        }


        List<String> strings = graph.getPath(line + "~" + station, targetLine + "~" + targetStation);


        int estimatedTime = graph.getResultDistance();
        for (int i = 0; i < strings.size(); i++) {
            if (i != 0) {
                if (!strings.get(i - 1).split("~")[1]
                        .equals(strings.get(i).split("~")[1])) {
                    System.out.println("Transition to line " + strings.get(i).split("~")[1]);
                } else {
                }
            }
            System.out.println(strings.get(i).split("~")[0]);
        }

        if (print) {
            System.out.printf("Total: %d minutes in the way\n", estimatedTime);
        }

    }


    public void printLine(String lineName) {
        Map<String, MetroInputPoint> pointMap = new TreeMap<>(new MetroNameComparator());
        pointMap.putAll(metroStationList.getStationMap().get(lineName).getValues());

        System.out.println("depot");

        for (Map.Entry<String, MetroInputPoint> entry : pointMap.entrySet()) {
            MetroInputPoint point = entry.getValue();
            if (point.getTransfer() == null) {
                System.out.println(point.getName());
            } else {
          /*      System.out.printf("%s - %s (%s line)\n", point.getName(),
                        point.getTransfer().getStation(), point.getTransfer().getLine());*/
            }
        }
        System.out.println("depot");
    }

    public void addNewStation(String lineName, String name, int time) {
        metroStationList.getStationMap().get(lineName).addStationToEnd(new MetroInputPoint(name, time));
    }

    public void addHeadStation(String lineName, String name, int time) {
        metroStationList.getStationMap().get(lineName).addStationToStart(new MetroInputPoint(name, time));
    }

    public void connectStation(String lineName, String stationName, String targetLine, String targetStation) {
        metroStationList.getStationMap().get(lineName).connect(stationName, targetLine, targetStation);
        metroStationList.getStationMap().get(targetLine).connect(targetStation, lineName, stationName);
    }

    public void removeStation(String lineName, String name) {
        metroStationList.getStationMap().get(lineName).removeStation(lineName, name);
    }
}
