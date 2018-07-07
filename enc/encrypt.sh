#!/bin/bash

set -e

# Echoes extra logging info
log() {
  if [ "$VERBOSE" = true ]
    then
      echo "--${1}"
  fi
}

# Checks if an env value is present and not empty
checkEnv() {
  echo "Checking"
  env_value=$(printf '%s\n' "${!1}")
  if [ -z ${env_value} ]; then
    echo "$1 is undefined, exiting..."
    exit 1
  else
    log "Found value for $1"
  fi
}

checkEnv LEKKIE_ENCRYPT_KEY

echo "Encrypting files"
openssl aes-256-cbc -a -md sha256 -in buildSrc/src/main/java/lekkie/KeyStore.kt -out enc/KeyStore.kt.aes -k $LEKKIE_ENCRYPT_KEY
openssl aes-256-cbc -a -md sha256 -in signing/release.keystore -out enc/release.keystore.aes -k $LEKKIE_ENCRYPT_KEY
openssl aes-256-cbc -a -md sha256 -in signing/play.json -out enc/play.json.aes -k $LEKKIE_ENCRYPT_KEY
openssl aes-256-cbc -a -md sha256 -in enc.properties -out enc/enc.properties.aes -k $LEKKIE_ENCRYPT_KEY
openssl aes-256-cbc -a -md sha256 -in app/google-services.json -out enc/google-services.json.aes -k $LEKKIE_ENCRYPT_KEY
log "Files encrypted"

echo "Finishing up"