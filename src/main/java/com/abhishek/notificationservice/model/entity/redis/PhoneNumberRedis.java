package com.abhishek.notificationservice.model.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("PhoneNumber")
public class PhoneNumberRedis implements Serializable {
    @Id
    private String phoneNumber;
    private String name;
    private enum status {
        BLOCKED,
        UNBLOCKED,
    }
}
