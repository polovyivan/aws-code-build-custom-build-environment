
FROM amazoncorretto:16-alpine-jdk

# 1- Add curl
RUN apk add --no-cache curl

# 2- Define a constant with the version of Maven
ARG MAVEN_VERSION=3.8.2

# 3- Define a constant with the directory for Maven installation
ARG MAVEN_HOME_DIR=usr/share/maven

# 4- Define a constant with the working directory
ARG APP_DIR="app"

# 5- Define the SHA key to validate the maven download
ARG SHA=b0bf39460348b2d8eae1c861ced6c3e8a077b6e761fb3d4669be5de09490521a74db294cf031b0775b2dfcd57bd82246e42ce10904063ef8e3806222e686f222

# 6- Define the URL where maven can be downloaded from
ARG BASE_URL=https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries

# 7- Create the directories, download Maven, validate the download, install it, remove downloaded file and set links
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

# 8- Define environmental variables required by Maven, like Maven_Home directory and where the maven repo is located
ENV MAVEN_CONFIG "/${APP_DIR}/.m2"

# 9- Define app name and build directory variables
ENV APP_NAME aws-code-build-custom-build-environment
ENV JAR_FILE /$APP_DIR/target/$APP_NAME.jar

# 10- Copy source code and POM
COPY ./src ./$APP_DIR/src
COPY pom.xml ./$APP_DIR

# 11- Define app directory as work directory
WORKDIR /$APP_DIR

# 12- Build and package source code using Maven
RUN mvn clean package

# 13- Remove Maven and source code of an application to make an image cleaner
RUN echo "[ECHO] Removing source code" \
    && rm -rf /$APP_DIR/src \
    \
    && echo "[ECHO] Removing pom.xml"  \
    && rm -f /$APP_DIR/pom.xml \
    \
    && echo "[ECHO] Removing local maven repository ${MAVEN_CONFIG}"  \
    && rm -rf $MAVEN_CONFIG \
    \
    && echo "[ECHO] Removing maven binaries"  \
    && rm -rf /$MAVEN_HOME_DIR

VOLUME /tmp
EXPOSE 8080

ENTRYPOINT exec java -jar $JAR_FILE -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS