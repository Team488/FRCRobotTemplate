# FRC CommonLib Robot Template Project [![Build Status](https://dev.azure.com/Team488/Team%20488%20Builds/_apis/build/status/Team488.FRCRobotTemplate?branchName=main)](https://dev.azure.com/Team488/Team%20488%20Builds/_build/latest?definitionId=3&branchName=main)

This is a template project which includes all the boilerplate to use our Seriously Common Lib. It can be forked and used as a robot project for the season if you want to use our library.

To find out more about the library, visit the [Seriously Common Lib repo](https://github.com/Team488/SeriouslyCommonLib).

## Getting Started

### For Most Students (Simple Setup)
```bash
git clone <this-repo-url>
cd TeamXbot2026
./gradlew build
```

### For Library Developers (Advanced Setup)
If you're working on changes to SeriouslyCommonLib and want to test them in this robot code:

```bash
# Clone both repositories side-by-side
git clone <this-repo-url> TeamXbot2026
git clone <library-repo-url> SeriouslyCommonLib

# Build with local library (add the flag to use local instead of Maven)
cd TeamXbot2026
./gradlew build -DuseLocalCommonLib=true
```

All changes to the local SeriouslyCommonLib are rebuilt automatically when using the flag.

Once your change to SeriouslyCommonLib has been merged, you need to update the SeriouslyCommonLib version number.
Open the `build.gradle` file and update the value of `SeriouslyCommonLibVersion` to the
[latest new version](https://dev.azure.com/Team488/Team%20488%20Builds/_artifacts/feed/XBot) and make sure it includes your changes.


This template, like our library as a whole, is a work-in-progress. See the library readme for more information.
