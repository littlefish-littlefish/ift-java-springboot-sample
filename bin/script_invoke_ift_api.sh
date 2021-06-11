#!/bin/bash

# -----------------------------------------------------------------------------------------------------------------
# This is a sample script to use curl command to get IAM token and IFT service token, and make an IFT API call.
# Usage: ./test_api.sh API_KEY ORG_ID IFT_URL
# E.g. ./test_api.sh my_api_key cc946be4-fa1d-4952-ae3f-db0de07cdb2c sandbox.food.ibm.com
#
# Note: This script uses jq command to parse json, to install on mac: brew install jq
# -----------------------------------------------------------------------------------------------------------------

# First get command-line arguments
API_KEY=$1
ORG_ID=$2
IFT_URL=$3

# Obtain an IBM Cloud IAM token
iam_token=`curl --request POST "https://iam.cloud.ibm.com/identity/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--data-urlencode "grant_type=urn:ibm:params:oauth:grant-type:apikey" \
--data-urlencode "apikey=$API_KEY"`

# Exchange an IBM Cloud IAM token for a service token
ift_token=`curl --request POST "https://$IFT_URL/ift/api/identity-proxy/exchange_token/v1/organization/$ORG_ID" \
--header "Content-Type: application/json" \
--data-raw "$iam_token"`

# Extract the onboarding_token
ift_token=`echo $ift_token | jq -r '.onboarding_token'`

# Invoke a sample IFT API call
curl --request GET "https://$IFT_URL/ift/api/outbound/v2/products" --header "Authorization: Bearer $ift_token"
