# Builder image with sbt 2 + Scala 3.9 pre-installed and caches pre-warmed.
# Projects bring their own build.sbt; this image only provides the toolchain.
FROM eclipse-temurin:21-jdk

ARG SBT_VERSION=2.0.11
ARG SCALA_VERSION=3.9.0

LABEL org.opencontainers.image.description="sbt ${SBT_VERSION} + Scala ${SCALA_VERSION} on Temurin JDK 21"

# Install the sbt runner
ADD https://github.com/sbt/sbt/releases/download/v${SBT_VERSION}/sbt-${SBT_VERSION}.tgz /tmp/sbt.tgz
RUN tar -xzf /tmp/sbt.tgz -C /opt && rm /tmp/sbt.tgz \
 # sbt 2 defaults to the native thin client (sbtn), which spawns a background
 # server and fails in non-interactive builds. Run sbt directly in the JVM instead.
 && echo "--server" >> /opt/sbt/conf/sbtopts
ENV PATH="/opt/sbt/bin:${PATH}"

# Pre-warm: compile a throwaway project so sbt itself, the Scala compiler,
# stdlib and compiler bridge are already in the caches, then delete the project.
RUN mkdir -p /tmp/warmup/project /tmp/warmup/src/main/scala \
 && cd /tmp/warmup \
 && echo "sbt.version=${SBT_VERSION}" > project/build.properties \
 && echo 'addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.1")' > project/plugins.sbt \
 && echo "scalaVersion := \"${SCALA_VERSION}\"" > build.sbt \
 && echo "@main def warmup(): Unit = ()" > src/main/scala/Warmup.scala \
 && sbt -batch compile \
 && cd / && rm -rf /tmp/warmup

WORKDIR /workspace
CMD ["sbt"]