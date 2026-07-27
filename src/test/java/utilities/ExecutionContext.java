package utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ExecutionContext {

    private static final String EXECUTION_TIMESTAMP =
            LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

    private ExecutionContext() {
        // Prevent object creation
    }

    public static String getExecutionTimestamp() {
        return EXECUTION_TIMESTAMP;
    }
}