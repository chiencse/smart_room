package com.example.smart_room.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "strategy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column()
    private String description;

    @Column()
    private String status;

    @OneToMany(mappedBy = "strategy", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<StrategyDevice> strategyDevices;
}
