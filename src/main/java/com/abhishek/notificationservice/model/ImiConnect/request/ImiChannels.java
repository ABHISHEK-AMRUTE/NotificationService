package com.abhishek.notificationservice.model.ImiConnect.request;


import lombok.Data;

@Data
public class ImiChannels {
    private ImiSmsBody sms;
    public ImiChannels(){
        this.sms = new ImiSmsBody();
    }
}
