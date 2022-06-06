@echo off

:begin

:: needed to overcome weird loop behavior in conjunction with variable expansion
SETLOCAL enabledelayedexpansion

set mainClass=org.confab.videobridge.MainKt
set cp=confab-videobridge.jar
FOR %%F IN (lib/*.jar) DO (
  SET cp=!cp!;lib/%%F%
)

java -Djava.util.logging.config.file=lib/logging.properties -Djdk.tls.ephemeralDHKeySize=2048 -cp %cp% %mainClass% %*
