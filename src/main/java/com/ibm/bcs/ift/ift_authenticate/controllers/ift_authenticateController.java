/**
 *********************************************************************
 * Licensed Materials - Property of IBM
 * Restricted Materials of IBM
 * 5900-A1Y
 *
 * © Copyright IBM Corp. 2018 All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *********************************************************************
 */
package com.ibm.bcs.ift.ift_authenticate.controllers;

import com.ibm.bcs.ift.ift_authenticate.beans.CloudIAMToken;
import com.ibm.bcs.ift.ift_authenticate.beans.IFTServiceToken;
import com.ibm.bcs.ift.ift_authenticate.config.ApplicationConfiguration;
import com.ibm.bcs.ift.ift_authenticate.exceptions.InternalServerErrorException;
import com.ibm.bcs.ift.ift_authenticate.handlers.IFTErrorHandler;
import com.ibm.bcs.ift.ift_authenticate.responses.InternalErrorResponse;
import com.ibm.bcs.ift.ift_authenticate.services.IFTTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ift_authenticateController {
    //--------------------------------------------------------
    // Autowireds
    //--------------------------------------------------------
    @Autowired
    private ApplicationConfiguration appConfig;

    @Autowired
    private IFTTokenService iftTokenService;

    /**
     * This is a wrapper service to the IBM Food Trust Connector API. It automates the authentication process
     * required before data submission.
     *
     * @param originalXML The XML document to be posted to IBM Food Trust.
     * @return The response from the IBM Food Trust Connector API.
     */
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String responseBody(
            @RequestBody
                    String originalXML
    ) {
        String response = "";

        if (appConfig.getApiKey() != null) {

            // The IBM Cloud IAM Token is obtained and is valid for a specified period of time, the details of which
            // are returned with the token. Once obtained, an IFT service token can be obtained for the same time span.

            response = submitDocToIFT(iftTokenService.getCloudIAMToken(), iftTokenService.getIFTServiceToken(), originalXML);

        } else {
            throw new InternalServerErrorException(new InternalErrorResponse("No API key specified."));
        }

        return response;
    }

    /**
     * Performs a REST API call to submit the IFT Service Token with an XML message to IFT.
     *
     * @param cloudIAMToken   IBM Cloud IAM Service Token
     * @param iftServiceToken IBM Food Trust Service Token
     * @param xml             the XML document to be posted to IBM Food Trust
     * @return JSON response from the IFT API call
     */
    private String submitDocToIFT(CloudIAMToken cloudIAMToken, IFTServiceToken iftServiceToken, String xml) {
        String response = "";

        // if the cloud IAM Token doesn't exist or expires in the very near future (less than 30
        // seconds), obtain a new token.
        if (cloudIAMToken == null
                || iftServiceToken == null
                || !iftTokenService.isTokenValid()) {

            // get a new IFT service token
            iftTokenService.obtainTokens();
        }

        //--------------------------------------------------------------------------------------
        // Submit the XML payload to IFT
        //--------------------------------------------------------------------------------------
        //set the header(s)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);

        List<MediaType> acceptList = new ArrayList<MediaType>();
        acceptList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(acceptList);

        try {
            String bearer = "Bearer " + iftServiceToken.getOnboarding_token();
            headers.set(HttpHeaders.AUTHORIZATION, bearer);

        } catch (Exception e) {
            throw new InternalServerErrorException(new InternalErrorResponse("[ERROR - Unable to read IFT Service Token] " + e.getMessage()));
        }

        //set the XML payload
        HttpEntity payload = new HttpEntity<>(xml, headers);

        //build the url
        String url = appConfig.getIftURL();

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate;

        restTemplate = restTemplateBuilder.errorHandler(new IFTErrorHandler())
                .build();

        //submit the data
        response = restTemplate.postForObject(url, payload, String.class);

        return response;
    }
}