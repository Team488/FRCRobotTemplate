package competition.subsystems.drive;

public class SuggestedRotationValue {

    /**
     * Used for SwerveDriveRotationAdvisor to pass back values in one of two ways:
     * DesiredHeading (The heading you want to be at)
     * HeadingPower (The power of which you should be rotating)
     */
    public double value;
    public ValueType type;

    public enum ValueType {
        DesiredHeading,
        HeadingPower
    }

    public SuggestedRotationValue(double value, ValueType type) {
        this.value = value;
        this.type = type;
    }

    public SuggestedRotationValue() {
        this.value = 0;
        this.type = ValueType.HeadingPower;
    }
}
