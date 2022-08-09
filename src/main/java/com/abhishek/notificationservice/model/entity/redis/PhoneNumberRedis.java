package com.abhishek.notificationservice.model.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@RedisHash("PhoneNumber")
public class PhoneNumberRedis {
    @Id
    private String phoneNumber;
    private String name;
    private enum status {
        BLOCKED,
        UNBLOCKED,
    }
}
