#!/bin/bash
OUTFILE="$1"
DOCKERHOST="$2"
DOCKERPASS="$3"
DOCKERREPO="$4"

echo "dbname: petclinic" > $OUTFILE
echo "mysql_root_password: $(pwgen -B 10 1)" >> $OUTFILE
echo "mysql_user: pc" >> $OUTFILE
echo "mysql_password: $(pwgen -B 10 1)" >> $OUTFILE
echo "docker_registry_user: admin" >> $OUTFILE
echo "docker_registry_password: $DOCKERPASS" >> $OUTFILE
echo "docker_registry_hostname: $DOCKERHOST" >> $OUTFILE
echo "docker_repo_url: $DOCKERREPO" >> $OUTFILE