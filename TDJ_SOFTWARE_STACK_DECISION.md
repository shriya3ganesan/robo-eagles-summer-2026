# Tie Dye Jedi Software Stack Decision

## Decision

Tie Dye Jedi will use **Android Studio** as the supported development environment for FTC robot programming.

Our initial software stack is:

- Java
- Android Studio
- FTC SDK
- GitHub
- REV Control Hub / Driver Hub

## Why Android Studio?

Android Studio is the standard development environment used by many FTC Java teams and is the environment most commonly assumed by FTC tutorials, examples, and documentation.

While VS Code is a strong general-purpose editor, Android Studio is better suited for our rookie FTC team because:

- FTC Java examples are commonly written with Android Studio in mind.
- Android Studio provides built-in Android project support.
- The FTC SDK is structured as an Android Studio / Gradle project.
- Students can more easily follow FTC-specific tutorials.
- Mentors and event volunteers are more likely to recognize the setup.
- It reduces setup variation across student laptops.
- It gives new programmers a more consistent starting point.

## Can Students Use VS Code?

Possibly, but it will not be the team-supported default.

Students who want to use VS Code may do so later if they can successfully build, test, and contribute code without requiring extra mentor support.

For now, the supported team path is:

**Android Studio first. Other tools later if students are ready.**

## Decision Criteria Used

We chose the team stack based on:

1. Student learnability
2. FTC ecosystem fit
3. Reliability during competition
4. Mentor supportability
5. Growth potential

## Guiding Principle

Our first goal is not to use the fanciest tool.

Our first goal is to help students successfully write code, deploy it to the robot, and make the robot move.
