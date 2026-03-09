# Study Optimizer (MVP)

A Java 21 CLI app that generates a study schedule based on exam dates, difficulty, available hours, and spaced repetition.

## Requirements

- Java 21+
- Maven 3.9+

## Build

```bash
mvn -q -DskipTests=false test
mvn -q package
```

The runnable fat jar will be in `target/` (name includes `-shaded.jar`).

## Run

From the `study-optimizer` directory:

```bash
java -jar target/study-optimizer-0.1.0-shaded.jar init
java -jar target/study-optimizer-0.1.0-shaded.jar generate
java -jar target/study-optimizer-0.1.0-shaded.jar today
java -jar target/study-optimizer-0.1.0-shaded.jar status
java -jar target/study-optimizer-0.1.0-shaded.jar complete <sessionId>
```

## Data location

Files are stored in:

- Windows: `%USERPROFILE%\.study-optimizer\`
- macOS/Linux: `~/.study-optimizer/`

Files:
- `input.json` (subjects + settings)
- `plan.json` (generated plan + progress)

