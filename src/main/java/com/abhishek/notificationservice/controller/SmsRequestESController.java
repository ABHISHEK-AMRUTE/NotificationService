package com.abhishek.notificationservice.controller;
import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestElastic;
import com.abhishek.notificationservice.service.impl.SmsRequestElasticServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class SmsRequestESController {
    private SmsRequestElasticServiceImpl smsRequestElasticService;

    public SmsRequestESController(SmsRequestElasticServiceImpl smsRequestElasticService) {
        this.smsRequestElasticService = smsRequestElasticService;
    }

    @GetMapping("/v1/smsRequest")
    public List<SmsRequestElastic> getSmsRequestsByText( @RequestParam String textQuery, @RequestParam int page, @RequestParam int pageSize ){
        return  smsRequestElasticService.findById(textQuery, page, pageSize);
    }

    @GetMapping("/v1/smsRequest/phoneNumber")
    public List<SmsRequestElastic> getSmsRequestByPhoneNumber(@RequestParam String phoneNumber, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateFrom,  @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date dateTo ){
        return smsRequestElasticService.findByPhoneNumber( phoneNumber, dateFrom, dateTo);
    }

}
