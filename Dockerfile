FROM eclipse-temurin:21-jdk
 
ARG SBT_VERSION=2.0.9
ARG SCALA_VERSION=3.9.0
 
LABEL org.opencontainers.image.description="sbt ${SBT_VERSION} + Scala ${SCALA_VERSION} on Temurin JDK 21"
 
# Install the sbt runner
ADD https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz /tmp/sbt.tgz
RUN tar -xzf /tmp/sbt.tgz -C /opt && rm /tmp/sbt.tgz
ENV PATH="/opt/sbt/bin:${PATH}"
 
# Pre-warm: compile a throwaway project so sbt itself, the Scala compiler,
# stdlib and compiler bridge are already in the caches, then delete the project.
RUN mkdir -p /tmp/warmup/project /tmp/warmup/src/main/scala \
 && cd /tmp/warmup \
 && echo "sbt.version=${SBT_VERSION}" > project/build.properties \
 && echo "scalaVersion := \"${SCALA_VERSION}\"" > build.sbt \
 && echo "@main def warmup(): Unit = ()" > src/main/scala/Warmup.scala \
 && sbt -batch compile \
 && cd / && rm -rf /tmp/warmup
 
WORKDIR /workspace
CMD ["sbt"]
 