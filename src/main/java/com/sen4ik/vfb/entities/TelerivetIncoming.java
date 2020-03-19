package com.sen4ik.vfb.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TelerivetIncoming {

    private String event;
    private String id;
    private String message_type;
    private String content;
    private String from_number;
    private String from_number_e164;
    private String to_number;
    private Integer time_created;
    private Integer time_sent;
    private String contact_id;
    private String phone_id;
    private String service_id;
    private String project_id;
    private String secret;

}
