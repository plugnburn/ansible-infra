#!/bin/bash
OUTFILE="$1"
echo "gitlab_root_password: $(pwgen -B 10 1)" > $OUTFILE
echo "jenkins_admin_password: $(pwgen -B 10 1)" >> $OUTFILE
echo "docker_admin_password: $(pwgen -B 10 1)" >> $OUTFILE
echo "docker_repo_user: admin" >> $OUTFILE
echo "gitlab_private_key_id: $(uuidgen)" >> $OUTFILE
echo "gitlab_user_token_id: $(uuidgen)" >> $OUTFILE
ssh-keygen -t rsa -C '' -q -N '' -f k1
PUBKEY="$(<k1.pub)"
PRIVKEY="$(<k1)"
echo "gitlab_public_key: $PUBKEY" >> $OUTFILE
echo "gitlab_private_key: |" >> $OUTFILE
nl -w 1 -b n -s ' ' k1 >> $OUTFILE
rm -f k*