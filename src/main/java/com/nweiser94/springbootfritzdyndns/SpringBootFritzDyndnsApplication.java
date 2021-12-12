package com.nweiser94.springbootfritzdyndns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBootFritzDyndnsApplication {

    @Value("${NETCUP_API_URL}")
    private String netcup_APIUrl;
    @Value("${FRITZ_USER}")
    private String fritzUser;
    @Value("${FRITZ_PASS}")
    private String fritzPass;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootFritzDyndnsApplication.class, args);
    }


    @Bean
    public RestTemplate netcupAPIRestTemplate() {
        return new RestTemplateBuilder().build();
    }

}
