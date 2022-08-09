package com.abhishek.notificationservice.model;

import lombok.Data;

import java.util.List;

@Data
public class PhoneNumberPayload {
    private List<String> phoneNumbers;
}
