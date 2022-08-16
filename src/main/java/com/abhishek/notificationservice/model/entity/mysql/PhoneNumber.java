package com.abhishek.notificationservice.model.entity.mysql;

import com.abhishek.notificationservice.utils.enums.PhoneNumberStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name="phoneNumbers")
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumber {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long Id;
    private String phoneNumber;
    private PhoneNumberStatusEnum status = PhoneNumberStatusEnum.WHITELISTED;
}
