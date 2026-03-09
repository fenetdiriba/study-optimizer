# Study Optimizer (MVP)

A Java 21 **desktop app** (JavaFX) that generates an optimized study schedule for students based on:

- exam dates
- subject difficulty
- estimated total study time
- available study hours per day
- spaced repetition principles

The app is designed as a **recruiter-ready project** that demonstrates:

- non-trivial algorithms and scheduling logic
- clean object-oriented design
- JSON persistence
- a simple but usable GUI

---

## High-level features

- **Subject management**
  - Add/edit/remove subjects with:
    - name
    - exam date
    - difficulty (1–5)
    - estimated total hours
- **Study plan generation**
  - Generates a day-by-day schedule from “today” to the last exam
  - Respects a per-day study time budget (hours per day)
  - Allocates multiple focused learning blocks per subject
- **Spaced repetition**
  - For each learning block, automatically schedules review sessions at default intervals:
    - +1, +3, +7, +14 days (configurable in code)
  - Never schedules reviews after the exam date
- **Progress tracking**
  - View all scheduled sessions in a table
  - Mark selected sessions as **done**
  - Shows overall progress as completed vs total sessions
- **Persistence**
  - All inputs and generated plans are saved as JSON files under the user’s home directory
  - App attempts to reload last input/plan on startup

---

## Architecture overview

Package layout (single Maven module, mostly pure Java in the core):

- `com.studyoptimizer.domain`
  - `Subject` – name, difficulty, exam date, estimated total minutes
  - `StudySession` – one study block (subject, date, duration, type, completion state)
  - `StudyDay` – list of sessions for a specific calendar date
  - `StudyPlan` – full plan: start/end date, daily budget, generated days
  - `PlanningInput` – user configuration: subjects, daily minutes, block sizes, repetition intervals
- `com.studyoptimizer.scheduling`
  - `ScheduleGenerator` – core algorithm that:
    - computes a schedule from today to last exam
    - prioritizes harder/closer subjects
    - creates learning blocks and spaced repetition review sessions
  - `SpacedRepetition` – exposes default repetition intervals `[1, 3, 7, 14]` days
- `com.studyoptimizer.progress`
  - `ProgressTracker` – marks sessions as completed and summarizes progress (counts + percentage)
- `com.studyoptimizer.storage`
  - `FileStore` – reads/writes JSON (`PlanningInput` and `StudyPlan`) using Jackson, with Java Time support
- `com.studyoptimizer.ui`
  - `StudyOptimizerApp` – JavaFX `Application` class and main window layout
  - `SubjectRow`, `SubjectDialog` – JavaFX models and dialog for subject CRUD
  - `SessionRow` – table model for viewing sessions
  - `PlanFormatter` – renders a readable text view of the plan (per day) in a side panel

Core logic (domain + scheduling + progress + storage) is **UI-agnostic**, so it could be reused by a console app or web API.

---

## Scheduling & prioritization (algorithm sketch)

Given:

- list of `Subject` objects (name, difficulty, exam date, estimated minutes)
- `PlanningInput` with:
  - `startDate` (defaults to `LocalDate.now()`)
  - `dailyAvailableMinutes`
  - `learnBlockMinutes` (size of a learning block)
  - `reviewMinutes` (length of each review block)

The `ScheduleGenerator`:

1. Determines the **planning horizon** from `startDate` to the latest exam date.
2. Tracks, for each subject, how many **learning minutes** are still required.
3. For each day in the horizon:
   - Starts with a daily time budget (`dailyAvailableMinutes`).
   - **Schedules due reviews first**, using a simple queue per day, without exceeding the budget.
   - While there is budget left:
     - Picks the **next subject** to study based on:
       \[
       \text{priority} = \frac{\text{difficulty}}{\text{daysUntilExam}+1}
       \]
     - Allocates a learning block (`learnBlockMinutes`, but never more than remaining minutes for that subject or remaining daily budget).
     - For each learning block, creates its future **review sessions** at configured intervals, skipping any that would fall after the exam date.
