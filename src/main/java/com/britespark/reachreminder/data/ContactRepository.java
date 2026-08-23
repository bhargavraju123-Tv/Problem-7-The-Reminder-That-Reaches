package com.britespark.reachreminder.data;

import com.britespark.reachreminder.domain.Contact;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ContactRepository {

    private final Map<String, Contact> contacts = new HashMap<>();

    public void save(Contact contact) {
        contacts.put(contact.residentId(), contact);
    }

    public Contact findById(String residentId) {
        return contacts.get(residentId);
    }

    public Collection<Contact> findAll() {
        return contacts.values();
    }
}