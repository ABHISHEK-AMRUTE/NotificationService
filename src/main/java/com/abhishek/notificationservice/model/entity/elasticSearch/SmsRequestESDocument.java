package com.abhishek.notificationservice.model.entity.elasticSearch;

import com.abhishek.notificationservice.controller.SmsRequestController;
import com.abhishek.notificationservice.model.entity.mysql.SmsRequest;
import com.abhishek.notificationservice.utils.enums.SmsStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Document(indexName = "smsrequest")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequestESDocument {
    @Id
    private String id;
    @Field( type = FieldType.Long)
    private Long smsRequestId;

    @Field( type = FieldType.Text)
    private String phoneNumber = "2897498273498";

    @Field( type = FieldType.Text)
    private String message="placeholder";

    @Field( type = FieldType.Text)
    private SmsStatusEnum status = SmsStatusEnum.FAILED;

    @Field( type = FieldType.Text)
    private String failure_code;

    @Field( type = FieldType.Text)
    private String failure_comments;

    @Field( type = FieldType.Date)
    private Date createdAt;

    @Field( type = FieldType.Date)
    private Date updatedAt;

    public SmsRequestESDocument(SmsRequest message) {
        this.smsRequestId = message.getId();
        this.phoneNumber = message.getPhoneNumber();
        this.message = message.getMessage();
        this.status = message.getStatus();
        this.failure_code = message.getFailure_code();
        this.failure_comments = message.getFailure_comments();
        this.createdAt = message.getCreated_at();
        this.updatedAt = message.getUpdated_at();
    }

}
