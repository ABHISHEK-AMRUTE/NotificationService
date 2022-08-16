package com.abhishek.notificationservice.model;

import com.abhishek.notificationservice.model.entity.elasticSearch.SmsRequestESDocument;
import lombok.Data;

import java.util.List;
@Data
public class ESSmsRequestResponse {
    private List<SmsRequestESDocument> data;
    private SmsErrorResponse error;
}
