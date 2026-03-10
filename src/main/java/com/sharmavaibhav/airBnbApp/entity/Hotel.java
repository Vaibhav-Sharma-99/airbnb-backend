package com.sharmavaibhav.airBnbApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table
public class Hotel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<Room> rooms;

    @Column(nullable = false)
    private String name;

    private String city;

    @Column(columnDefinition = "TEXT[]")  // postgres can store array in cols but we need to tell it to jpa also
    // what dataType of column to create it doesnt auto-infer in this case is all
    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    private boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    @Embedded
    private HotelContactInfo contactInfo;
//    contact_info_address
//    contact_info_phoneNumbers

    @ManyToOne(optional = false)
    private User owner;

}
