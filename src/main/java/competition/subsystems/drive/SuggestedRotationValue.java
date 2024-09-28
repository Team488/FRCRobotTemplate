package competition.subsystems.drive;

public class SuggestedRotationValue {

    /**
     * Used for SwerveDriveRotationAdvisor to pass back values in one of two ways:
     * DesiredHeading (The heading you want to be at)
     * HeadingPower (The power of which you should be rotating)
     */
    public double value;
    public RotationGoalType type;

    public enum RotationGoalType {
        DesiredHeading,
        HeadingPower
    }

    public SuggestedRotationValue(double value, RotationGoalType type) {
        this.value = value;
        this.type = type;
    }

    public SuggestedRotationValue() {
        this.value = 0;
        this.type = RotationGoalType.HeadingPower;
    }
}
