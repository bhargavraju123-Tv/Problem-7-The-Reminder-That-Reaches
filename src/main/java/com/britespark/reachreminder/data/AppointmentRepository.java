package com.britespark.reachreminder.data;

import com.britespark.reachreminder.domain.Appointment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();

    public void save(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> findAll() {
        return Collections.unmodifiableList(appointments);
    }

    public int count() {
        return appointments.size();
    }
}