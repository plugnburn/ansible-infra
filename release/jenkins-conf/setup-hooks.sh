CURHOST="$(hostname)"
PROJECTNAME="$1"
GITLABURL="$2"
GITLABTOKEN="$3"
TARGETURL="${GITLABURL}api/v3/projects/root%2f${PROJECTNAME}/hooks?access_token=${GITLABTOKEN}"
JENKINSURL="http://$CURHOST/"
curl -s -H 'Content-Type:application/json' --data-ascii "{\"url\":\"${JENKINSURL}project/${PROJECTNAME}-master\",\"push_events\":true,\"merge_requests_events\":false,\"enable_ssl_verification\":false}" "$TARGETURL" > /dev/null
curl -s -H 'Content-Type:application/json' --data-ascii "{\"url\":\"${JENKINSURL}project/${PROJECTNAME}-pr\",\"push_events\":false,\"merge_requests_events\":true,\"enable_ssl_verification\":false}" "$TARGETURL" > /dev/null
touch /tmp/hooks-set-up