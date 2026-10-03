# FRC CommonLib Robot Template Project [![Build Status](https://dev.azure.com/Team488/Team%20488%20Builds/_apis/build/status/Team488.FRCRobotTemplate?branchName=main)](https://dev.azure.com/Team488/Team%20488%20Builds/_build/latest?definitionId=3&branchName=main)

This is a template project which includes all the boilerplate to use our Seriously Common Lib. It can be forked and used as a robot project for the season if you want to use our library.

To find out more about the library, visit the [Seriously Common Lib repo](https://github.com/Team488/SeriouslyCommonLib).

## Up-front warning

This template, like our library as a whole, is a work-in-progress. See the library readme for more information.


## 2027 alpha development

This project targets Java 25, Gradle 9.4.1, and WPILib 2027.0.0-alpha-7 for SystemCore.
Use the `2027` branch of SeriouslyCommonLib in the sibling `../SeriouslyCommonLib` directory:

```bash
./gradlew build -DuseLocalCommonLib=true
```

In IntelliJ, select JDK 25 as the Gradle JVM and add `-DuseLocalCommonLib=true` to Gradle run configurations.
The published SCL version in `build.gradle` is the previous 2026 fallback and is not compatible with this migration.
CI checks out SCL's `2027` branch and builds with the same local-library flag.

MapleSim is temporarily disabled: simulation uses `NoopSimulator`, and the MapleSim sources remain excluded from compilation.
The old MapleSim, Phoenix 5, and LaserCAN vendor definitions are preserved as `.disabled` files in `config`.
The real LaserCAN factory reports unsupported hardware if invoked; simulated/mock devices remain available.

**Before hardware deployment:** verify the SystemCore onboard IMU mounting orientation in each electrical contract.
`MountOrientation.FLAT` is provisional. The old SPI NavX is no longer supported by this SCL branch.
Deployment now targets SystemCore; this migration has not been tested on hardware.
