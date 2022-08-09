package com.abhishek.notificationservice.model.entity.mysql;

import com.abhishek.notificationservice.util.enums.PhoneNumberStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

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
    // default value is whitelisted
    private PhoneNumberStatusEnum status = PhoneNumberStatusEnum.WHITELISTED;
}
