package org.mahu.proto.jerseyrest;

import java.util.LinkedList;
import java.util.List;

public class Metrics {

    private final List<StatisticsRun> stat = new LinkedList<>();

    StatisticsRun create(final String name, final int count) {
        StatisticsRun t = new StatisticsRun(name, count);
        stat.add(t);
        return t;
    }

    public void printInfo() {
        for (StatisticsRun statRun : stat) {
            long avgInMicroSec = statRun.getElpasedTimeInMs() * 1000 / statRun.getNrOfRuns();
            System.out.println(statRun.getName() + " - totaltime(ms)=" + statRun.getElpasedTimeInMs()
                    + " avg(microSec)=" + avgInMicroSec);
        }

    }

}
