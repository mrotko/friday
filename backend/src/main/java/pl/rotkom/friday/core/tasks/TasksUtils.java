package pl.rotkom.friday.core.tasks;

import java.time.Duration;

public class TasksUtils {

    public static int toMin(Duration duration) {
        return (int) Math.ceil(duration.getSeconds() / 60.);
    }

}
