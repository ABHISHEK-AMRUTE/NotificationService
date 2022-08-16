package com.abhishek.notificationservice.model;

import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsDetailsResponse {
    private SmsRequest data;
    private SmsErrorResponse error;
}
