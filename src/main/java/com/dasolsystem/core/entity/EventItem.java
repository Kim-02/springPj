package com.dasolsystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id",updatable = false,nullable = false)
    private Long id;

    @Column(name="item_name",nullable = false)
    private String itemName;

    @Column(name="item_cost")
    private String itemCost;

}
