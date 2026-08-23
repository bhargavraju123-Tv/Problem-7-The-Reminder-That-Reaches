package com.britespark.reachreminder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
@ConfigurationProperties(prefix = "reminder")
public class ReminderProperties {

    // Default values updated to overnight hours (11 PM to 3 AM)
    private LocalTime quietHoursStart = LocalTime.parse("23:00");
    private LocalTime quietHoursEnd = LocalTime.parse("03:00");
    private String pythonCommand = "python";

    public LocalTime getQuietHoursStart() { return quietHoursStart; }
    public void setQuietHoursStart(LocalTime quietHoursStart) { this.quietHoursStart = quietHoursStart; }

    public LocalTime getQuietHoursEnd() { return quietHoursEnd; }
    public void setQuietHoursEnd(LocalTime quietHoursEnd) { this.quietHoursEnd = quietHoursEnd; }

    public String getPythonCommand() { return pythonCommand; }
    public void setPythonCommand(String pythonCommand) { this.pythonCommand = pythonCommand; }
}