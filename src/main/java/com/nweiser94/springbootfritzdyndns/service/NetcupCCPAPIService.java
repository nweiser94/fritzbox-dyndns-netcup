package com.nweiser94.springbootfritzdyndns.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nweiser94.springbootfritzdyndns.exception.APICommunicationException;
import com.nweiser94.springbootfritzdyndns.pojo.DNSRecord;
import com.nweiser94.springbootfritzdyndns.pojo.NetcupAPIResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.nweiser94.springbootfritzdyndns.pojo.NetcupAPIRequest.*;

/**
 * Service for the netcup CCP api communication.
 */
@Slf4j
@Service
public class NetcupCCPAPIService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${NETCUP_API_KEY}")
    private String netcupApiKey;

    @Value("${NETCUP_API_PASSWORD}")
    private String netcupApiPassword;

    @Value("${NETCUP_CUSTOMER_NUMBER}")
    private String customerNumber;

    @Value("${NETCUP_API_URL}")
    private String netcupAPIUrl;


    /**
     *  Executes the login action for the netcup ccp api. As a result the api returns the apisessionid for further communication.
     *  @return {@link Optional} of the apisessionid.
     *  @throws JsonProcessingException Thrown in case the request build failed.
     */
    public Optional<Object> login() throws JsonProcessingException, APICommunicationException {
       final HttpEntity httpRequest = Builder()
               .withActionType("login")
               .withParam(API_KEY, netcupApiKey)
               .withParam(API_PASSWORD, netcupApiPassword)
               .withParam(CUSTOMER_NUMBER, customerNumber)
               .buildAsHTTPEntity();
       final NetcupAPIResponse response = excecuteRequest(httpRequest, "login");
       return response.getResponseDataValue("apisessionid");

    }

    /**
     * Updates the dns records with the provided hostname and ip adresses from the fritzbox. If no entries are present
     * new Records are generated with the given information.
     * @param sessionid The api session id.
     * @param dnsRecords The list of  dns records.
     * @param ipv4 The IPv4 address of the fritz box.
     * @param ipv6 The IPv6 address of the fritz box.
     * @param hostname The hostname to update the records for.
     * @param domain The name of the domain to update the records for.
     * @throws JsonProcessingException Thrown in case the request build failed.
     */
    public void updateDNSRecords(@NonNull String sessionid, @NonNull List<DNSRecord> dnsRecords, @NonNull String ipv4, @NonNull String ipv6, @NonNull String hostname, @NonNull String domain) throws JsonProcessingException, APICommunicationException {
        final Optional<DNSRecord> IPv4Record = dnsRecords.stream().filter(r -> matchingIPv4Record(r, hostname)).findAny();
        final Optional<DNSRecord> IPv6Record = dnsRecords.stream().filter(r -> matchingIPv6Record(r, hostname)).findAny();
        final DNSRecord ipv4DNS = IPv4Record.map(r -> {
            r.setDestination(ipv4);
            return r;
        }).orElse(buildNewIPv4Record(ipv4, hostname, "A"));
        final DNSRecord ipv6DNS = IPv6Record.map(r -> {
            r.setDestination(ipv6);
            return r;
        }).orElse(buildNewIPv4Record(ipv6, hostname, "AAAA"));
        final List<DNSRecord> updatedRecords =  Arrays.asList(ipv4DNS, ipv6DNS);
        final Map<String, Object> dnsrecordset = new HashMap<>();
        dnsrecordset.put("dnsrecords", updatedRecords);
        final HttpEntity httpRequest = Builder()
                .withActionType("updateDnsRecords")
                .withParam(API_KEY, netcupApiKey)
                .withParam(API_PASSWORD, netcupApiPassword)
                .withParam(CUSTOMER_NUMBER, customerNumber)
                .withParam(DOMAINNAME, domain)
                .withParam(API_SESSION_ID, sessionid)
                .withParam(DNS_RECORD_SET, dnsrecordset)
                .buildAsHTTPEntity();
        excecuteRequest(httpRequest, "updateDnsRecords");
    }

    private DNSRecord buildNewIPv4Record(String ip, String hostname, String type) {
        DNSRecord record = new DNSRecord();
        record.setDestination(ip);
        record.setHostname(hostname);
        record.setType(type);
        return record;
    }

    private boolean matchingIPv4Record(@NonNull DNSRecord records, @NonNull String hostname) {
        return hostname.equals(records.getHostname()) && "A".equals(records.getType());
    }

    private boolean matchingIPv6Record(@NonNull DNSRecord records, @NonNull String hostname) {
        return hostname.equals(records.getHostname()) && "AAAA".equals(records.getType());
    }


    private NetcupAPIResponse excecuteRequest(HttpEntity httpRequest, String action) throws APICommunicationException, JsonProcessingException {
        ResponseEntity<String> entity = restTemplate.postForEntity(netcupAPIUrl, httpRequest, String.class);
        if(entity.getStatusCode().isError()) {
            log.error("Error while {} acton. Reason: {}", action, entity.getStatusCode().getReasonPhrase());
            throw new APICommunicationException(String.format("Error while %s acton. Reason: %s", action, entity.getStatusCode().getReasonPhrase()));
        }
        if(entity.getBody() == null) {
            log.error("Error while {} acton. Response is incomplete.", action);
            throw new APICommunicationException(String.format("Error while %s action. Reason: Response is incomplete", action));
        }
        final String apiResponse =  entity.getBody();
        final NetcupAPIResponse response = objectMapper.readValue(apiResponse, NetcupAPIResponse.class);
        if(response.getStatus().equals("error")){
            log.error("Error while {} action. Reason: {}",action, response.getLongmessage());
            throw new APICommunicationException(String.format("Error while %s action. Reason: %s",action, response.getLongmessage()));
        }
        if(response.getResponsedata() == null){
            log.error("Error while {} action. Reason: did not receive response data",action);
            throw new APICommunicationException(String.format("Error while %s action. Reason: did not receive response data",action));
        }
        log.debug("Succesful api response from netcup - {}", response);
        return response;
    }


    /**
     *  Fetches the existing dns records for the given domain.
     * @param domain The name of the domain.
     * @param sessionid The active apisessionid.
     * @return The list of dns records.
     * @throws JsonProcessingException Thrown if the request build failed.
     */
    public List<DNSRecord> fetchDNSRecords(@NonNull String domain, @NonNull String sessionid) throws JsonProcessingException, APICommunicationException {
        final HttpEntity httpRequest = Builder()
                .withActionType("infoDnsRecords")
                .withParam(API_KEY, netcupApiKey)
                .withParam(API_PASSWORD, netcupApiPassword)
                .withParam(CUSTOMER_NUMBER, customerNumber)
                .withParam(DOMAINNAME, domain)
                .withParam(API_SESSION_ID, sessionid)
                .buildAsHTTPEntity();

        final NetcupAPIResponse response = excecuteRequest(httpRequest, "infoDnsRecords");
        return response.getResponseDataValue("dnsrecords")
                .map(o -> (List<LinkedHashMap<String, Object>>) o)
                .stream()
                .flatMap(Collection::stream)
                .map(DNSRecord::new)
                .collect(Collectors.toList());
    }


}
