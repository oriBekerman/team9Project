package il.cshaifasweng.OCSFMediatorExample.client;

import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Map;

public class TimerManager {
    private static TimerManager instance;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, Future<?>> timers = new HashMap<>(); // Store timers with unique names

    private TimerManager() {} // Private constructor for Singleton

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    // Start a new timer with a unique name
    public void startTimer(String timerName, Runnable action, int minutes) {
        cancelTimer(timerName); // Cancel existing timer with same name (if any)

        System.out.println("Timer started: " + timerName + " (" + minutes + " minutes)");

        // Schedule the timeout action
        Future<?> timeoutTask = scheduler.schedule(() -> {
            System.out.println("Time ran out for: " + timerName);
            action.run(); // Execute the function when time expires
            timers.remove(timerName); // Remove after execution
        }, minutes, TimeUnit.MINUTES);

        timers.put(timerName, timeoutTask);
    }

    // Cancel a timer by name
    public void cancelTimer(String timerName) {
        if (timers.containsKey(timerName)) {
            timers.get(timerName).cancel(true);
            timers.remove(timerName);
            System.out.println("Timer cancelled: " + timerName);
        }
    }

    // Check if a timer exists
    public boolean hasTimer(String timerName) {
        return timers.containsKey(timerName);
    }
}
