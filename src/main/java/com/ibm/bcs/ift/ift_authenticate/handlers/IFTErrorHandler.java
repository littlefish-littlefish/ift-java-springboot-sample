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
package com.ibm.bcs.ift.ift_authenticate.handlers;

import com.ibm.bcs.ift.ift_authenticate.exceptions.InternalServerErrorException;
import com.ibm.bcs.ift.ift_authenticate.responses.InternalErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Component
public class IFTErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse)
            throws IOException {
        return (
                httpResponse.getStatusCode().series() == CLIENT_ERROR
                        || httpResponse.getStatusCode().series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse)
            throws IOException {

        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
            // handle SERVER_ERROR
            throw new InternalServerErrorException(new InternalErrorResponse(getMessage(httpResponse)));

        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            // handle CLIENT_ERROR
            throw new InternalServerErrorException(new InternalErrorResponse(getMessage(httpResponse)));
        }
    }

    private String getMessage(ClientHttpResponse response) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(response.getBody()));
        String line = null;
        String message = "";
        while ((line = in.readLine()) != null) {
            message += line;
        }

        return message;
    }
}
