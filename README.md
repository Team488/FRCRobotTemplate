# FRC CommonLib Robot Template Project [![Build Status](https://dev.azure.com/Team488/Team%20488%20Builds/_apis/build/status/Team488.FRCRobotTemplate?branchName=main)](https://dev.azure.com/Team488/Team%20488%20Builds/_build/latest?definitionId=3&branchName=main)

This is a template project which includes all the boilerplate to use our Seriously Common Lib. It can be forked and used as a robot project for the season if you want to use our library.

To find out more about the library, visit the [Seriously Common Lib repo](https://github.com/Team488/SeriouslyCommonLib).

## Up-front warning

This template, like our library as a whole, is a work-in-progress. See the library readme for more information.

## Development setup

Code formatting is auto-fixed by Spotless, using the same Eclipse formatter profile VS Code is
configured to use. Run `git config core.hooksPath githooks` once per clone so a misformatted
commit gets caught (and fixed in your working tree for you to review) before it lands. Install the
recommended `redhat.java` VS Code extension if prompted, for format-on-save.

## Electrical report and web tool

`./gradlew electricalReport` lists every CAN device, I/O channel and power connection in a robot's
electrical contract (`--args="--contract 2025"` for another robot, `--json` for JSON). The list of
robots is in `src/main/java/electrical/report/Main.java`; keep it in sync with the `ContractToUse`
switch in `competition.Robot`.

- Tag mechanism getters with `@ReadyWhen("Name")` (a `Hardware` constant) so robots without that
  mechanism leave them out.
- Put wiring the code doesn't use (CAN daisy-chain order, non-motor PDH loads, converter branches)
  in the year's contract by overriding the `XElectricalReportContract` methods.
- The **Electrical snapshots** GitHub workflow publishes every robot's contract as JSON on each
  merge to `main` (branch `electrical-snapshots`). Point the electrical web tool
  (Team488/electrical-contract-web) at that folder by adding it to `snapshotSources` in
  `infra/cdk.json`.
