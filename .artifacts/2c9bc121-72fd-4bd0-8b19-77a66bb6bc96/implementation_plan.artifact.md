# Fix NoSuchObjectException in Kotlin Compilation

The error `java.rmi.NoSuchObjectException: no such object in table` is a communication failure between the Gradle process and the Kotlin compiler daemon. This typically happens when the Kotlin daemon crashes or becomes unresponsive due to memory pressure or misconfigured JVM arguments.

## Proposed Changes

### [Core Infrastructure]

#### [MODIFY] [gradle.properties](file:///C:/Users/lenovo/Projects/sheduling%20and%20planning/sphere%20orbit/gradle.properties)
- Increase the Kotlin daemon heap size and metaspace size.
- Remove the `-XX:-UseParallelGC` flag, which was likely causing suboptimal garbage collection behavior.
- Add `-Djava.rmi.server.hostname=127.0.0.1` to ensure the daemon uses a reliable local address for RMI communication.

## Verification Plan

### Manual Verification
1. Run `./gradlew --stop` to kill any existing (potentially corrupted) Kotlin daemons.
2. Run `./gradlew :feature:calendar:compileDebugKotlin` to verify the fix.
3. Perform a full clean build: `./gradlew clean assembleDebug`.
