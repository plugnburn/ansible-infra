#!/bin/bash

source ../env_prep.sh
DYNINV="./ec2.py"
INSTFILE="tomcat/tomcat_instances"
CURINSTANCECOUNT="$(<$INSTFILE)"
INSTANCECOUNT="$1"

function run_playbook() {
  ansible-playbook --ssh-extra-args="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" --key-file=$KEYFILE -i $DYNINV "$@"
}

# count instances in dynamic inventory by role: count_instances $role
function count_instances() {
  local role="$1"
  local count=$($DYNINV --refresh-cache | grep -Pzo "\"tag_role_${role}\"(\n|.)*?\]" | head -n -1 | tail -n +2 | wc -l)
  echo -n $count
}

if (( INSTANCECOUNT > CURINSTANCECOUNT )); then
  run_playbook tomcat/tomcat-instance-scale-up.yml --extra-vars "@inputs.yaml" --extra-vars "backends=$INSTANCECOUNT"
  echo "Waiting for new Tomcat instances to scale up..."
  c=CURINSTANCECOUNT
  while (( c <  INSTANCECOUNT )); do
    sleep 5
    c=$(count_instances tomcat)
  done
  echo "Instances scaled up, continuing with Tomcat deployment..."
  run_playbook tomcat/tomcat-scale-up.yml --extra-vars "@inputs.yaml" --extra-vars "backends=$INSTANCECOUNT"
else
  run_playbook tomcat/tomcat-instance-scale-down.yml --extra-vars "@inputs.yaml" --extra-vars "backends=$INSTANCECOUNT"
  echo "Waiting for Tomcat instances to scale down..."
  c=CURINSTANCECOUNT
  while (( c >  INSTANCECOUNT )); do
    sleep 5
    c=$(count_instances tomcat)
  done
  echo "Instances scaled down, continuing with HAProxy reconfiguration..."
fi

run_playbook tomcat/tomcat-scale-finish.yml --extra-vars "@inputs.yaml"

echo -n $INSTANCECOUNT > $INSTFILE