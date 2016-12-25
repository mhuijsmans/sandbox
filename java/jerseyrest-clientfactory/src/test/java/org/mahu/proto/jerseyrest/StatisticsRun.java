package org.mahu.proto.jerseyrest;

public class StatisticsRun {

    private final long startTime = now();
    // private final long usedMemoryStart;
    private long endTime;
    // private long usedMemoryEnd;
    private final String name;
    private final int nrOfRuns;

    StatisticsRun(final String name, final int nrOfRuns) {
        this.name = name;
        this.nrOfRuns = nrOfRuns;
        this.endTime = 0;
        // this.usedMemoryStart = calculateUsedMemory();
    }

    void ready() {
        if (endTime == 0) {
            endTime = now();
            // usedMemoryEnd = calculateUsedMemory();
        }
    }

    String getName() {
        return name;
    }

    long getElpasedTimeInMs() {
        return endTime - startTime;
    }

    int getNrOfRuns() {
        return nrOfRuns;
    }

    // long getMemortyIncrease() {
    // return usedMemoryEnd - usedMemoryStart;
    // }

    private final static long now() {
        return System.currentTimeMillis();
    }

    private static long calculateUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        return usedMemoryBefore;
    }
}
