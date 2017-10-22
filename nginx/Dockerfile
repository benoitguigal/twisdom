# Set nginx base image
FROM nginx

# File Author / Maintainer
MAINTAINER Benoît Guigal

RUN apt-get update && apt-get install -y supervisor

# Copy custom configuration file from the current directory
COPY nginx.conf /etc/nginx/nginx.conf
RUN mkdir /etc/nginx/sites-available && mkdir /etc/nginx/sites-enabled
COPY sites-available /etc/nginx/sites-available
ENV SITES_ENABLED twisdom
COPY h5bp /etc/nginx/h5bp

COPY ./supervisord.conf /etc/

COPY ./entrypoint.sh /
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

