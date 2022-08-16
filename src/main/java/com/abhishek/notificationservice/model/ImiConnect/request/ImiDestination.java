package com.abhishek.notificationservice.model.ImiConnect.request;

import lombok.Data;

import java.util.List;
@Data
public class ImiDestination {
    private List<String> msisdn;
    private String correlationId;
}
