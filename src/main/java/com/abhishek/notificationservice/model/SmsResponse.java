package com.abhishek.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsResponse {
    private String requestId;
    private String comments;
}
