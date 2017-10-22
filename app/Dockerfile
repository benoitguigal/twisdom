FROM openjdk:7-jdk

ENV SCALA_VERSION 2.10.1
ENV SBT_VERSION 0.13.0

RUN ls /usr/lib/jvm
# Scala expects this file
RUN touch /usr/lib/jvm/java-7-openjdk-amd64/release

RUN mkdir /src 
WORKDIR /src 

# Install Scala
## Piping curl directly in tar
RUN \
  curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C /root/ && \
  echo >> /root/.bashrc && \
  echo 'export PATH=~/scala-$SCALA_VERSION/bin:$PATH' >> /root/.bashrc

# Install sbt
RUN \
  curl -L -o sbt-$SBT_VERSION.deb https://dl.bintray.com/sbt/debian/sbt-$SBT_VERSION.deb && \
  dpkg -i sbt-$SBT_VERSION.deb && \
  rm sbt-$SBT_VERSION.deb && \
  apt-get update && \
  apt-get install sbt && \
  sbt sbtVersion

# Add project 
RUN mkdir -p /root/twisdom
ADD ./ /root/twisdom  

# Define working directory 
WORKDIR /root/twisdom

RUN sbt clean clean-files compile

# Expose port
EXPOSE 9000

CMD ["sbt" ,"start -Dhttp.port=9000 -Dpidfile.path=/dev/null"]