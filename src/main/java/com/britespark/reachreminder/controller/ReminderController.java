package com.britespark.reachreminder.controller;

import com.britespark.reachreminder.service.ReminderOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderOrchestrator orchestrator;

    public ReminderController(ReminderOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping("/run")
    public ResponseEntity<String> runReminders() {
        System.out.println("Starting reminder run in the background...");

        // This triggers the engine in a new background thread!
        new Thread(() -> {
            orchestrator.runReminders();
        }).start();

        // This returns immediately so Postman doesn't timeout!
        return ResponseEntity.ok("Reminder run started in the background. Check console and outbox.jsonl for details.");
    }
}