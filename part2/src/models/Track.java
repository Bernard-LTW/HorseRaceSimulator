package models;

public class Track {
    public enum TrackShape {
        OVAL, FIGURE_EIGHT, CUSTOM
    }

    public enum TrackCondition {
        MUDDY, DRY, ICY
    }

    private String name;
    private int laneCount;
    private int length;
    private TrackShape shape;
    private TrackCondition condition;

    public Track(String name, int laneCount, int length, TrackShape shape, TrackCondition condition) {
        this.name = name;
        this.laneCount = laneCount;
        this.length = length;
        this.shape = shape;
        this.condition = condition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLaneCount() {
        return laneCount;
    }

    public int getLength() {
        return length;
    }

    public TrackShape getShape() {
        return shape;
    }

    public TrackCondition getCondition() {
        return condition;
    }

    public void setLaneCount(int laneCount) {
        this.laneCount = laneCount;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setShape(TrackShape shape) {
        this.shape = shape;
    }

    public void setCondition(TrackCondition condition) {
        this.condition = condition;
    }

    public double getSpeedModifier() {
        switch (condition) {
            case MUDDY:
                return 0.8; // 20% slower
            case DRY:
                return 1.0; // Normal speed
            case ICY:
                return 0.6; // 40% slower
            default:
                return 1.0;
        }
    }

    public double getFallRiskModifier() {
        switch (condition) {
            case MUDDY:
                return 0.1; // Low risk
            case DRY:
                return 0.0; // No risk
            case ICY:
                return 0.3; // High risk
            default:
                return 0.0;
        }
    }

    public double getShapeSpeedAdjustment(int distanceTravelled) {
        switch (shape) {
            case OVAL:
                return 1.0; // Constant speed
            case FIGURE_EIGHT:
                if (distanceTravelled % (length / 2) == 0) {
                    return 0.7; // Slow down at intersections
                }
                return 1.0;
            case CUSTOM:
                // Custom logic can be added here
                return 1.0;
            default:
                return 1.0;
        }
    }
}