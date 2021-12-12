package com.nweiser94.springbootfritzdyndns.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginData {
    private String apisessionid;
}
