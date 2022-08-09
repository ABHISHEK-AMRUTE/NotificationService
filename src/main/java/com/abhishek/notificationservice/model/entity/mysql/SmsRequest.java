package com.abhishek.notificationservice.model.entity.mysql;

import com.abhishek.notificationservice.util.enums.SmsStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data // using for all the boilers (constructors, getter and setters)
@Entity // To make this class an Entity
@Table(name="smsRequests") // the custom table name for our database
/**
 * This will serve as a model/entity for our mysql database
 */
public class SmsRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String phoneNumber;

    private String message;

    private SmsStatusEnum status;

    private String failure_code;

    private String failure_comments;

    @CreatedDate
    private Date created_at;

    @LastModifiedDate
    private Date updated_at;

}
