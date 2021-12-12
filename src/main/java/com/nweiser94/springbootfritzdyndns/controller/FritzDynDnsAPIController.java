package com.nweiser94.springbootfritzdyndns.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.nweiser94.springbootfritzdyndns.exception.APICommunicationException;
import com.nweiser94.springbootfritzdyndns.pojo.APIError;
import com.nweiser94.springbootfritzdyndns.pojo.DNSRecord;
import com.nweiser94.springbootfritzdyndns.service.NetcupCCPAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST API for the fritz box dyndns functionality.
 */
@RestController
@RequestMapping("/")
public class FritzDynDnsAPIController {

    @Autowired
    private NetcupCCPAPIService netcupCCPAPIService;

    @GetMapping
    @CrossOrigin("*")
    public ResponseEntity updateDynamicDNSRecords(@RequestParam String ipv4, @RequestParam String ipv6, @RequestParam String domain, @RequestParam String hostname) throws JsonProcessingException, APICommunicationException {

        Optional<Object> apisessionid = netcupCCPAPIService.login();
        if(!apisessionid.isPresent()) {
            return ResponseEntity.internalServerError().build();
        }
        final String sessionId = (String) apisessionid.get();
        List<DNSRecord> dnsRecords = netcupCCPAPIService.fetchDNSRecords(domain, sessionId);

        if(dnsRecords.isEmpty()) {
            return ResponseEntity.internalServerError().build();
        }
        netcupCCPAPIService.updateDNSRecords(sessionId, dnsRecords, ipv4, ipv6, hostname, domain);
        return ResponseEntity.ok().build();
    }


    @ExceptionHandler({APICommunicationException.class})
    public ResponseEntity handleException(APICommunicationException ex) {
        final String message = ex.getMessage();
        final APIError error = new APIError(message);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }










}
