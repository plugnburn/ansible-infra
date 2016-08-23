#!/bin/bash
SCRDIR=$(dirname $(realpath $0))
source $SCRDIR/env_prep.sh
ansible-playbook --ssh-extra-args="-o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no" --key-file=$KEYFILE -i ./ec2.py "$@"