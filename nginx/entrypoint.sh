#!/usr/bin/env bash

# Enable sites based on SITES_ENABLED env variable
IFS=',' read -ra SITES <<< "$SITES_ENABLED"
for site in "${SITES[@]}"; do
    ln -s /etc/nginx/sites-available/${site} /etc/nginx/sites-enabled/${site}
done

## run supervisor
supervisord --nodaemon --configuration /etc/supervisord.conf