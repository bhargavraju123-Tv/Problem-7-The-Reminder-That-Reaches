package com.britespark.reachreminder.data;

import com.britespark.reachreminder.domain.Appointment;
import org.springframework.stereotype.Repository;
import java.util.Collections;
import java.util.List;

@Repository
public class AppointmentRepository {
    public List<Appointment> findAll() {
        return Collections.emptyList(); // Stub for now
    }
}