package com.example.smart_room.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "device")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Status status = Status.OFF;

    @Column()
    private String deviceKey;

    @Column()
    private String location;

    @Column()
    private String ownerId;

    @Column()
    private String roomId;

    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL)
    @JsonManagedReference("device-strategy")
    private List<StrategyDevice> strategyDevices;


    public enum Status {
        ON,
        OFF,
        STANDBY
    }
}
