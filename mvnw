#!/usr/bin/env sh
set -e

MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Maven Wrapper jar not found at $WRAPPER_JAR" 1>&2
  echo "Run: pwsh -File \"$MAVEN_PROJECTBASEDIR/.mvn/wrapper/download-maven-wrapper.ps1\"" 1>&2
  exit 1
fi

JAVA_EXE="${JAVA_HOME:-}/bin/java"
if [ ! -x "$JAVA_EXE" ]; then
  JAVA_EXE="java"
fi

exec "$JAVA_EXE" -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" -classpath "$WRAPPER_JAR" org.apache.maven.wrapper.MavenWrapperMain "$@"

