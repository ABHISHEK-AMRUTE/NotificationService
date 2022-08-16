package com.abhishek.notificationservice.model.ImiConnect.request;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ImiConnectRequest {

    private String deliverychannel;

    private ImiChannels channels;

    private List<ImiDestination> destination;

    public ImiConnectRequest(){
        this.deliverychannel = "sms";
        this.channels = new ImiChannels();
        this.destination = Collections.singletonList(new ImiDestination());
    }
}