4. Produces a `StudyPlan` with a `StudyDay` per calendar date and sorted sessions in each day.

Constraints:

- No sessions are scheduled after a subject’s exam date.
- Daily time budget is never exceeded in tests (`ScheduleGeneratorTest`).

---

## Tech stack

- **Language**: Java 21 (records, `java.time`, etc.)
- **UI toolkit**: JavaFX (`javafx-controls` 21.x)
- **Build**: Maven with Maven Wrapper (no global Maven required)
- **JSON**: Jackson (`jackson-databind`, `jackson-datatype-jsr310`)
- **Tests**: JUnit 5 (MVP uses a couple of core tests; can be expanded)

This choice keeps the project “mostly Java” while using standard, widely adopted JVM libraries.

---

## Requirements

- **JDK 21+** (JDK, not just a JRE; needed for compilation)
- **Internet access on first run** (Maven wrapper downloads Maven + dependencies)

---

## Build / run

From `study-optimizer` directory:

```bash
.\mvnw.cmd -q test
.\mvnw.cmd javafx:run
```

If you don’t have Maven installed, the project includes a Maven wrapper. If the wrapper jar is missing, download it once:

```powershell
powershell -ExecutionPolicy Bypass -File .\.mvn\wrapper\download-maven-wrapper.ps1
```

On macOS/Linux, you can use:

```bash
./mvnw -q test
./mvnw javafx:run
```

---

## How to use the app (UX flow)

1. **Start the app**
   - Run `.\mvnw.cmd javafx:run`.
   - The window will attempt to load previous `input.json` / `plan.json` if they exist.
2. **Add subjects**
   - In the **Subjects** panel:
     - Click **Add…**.
     - Enter:
       - Name (e.g. `Linear Algebra`)
       - Exam date (`YYYY-MM-DD`)
       - Difficulty (1–5)
       - Estimated hours (total you want to invest)
   - Use **Edit…** to tweak values or **Remove** to delete a subject.
3. **Configure study constraints**
   - Top toolbar:
     - **Daily hours** – how many hours per day you are willing to study.
     - **Learn block (min)** – size of each focused learning block.
     - **Review (min)** – duration of each spaced repetition review.
4. **Generate a plan**
   - Click **Generate plan**.
   - The app:
     - Builds a `PlanningInput` from your subjects and settings.
     - Calls `ScheduleGenerator` to build a `StudyPlan`.
     - Saves both `input.json` and `plan.json`.
5. **Review the plan**
   - **Plan text area**: human-readable per-day listing of sessions.
   - **Sessions table**: one row per session (date, subject, type, minutes, done, session ID).
6. **Track progress**
   - Select a row in the **Sessions** table.
   - Click **Mark session done**.
   - The table and text view update, and the progress label shows updated completion percentage.

Inputs and plans can be reloaded on the next launch using **Load input** / **Load plan**.

---

## Data location & JSON files

Files are stored in:

- Windows: `%USERPROFILE%\.study-optimizer\`
- macOS/Linux: `~/.study-optimizer/`

Files:

- `input.json` – serialized `PlanningInput` (subjects + global parameters)
- `plan.json` – serialized `StudyPlan` (generated days + sessions + completion state)

These are plain JSON, making it easy to:

- inspect/debug plans
- build other tooling on top (e.g., export to calendar)
- unit test core logic with real scenarios

---

## Why this is interesting to recruiters

- **Algorithmic thinking**: Implements a non-trivial scheduling algorithm with priorities, constraints, and spaced repetition logic.
- **Clean separation of concerns**:
  - domain models and algorithm in `domain` / `scheduling`
  - persistence in `storage`
  - presentation in `ui`
- **Real-world scenario**: Solves a concrete student productivity problem rather than a toy demo.
- **Extensibility**: Clear places to add:
  - smarter optimization (priority queues, different scoring functions)
  - burnout prevention (auto rest days if schedule is too dense)
  - performance prediction (readiness scores based on completion)
  - alternative UIs (web, mobile, CLI) using the same core logic.

