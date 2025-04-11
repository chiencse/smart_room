package com.example.smart_room.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "strategy_device")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Use Long if using IDENTITY strategy

    @ManyToOne
    @JoinColumn(name = "strategy_id", referencedColumnName = "id")
    @JsonBackReference("strategy-device")
    private Strategy strategy;

    @ManyToOne
    @JoinColumn(name = "device_id", referencedColumnName = "id")
    @JsonBackReference("device-strategy")
    private Device device;

    @Column()
    private String value;
}
