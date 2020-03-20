package com.sen4ik.vfb.entities;

import com.sen4ik.vfb.base.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Entity
@Table(name = "contacts", schema = Constants.SCHEMA)
@NoArgsConstructor
@Getter
@Setter
public class Contact {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Basic
    @Column(name = "name")
    @NotEmpty(message = "Please provide a name")
    private String name;

    @Basic
    @Column(name = "phone_number")
    @NotEmpty(message = "Please provide a phone number")
    private String phoneNumber;

    @Basic
    @Column(name = "bible_translation")
    @NotEmpty(message = "Please provide a bible translation")
    private String bibleTranslation;

    @Basic
    @Column(name = "added_on", updatable = false)
    @CreationTimestamp
    @ColumnDefault("CURRENT_TIMESTAMP")
    private Timestamp addedOn;

    @Basic
    @Column(name = "subscription_confirmed")
    private Byte subscriptionConfirmed = 0;

    @Basic
    @Column(name = "selected_send_time")
    private Double selectedSendTime;

    @Basic
    @Column(name = "selected_time_zone")
    private String selectedTimeZone;

    @Basic
    @Column(name = "selected_send_time_pacific")
    private Double selectedSendTimePacific;

}
