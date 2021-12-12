package com.nweiser94.springbootfritzdyndns.pojo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RequestBuilder {

    private String action;
    private Map<String, Object> param = new HashMap<>();
    private HttpHeaders headers = new HttpHeaders();


    /**
     *
     * @param action
     * @return
     */
    public RequestBuilder withActionType(String action) {
        this.action = action;
        return this;
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public RequestBuilder withParam(String key, Object value) {
        this.param.put(key, value);
        return this;
    }

    /**
     *
     * @return
     */
    public NetcupAPIRequest build() {
        return new NetcupAPIRequest(action, param);
    }

    /**
     *
     * @return
     * @throws JsonProcessingException
     */
    public String buildAsJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(build());
    }

    /**
     *
     * @return
     * @throws JsonProcessingException
     */
    public HttpEntity buildAsHTTPEntity() throws JsonProcessingException {
        final String jsonBody = buildAsJSON();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(jsonBody, headers);
    }
}
