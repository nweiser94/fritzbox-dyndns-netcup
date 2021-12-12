package com.nweiser94.springbootfritzdyndns.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DNSRecord {
    private String id;
    private String hostname;
    private String type;
    private String priority;
    private String destination;
    private boolean deleterecord;
    private String state;



    public DNSRecord(LinkedHashMap<String, Object> linkedHashMap) {
        this.id = (String) linkedHashMap.get("id");
        this.hostname = (String) linkedHashMap.get("hostname");
        this.type = (String) linkedHashMap.get("type");
        this.priority = (String) linkedHashMap.get("priority");
        this.destination = (String) linkedHashMap.get("destination");
        this.deleterecord = (boolean) linkedHashMap.get("deleterecord");
        this.state = (String) linkedHashMap.get("state");
    }

}
