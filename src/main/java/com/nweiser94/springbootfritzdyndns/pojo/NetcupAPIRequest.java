package com.nweiser94.springbootfritzdyndns.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class NetcupAPIRequest {

    public static String API_KEY = "apikey";
    public static String API_PASSWORD = "apipassword";
    public static String CUSTOMER_NUMBER = "customernumber";
    public static String API_SESSION_ID = "apisessionid";
    public static String DOMAINNAME = "domainname";
    public static String DNS_RECORD_SET = "dnsrecordset";

    private String action;
    private Map<String, Object> param;


    /**
     *
     * @return
     */
    public static RequestBuilder Builder() {
        return new RequestBuilder();
    }



}



