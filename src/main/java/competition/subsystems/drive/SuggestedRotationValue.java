package competition.subsystems.drive;

public class SuggestedRotationValue {

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
