package com.abhishek.notificationservice.repository;

import com.abhishek.notificationservice.util.enums.PhoneNumberStatusEnum;
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

   public void savePhoneNumber(String phoneNumberRedis){
      hashOperations.put("PhoneNumber", phoneNumberRedis , PhoneNumberStatusEnum.WHITELISTED );
   }

   public void blackListPhoneNumber(String phoneNumberRedis){
      hashOperations.put("PhoneNumber", phoneNumberRedis , PhoneNumberStatusEnum.BLACKLISTED );
   }

   public void whiteListPhoneNumber(String phoneNumberRedis){
      hashOperations.put("PhoneNumber", phoneNumberRedis , PhoneNumberStatusEnum.WHITELISTED );
   }

   public PhoneNumberStatusEnum getPhoneNumberStatus( String phoneNumber ){
      return (PhoneNumberStatusEnum) hashOperations.get("PhoneNumber", phoneNumber);
   }

}
