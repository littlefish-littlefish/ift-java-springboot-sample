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
package com.ibm.bcs.ift.ift_authenticate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
