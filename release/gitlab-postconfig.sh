#!/bin/bash
GITLABHOST="$(hostname)"
PROJECTNAME="$1"
WEBROOTPASS="$2"
PUBKEY="$3"

APITOKEN=$(curl -s -H 'Content-Type:application/json' --data-ascii "{\"grant_type\":\"password\",\"username\":\"root\",\"password\":\"${WEBROOTPASS}\"}" "http://${GITLABHOST}/oauth/token" | cut -d '"' -f 4)
curl -s -H 'Content-Type:application/json' --data-ascii "{\"name\":\"${PROJECTNAME}\",\"visibility_level\":10}" "http://${GITLABHOST}/api/v3/projects/?access_token=${APITOKEN}" > /dev/null
curl -s -H 'Content-Type:application/json' --data-ascii "{\"title\":\"Admin pubkey\",\"key\":\"${PUBKEY}\"}" "http://${GITLABHOST}/api/v3/user/keys/?access_token=${APITOKEN}" > /dev/null

echo $APITOKEN > /tmp/gitlab-postconfigured

USERTOKEN=$(curl -s -X POST "http://${GITLABHOST}/api/v3/session?login=root&password=${WEBROOTPASS}" | grep -Eoi '"private_token":".*"' | cut -d '"' -f 4)
echo $USERTOKEN > /tmp/gitlab-postconfigured-usertoken