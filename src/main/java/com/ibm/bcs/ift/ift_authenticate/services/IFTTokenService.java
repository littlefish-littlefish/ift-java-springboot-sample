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
package com.ibm.bcs.ift.ift_authenticate.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.bcs.ift.ift_authenticate.beans.CloudIAMToken;
import com.ibm.bcs.ift.ift_authenticate.beans.IFTServiceToken;
import com.ibm.bcs.ift.ift_authenticate.config.ApplicationConfiguration;
import com.ibm.bcs.ift.ift_authenticate.handlers.IFTErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Service
public class IFTTokenService {

    //--------------------------------------------------------
    // Autowireds
    //--------------------------------------------------------

    private ApplicationConfiguration appConfig;

    private CloudIAMToken cloudIAMToken = null;
    private IFTServiceToken iftServiceToken = null;

    public IFTTokenService(@Autowired ApplicationConfiguration appConfig) {

        this.appConfig = appConfig;

        obtainTokens();

    }

    /**
     * Calls the IBM Cloud IAM Service with the api key in the application config
     * and obtains a token. This token is then used to call IBM Food Trust to exchange it
     * for a valid IFT service token. This is the token that must be supplied to all data
     * submission calls to IFT.
     */
    public void obtainTokens() {

        // Multiple/frequent token requests will be denied. Only request a new IBM Cloud IAM token if
        // one does not already exist or if it is no longer valid.
        if (this.cloudIAMToken == null || !isTokenValid()) {

            // Get the IBM Cloud IAM Token
            setCloudIAMToken(obtainCloudIAMToken());

            // Pass the IBM Cloud IAM Token to IFT to exchange it for a service token
            if (this.cloudIAMToken != null) {
                setIFTServiceToken(obtainIFTServiceToken());
            }
        }
    }

    /**
     * Performs a REST API call to the IBM Cloud IAM service to obtain a Token.
     *
     * @return CloudIAMToken An IBM Cloud IAM Token
     */
    private CloudIAMToken obtainCloudIAMToken() {

        //Safety net... this should never happen because this is a private method.
        if (cloudIAMToken == null) {
            //set the header(s)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            //create the form data
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "urn:ibm:params:oauth:grant-type:apikey");
            map.add("apikey", appConfig.getApiKey());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
            RestTemplate restTemplate = restTemplateBuilder.errorHandler(new IFTErrorHandler()).build();

            //pass the apikey from the config file to the IBM Cloud IAM service to retrieve a token
            cloudIAMToken = restTemplate.postForObject(appConfig.getIftCloudIAMURL(), request, CloudIAMToken.class);

            return cloudIAMToken;

        } else {
            return this.cloudIAMToken;
        }

    }

    /**
     * Performs a REST API call to exchange the IBM Cloud IAM service for an
     * IBM Food Trust token. This token is required for all data submission calls to IFT.
     *
     * @return IFTServiceToken An IBM Food Trust Service Token
     */
    private IFTServiceToken obtainIFTServiceToken() {

        if (iftServiceToken == null) {
            //Safety net... this should never happen because this is a private method.
            if (cloudIAMToken == null || !isTokenValid()) {
                // This method was called out of order... a Cloud IAM Token is a required parameter for the exchange.
                obtainCloudIAMToken();
            }

            //set the header(s)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            //create the data
            try {
                ObjectMapper mapper = new ObjectMapper();  //for converting Java object to JSON
                HttpEntity<String> iam_token = new HttpEntity<>(mapper.writeValueAsString(cloudIAMToken), headers);

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            //build the url
            String url = appConfig.getIftAuthURL() + appConfig.getIftOrgId();

            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
            RestTemplate restTemplate = restTemplateBuilder.errorHandler(new IFTErrorHandler())
                    .build();
            //make the Rest Call
            iftServiceToken = restTemplate.postForObject(url, cloudIAMToken, IFTServiceToken.class);

            return iftServiceToken;

        } else {
            return this.iftServiceToken;
        }
    }

    /**
     * Checks the expiration time of the Cloud IAM token and returns TRUE if it
     * has not expired yet.
     *
     * @return boolean TRUE or FALSE
     */
    public boolean isTokenValid() {

        if (cloudIAMToken != null) {
            long curr_time = System.currentTimeMillis() / 1000l;
            long exp_time = new Long(cloudIAMToken.getExpiration());

            if (exp_time > (curr_time - 30)) { //give a 30 second buffer
                return TRUE;
            }
        }

        return FALSE;
    }

    //------------------------------------------------------------------------------------
    // GETTERS AND SETTERS
    //------------------------------------------------------------------------------------
    public CloudIAMToken getCloudIAMToken() {
        return cloudIAMToken;
    }

    private void setCloudIAMToken(CloudIAMToken cloudIAMToken) {
        this.cloudIAMToken = cloudIAMToken;
    }

    public IFTServiceToken getIFTServiceToken() {
        return iftServiceToken;
    }

    private void setIFTServiceToken(IFTServiceToken iftServiceToken) {
        this.iftServiceToken = iftServiceToken;
    }
}
