# Fix Commit Error in `KD_BasicOmniOpMode_Linear.java`

The user is encountering a commit error after creating a new OpMode `KD_BasicOmniOpMode_Linear.java`. Research indicates that the file was likely copied from a sample but the package declaration was not updated to match its new location in the `TeamCode` module.

## Proposed Changes

### [TeamCode Component]

#### [MODIFY] [KD_BasicOmniOpMode_Linear.java](file:///C:/Users/kaycd/OneDrive/Documents/GitHub/robo-eagles-summer-2026/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/SampleCode/KD_BasicOmniOpMode_Linear.java)

- Update the package declaration from `org.firstinspires.ftc.robotcontroller.external.samples` to `org.firstinspires.ftc.teamcode.SampleCode`.
- Clean up the closing braces at the end of the file for better formatting.

## Verification Plan

### Automated Tests
- Run `:TeamCode:assembleDebug` to ensure the code compiles correctly with the new package name.

### Manual Verification
- The user can attempt to commit the changes again in Android Studio to verify that the "Package name mismatch" or similar commit-time checks pass.
