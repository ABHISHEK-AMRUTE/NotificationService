package com.abhishek.notificationservice.model.ImiConnect.request;

import lombok.Data;

@Data
public class ImiConnectRequest {

    private String deliveryChannel;

    private ImiChannels channels;

    private ImiDestination destinations;

    public ImiConnectRequest(){
        this.deliveryChannel = "sms";
        this.channels = new ImiChannels();
        this.destinations = new ImiDestination();
    }
}
