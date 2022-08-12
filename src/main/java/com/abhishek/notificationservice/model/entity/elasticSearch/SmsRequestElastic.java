package com.abhishek.notificationservice.model.entity.elasticSearch;

import com.abhishek.notificationservice.utils.enums.SmsStatusEnum;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.Date;

@Document(indexName = "smsrequest")
@Setting(settingPath = "static/es-setting.json")
public class SmsRequestElastic {
    @Id
    @Field( type = FieldType.Long)
    private Long id;

    @Field( type = FieldType.Text)
    private String phoneNumber;

    @Field( type = FieldType.Text)
    private String message;

    @Field( type = FieldType.Text)
    private SmsStatusEnum status;

    @Field( type = FieldType.Text)
    private String failure_code;

    @Field( type = FieldType.Text)
    private String failure_comments;

    @CreatedDate
    private Date created_at;

    @LastModifiedDate
    private Date updated_at;
}
