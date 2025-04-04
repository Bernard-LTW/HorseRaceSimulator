package models;

public class Track {
    public enum TrackShape {
        OVAL, FIGURE_EIGHT
    }

    public enum TrackCondition {
        MUDDY, DRY, ICY
    }

    private String name;
    private int laneCount;
    private int length;
    private TrackShape shape;
    private TrackCondition condition;
    private double bestTime;
    private String bestHorse;

    public Track(String name, int laneCount, int length, TrackShape shape, TrackCondition condition) {
        this.name = name;
        this.laneCount = laneCount;
        this.length = length;
        this.shape = shape;
        this.condition = condition;
        this.bestTime = 0;
        this.bestHorse = "";
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name;}

    public int getLaneCount() { return laneCount;}

    public int getLength() { return length; }

    public TrackShape getShape() { return shape; }

    public TrackCondition getCondition() { return condition; }

    public void setLength(int length) { this.length = length; }

    public void setCondition(TrackCondition condition) { this.condition = condition; }

    public double getBestTime() { return bestTime; }

    public void setBestTime(double bestTime) { this.bestTime = bestTime; }

    public String getBestHorse() { return bestHorse; }

    public void setBestHorse(String bestHorse) { this.bestHorse = bestHorse;}

    public double getSpeedModifier() {
        return switch (condition) {
            case MUDDY -> 0.8;
            case DRY -> 1.0;
            case ICY -> 0.6;
        };
    }

    public double getFallRiskModifier() {
        return switch (condition) {
            case MUDDY -> 0.1;
            case DRY -> 0.0;
            case ICY -> 0.3;
        };
    }

    public double getShapeSpeedAdjustment(int distanceTravelled) {
        return switch (shape) {
            case OVAL -> 1.0;
            case FIGURE_EIGHT -> {
                if (distanceTravelled % (length / 2) == 0) {
                    yield 0.7;
                }
                yield 1.0;
            }
        };
    }
}