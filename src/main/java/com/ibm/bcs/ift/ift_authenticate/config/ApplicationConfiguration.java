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
package com.ibm.bcs.ift.ift_authenticate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:config.properties")
public class ApplicationConfiguration {

    private static String INTEGRATION = "INTEGRATION";

    @Value("${iftApiKey}")
    private String apiKey;

    @Value("${iftEnvironment}")
    private String iftEnv;

    // Org Id
    @Value("${iftIntegrationOrgId}")
    private String iftIntegrationOrgId;
    @Value("${iftProductionOrgId}")
    private String iftProductionOrgId;

    // Auth URl
    @Value("${iftIntegrationAuthURL}")
    private String iftIntegrationAuthURL;
    @Value("${iftProductionAuthURL}")
    private String iftProductionAuthURL;

    // Cloud IAM URL
    @Value("${iftCloudIAMURL}")
    private String iftCloudIAMURL;

    // IFT URL
    @Value("${iftIntegrationURL}")
    private String iftIntegrationURL;
    @Value("${iftProductionURL}")
    private String iftProductionURL;


    public ApplicationConfiguration() {
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getIftEnv() {
        return iftEnv;
    }

    public void setIftEnv(String iftEnv) {
        this.iftEnv = iftEnv;
    }

    public String getIftOrgId() {
        if (iftEnv.equalsIgnoreCase(INTEGRATION)) {
            return iftIntegrationOrgId;
        } else {
            return iftProductionOrgId;
        }
    }

    public void setIftIntegrationOrgId(String iftOrgId) {
        this.iftIntegrationOrgId = iftOrgId;
    }

    public void setIftProductionOrgId(String iftOrgId) {
        this.iftProductionOrgId = iftOrgId;
    }

    public String getIftAuthURL() {
        if (iftEnv.equalsIgnoreCase(INTEGRATION)) {
            return iftIntegrationAuthURL;
        } else {
            return iftProductionAuthURL;
        }
    }

    public void setIftIntegrationAuthURL(String iftAuthURL) {
        this.iftIntegrationAuthURL = iftAuthURL;
    }

    public void setIftProductionAuthURL(String iftAuthURL) {
        this.iftProductionAuthURL = iftAuthURL;
    }

    public String getIftCloudIAMURL() {
        return iftCloudIAMURL;
    }

    public void setIftCloudIAMURL(String iftCloudIAMURL) {
        this.iftCloudIAMURL = iftCloudIAMURL;
    }

    public String getIftURL() {
        if (iftEnv.equalsIgnoreCase(INTEGRATION)) {
            return iftIntegrationURL;
        } else {
            return iftProductionURL;
        }
    }

    public void setIftIntegrationURL(String iftURL) {
        this.iftIntegrationURL = iftURL;
    }

    public void setProductionURL(String iftURL) {
        this.iftProductionURL = iftURL;
    }

}
