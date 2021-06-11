# ift-java-springboot-sample

# Summary

This project contains a Java sample which demonstrates how to obtain IAM token and IFT token, and make a sample IFT API call. It is based on the Java sample in IFT documentation, which is a little out-of-date and does not work out-of-the-box. Refer to IFT documentation for details:
https://food.ibm.com/ift/docs/java-sample/

This project also contains a sample script which does similar things with curl commands. Refer to the script in bin directory for details.

# Updates

Updates to the Java sample in IFT documentation:
* Updated the dependencie versions in build.gradle
* Updated some dependency class names since they were changed in new version

# Configuration

Update the config.properties file before running the sample.
* iftEnvironment can be set to either 'INTEGRATION' (case insensitive) or some other string. When it is set to 'INTEGRATION', the corresponding configurations with 'Integration' as part of the name will be picked up, otherwise, the corresponding configuration with 'Production' as part of the name will be picked up.
* When iftEnvironment is set, only one set of URL properties (either Integration or Production) need to be set.
* Set iftApiKey attribute.
Refer to the config.properties file for sample settings.

# Steps to run the sample

Here are the steps to run the sample:
1. Download or clone the project
2. cd into the project directory
3. Update the configuration file at src/main/resources/config.properties as explained above
4. Execute command to build the project: 
gradle wrapper
5. Execute command to run the project as Sping Boot application: 
./gradlew bootRun
6. Post an XML document to localhost:8080, eg. 
curl -X POST http://localhost:8080  -H 'Accept: application/json'  -H 'Content-Type: application/xml'  -d 'your-xml-document-goes-here'
