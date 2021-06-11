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
package com.ibm.bcs.ift.ift_authenticate.exceptions;

import com.ibm.bcs.ift.ift_authenticate.responses.InternalErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends RuntimeException {
    private InternalErrorResponse response;

    public InternalServerErrorException(InternalErrorResponse response) {
        super(response.getMessage());
        this.response = response;
    }

    public InternalErrorResponse getResponse() {
        return this.response;
    }
}
