package com.abhishek.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsSuccessResponse {
    private String requestId;
    private String comments;
}
