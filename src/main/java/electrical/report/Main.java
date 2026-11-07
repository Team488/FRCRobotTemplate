package electrical.report;

import java.util.LinkedHashMap;
import java.util.Map;

import competition.electrical_contract.Contract2023;
import competition.electrical_contract.Contract2025;
import competition.electrical_contract.Contract2026;
import competition.electrical_contract.RoboxContract;
import xbot.common.electrical.report.ElectricalReport;
import xbot.common.electrical.report.ElectricalReport.RobotContract;

/**
 * Electrical report for this robot code: every CAN device, I/O channel and power connection in
 * each robot's contract, as text or as JSON for the electrical web tool.
 *
 * <pre>
 * ./gradlew electricalReport                                      text report, Competition robot
 * ./gradlew electricalReport --args="--contract 2025"             another robot
 * ./gradlew electricalReport --args="--contract 2025 --json -f x.json"
 * ./gradlew electricalReport --args="--all -d out"                every robot as JSON + index.json
 * </pre>
 *
 * <p>Keep CONTRACTS in sync with the ContractToUse switch in competition.Robot. The GitHub
 * workflow "Electrical snapshots" publishes {@code --all} on every merge to main.
 */
public final class Main {

    /** ContractToUse name to robot, newest first; the first entry is the default. */
    static final Map<String, RobotContract> CONTRACTS = new LinkedHashMap<>();

    static {
        CONTRACTS.put("Competition", new RobotContract("Competition", Contract2026::new));
        CONTRACTS.put("2025", new RobotContract("2025 Robot", Contract2025::new));
        CONTRACTS.put("2023", new RobotContract("2023 Robot", Contract2023::new));
        CONTRACTS.put("Robox", new RobotContract("Robox", RoboxContract::new));
    }

    private Main() {}

    public static void main(String[] args) throws Exception {
        ElectricalReport.run(args, CONTRACTS);
    }
}
