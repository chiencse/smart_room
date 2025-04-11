package com.example.smart_room.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.FirebaseApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    private final DatabaseReference databaseReference;

    @Autowired
    public FirebaseService(FirebaseApp firebaseApp) {
        this.databaseReference = FirebaseDatabase.getInstance(firebaseApp).getReference("sensor_data");
    }

    public void saveData(String sensorId, double value) {
        databaseReference.push().setValueAsync("Sensor: " + sensorId + ", Value: " + value);
    }
}