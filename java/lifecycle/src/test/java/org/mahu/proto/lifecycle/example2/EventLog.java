package org.mahu.proto.lifecycle.example2;

import java.util.LinkedList;
import java.util.List;

public class EventLog {

    static public enum Event {
        start, stop, abort, event
    };

    static public class LogEntry {
        public final Event event;
        public final Class<?> cls;

        LogEntry(final Event event, final Object object) {
            this(event, object.getClass());
        }

        public LogEntry(final Event event, final Class<?> cls) {
            this.event = event;
            this.cls = cls;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof LogEntry) {
                LogEntry le = LogEntry.class.cast(o);
                return le.event.equals(event) && le.cls.equals(cls);
            }
            return false;
        }
        
        @Override
        public String toString() {
            return event +":" + cls.getName();
        }
    }

    private final static List<LogEntry> log = new LinkedList<>();

    public static void log(Event event, Object obj) {
        synchronized (log) {
            log.add(new LogEntry(event, obj));
        }
    }

    public static void clear() {
        synchronized (log) {
            log.clear();
        }
    }

    public static int size() {
        synchronized (log) {
            return log.size();
        }
    }

    public static LogEntry get(int i) {
        synchronized (log) {
            return log.get(i);
        }
    }

}
