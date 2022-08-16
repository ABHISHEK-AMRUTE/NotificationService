package com.abhishek.notificationservice.model;

import lombok.Data;

@Data
public class SmsResponse {
    private SmsSuccessResponse data;
    private SmsErrorResponse error;
}
