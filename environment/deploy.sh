#!/bin/bash

source ../env_prep.sh
DYNINV="./ec2.py"

function run_playbook() {
  ansible-playbook --ssh-extra-args="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" --key-file=$KEYFILE -i $DYNINV "$@"
}

ansible-playbook infra-setup.yml
sleep 3
echo -n 3 > tomcat/tomcat_instances
$DYNINV > /dev/null
run_playbook env.yml --extra-vars "@inputs.yaml"