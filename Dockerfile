# Builder image with sbt 2 + Scala 3.9 pre-installed and caches pre-warmed.
# Projects bring their own build.sbt; this image only provides the toolchain.
# The sbt and Scala versions (and any extra pre-fetched dependencies) are
# defined by the warmup/ project, not here.
FROM eclipse-temurin:25-jdk

# Version of the sbt runner script; it launches whatever sbt.version a project pins
ARG SBT_RUNNER_VERSION=2.0.9

# Install the sbt runner
ADD https://github.com/sbt/sbt/releases/download/v${SBT_RUNNER_VERSION}/sbt-${SBT_RUNNER_VERSION}.tgz /tmp/sbt.tgz
RUN tar -xzf /tmp/sbt.tgz -C /opt && rm /tmp/sbt.tgz \
 # sbt 2 defaults to the native thin client (sbtn), which spawns a background
 # server and fails in non-interactive builds. Run sbt directly in the JVM instead.
 && echo "--server" >> /opt/sbt/conf/sbtopts \
 # Keep sbt's boot dir, ivy home and caches in a fixed location, so they work
 # (and stay pre-warmed) no matter which user or $HOME the container runs with.
 && echo "-Duser.home=/opt/sbt-home" >> /opt/sbt/conf/sbtopts \
 && mkdir -p /opt/sbt-home
ENV PATH="/opt/sbt/bin:${PATH}" \
    COURSIER_CACHE=/opt/sbt-home/.cache/coursier

# Pre-warm: compile the warmup project (main + test) so sbt, the Scala compiler,
# compiler bridge and all its dependencies land in the caches, then delete it.
COPY warmup /tmp/warmup
RUN cd /tmp/warmup \
 && sbt -batch Test/compile \
 && cd / && rm -rf /tmp/warmup \
 # Any uid must be able to take lock files and add to the caches
 && chmod -R a+rwX /opt/sbt-home \
 && mkdir -p /workspace && chmod a+rwx /workspace

WORKDIR /workspace
CMD ["sbt"]