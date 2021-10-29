
FROM amazoncorretto:17-alpine-jdk

# 1- Add curl
RUN apk add --no-cache curl

# 2- Define a constant with the version of Maven.
ARG MAVEN_VERSION=3.8.3

# 3- Define the SHA key to validate the maven download. EACH VERSION HAS ITS OWN SHA!!!
ARG SHA=1c12a5df43421795054874fd54bb8b37d242949133b5bf6052a063a13a93f13a20e6e9dae2b3d85b9c7034ec977bbc2b6e7f66832182b9c863711d78bfe60faa

# 4- Define a constant with the directory for Maven installation
ARG MAVEN_HOME_DIR=usr/share/maven

# 5- Define a constant with the working directory
ARG APP_DIR="app"

# 6- Define the URL where maven can be downloaded from
ARG BASE_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries

# 7- Create the directories, download Maven, validate the download, install it, remove the downloaded file, and set links
RUN mkdir -p /$MAVEN_HOME_DIR /$MAVEN_HOME_DIR/ref \
  && echo "[ECHO] Downloading maven" \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  \
  && echo "[ECHO] Checking download hash" \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  \
  && echo "[ECHO] Unzipping maven" \
  && tar -xzf /tmp/apache-maven.tar.gz -C /$MAVEN_HOME_DIR --strip-components=1 \
  \
  && echo "[ECHO] Cleaning and setting links" \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /$MAVEN_HOME_DIR/bin/mvn /usr/bin/mvn

# 8- Define environmental variables required by Maven, like the Maven_Home directory and where the maven repo is located
ENV MAVEN_CONFIG "/${APP_DIR}/.m2"

# 9- Define app name an artifactId from POM
ENV APP_NAME aws-code-build-custom-build-environment

# 10- Copy source code and POM
COPY ./src ./$APP_DIR/src
COPY pom.xml ./$APP_DIR

# 11- Define app directory as the working directory
WORKDIR /$APP_DIR

# 12- Build and package source code using Maven
RUN mvn clean package

# 13- Copy jar file to the work directory
RUN mv target/$APP_NAME.jar .

# 14- Remove Maven and source code of an application to make an image cleaner
RUN echo "[ECHO] Removing source code" \
    && rm -rf /$APP_DIR/src \
    \
    && echo "[ECHO] Removing pom.xml"  \
    && rm -f /$APP_DIR/pom.xml \
    \
     && echo "[ECHO] Removing output of the build"  \
    && rm -rf /$APP_DIR/target \
    \
    && echo "[ECHO] Removing local maven repository ${MAVEN_CONFIG}"  \
    && rm -rf $MAVEN_CONFIG \
    \
    && echo "[ECHO] Removing maven binaries"  \
    && rm -rf /$MAVEN_HOME_DIR \
    \
    && echo "[ECHO] Removing curl binaries"  \
    && apk del --no-cache curl

VOLUME $APP_DIR/tmp
EXPOSE 8080

ENTRYPOINT exec java -jar $APP_NAME.jar -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS
