package com.abhishek.notificationservice.model.entity.mysql;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="phoneNumbers")
public class PhoneNumber {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private String phoneNumber;

    private enum status { BLOCKED, UNBLOCKED }
}
