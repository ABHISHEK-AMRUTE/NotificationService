package com.abhishek.notificationservice.model.ImiConnect.response;

import lombok.Data;

@Data
public class ImiConnectResponse {
    // Used Object because the ImiConnect can have response as ArrayList or just a ImiResponseBody
    private Object response;
}
