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
package com.ibm.bcs.ift.ift_authenticate.beans;

import java.io.Serializable;

public class IFTServiceToken implements Serializable {
    private String onboarding_token;

    public IFTServiceToken() {
    }

    public String getOnboarding_token() {
        return onboarding_token;
    }

    public void setOnboarding_token(String onboarding_token) {
        this.onboarding_token = onboarding_token;
    }
}
