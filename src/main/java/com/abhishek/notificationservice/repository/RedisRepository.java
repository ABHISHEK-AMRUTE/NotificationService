package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.model.entity.redis.PhoneNumberRedis;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository

public class RedisRepository {
   private HashOperations hashOperations;
   private RedisTemplate redisTemplate;

   public RedisRepository(RedisTemplate redisTemplate) {
      this.hashOperations = redisTemplate.opsForHash();
      this.redisTemplate = redisTemplate;
   }

   public void savePhoneNumber(PhoneNumberRedis phoneNumberRedis){
      hashOperations.put("PhoneNumber", phoneNumberRedis.getPhoneNumber(),phoneNumberRedis);
   }

}
