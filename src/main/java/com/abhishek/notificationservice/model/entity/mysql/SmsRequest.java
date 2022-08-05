package com.abhishek.notificationservice.model.entity.mysql;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data // using for all the boilers (constructors, getter and setters)
@Entity // To make this class an Entity
@Table(name="smsRequests") // the custome table name for our database
/**
 * This will serve as a model/entity for our mysql database
 */
public class SmsRequest {

    @Id
    private Long id;

    private Long phoneNumber;

    private String message;

    private String failure_code;

    private String failure_comments;

    private Date created_at;

    private Date updated_at;

}
