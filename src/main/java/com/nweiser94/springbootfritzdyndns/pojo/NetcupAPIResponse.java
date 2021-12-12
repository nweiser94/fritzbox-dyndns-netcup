package com.nweiser94.springbootfritzdyndns.pojo;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;
import java.util.Optional;

/**
 * POJO for the nectup ccp api response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NetcupAPIResponse {
    private String serverrequestid;
    private String clientrequestid;
    private String action;
    private String status;
    private int statuscode;
    private String shortmessage;
    private String longmessage;
    private Map<String, Object> responsedata;


    /**
     * Gets a data value from the reponsedata map. Value can be not present if an error has occured.
     * @param key The key of the value to fetch.
     * @return {@link Optional} of the value.
     */
    public Optional<Object> getResponseDataValue(String key) {
        return responsedata != null ? Optional.ofNullable(responsedata.get(key)) : Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("action[%s]-serverrequestid[%s]-clientrequestid[%s]-status[%s]-data[%s]", action, serverrequestid, clientrequestid, status, responsedata);
    }
}
