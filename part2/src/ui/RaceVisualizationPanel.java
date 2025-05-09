package ui;

import models.Race;
import models.Horse;
import models.Track;
import models.Bet;
import models.BetManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class RaceVisualizationPanel extends JPanel {
    private Race currentRace;
    private Map<Horse, Point> horsePositions;
    private Map<Horse, List<BufferedImage>> horseFrames;
    private Map<Horse, Integer> currentFrameIndices;
    private BufferedImage trackBackground;
    private Timer animationTimer;
    private final int TRACK_HEIGHT = 600;
    private final int VERTICAL_SPACING = 25;
    private final int START_X = 50;
    private final int ANIMATION_DELAY = 100;
    private final Font nameFont = new Font("Arial", Font.BOLD, 12);
    private final Font trackInfoFont = new Font("Arial", Font.BOLD, 14);
    private final Color GRASS_COLOR = new Color(76, 175, 80);
    private final float TRACK_THICKNESS = 1.5f;
    private BetManager betManager;

    public RaceVisualizationPanel(BetManager betManager) {
        setLayout(null);
        setBackground(GRASS_COLOR);
        horsePositions = new HashMap<>();
        horseFrames = new HashMap<>();
        currentFrameIndices = new HashMap<>();
        this.betManager = betManager;
        
        createTrackBackground();

        animationTimer = new Timer(ANIMATION_DELAY, e -> {
            updatePositions();
            updateAnimationFrames();
            repaint();
        });
    }

    private void createTrackBackground() {
        trackBackground = new BufferedImage(800, TRACK_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = trackBackground.createGraphics();
        g2d.setColor(GRASS_COLOR);
        g2d.fillRect(0, 0, 800, TRACK_HEIGHT);
        g2d.dispose();
    }

    private int calculateHorseSize() {
        if (currentRace == null) return 64;
        int numHorses = currentRace.getLanes().length;
        int maxSize = 64;
        int minSize = 32;
        int calculatedSize = (TRACK_HEIGHT - (2 * VERTICAL_SPACING)) / (numHorses * 2);
        return Math.max(minSize, Math.min(maxSize, calculatedSize));
    }

    private void loadHorseFrames(Horse horse) {
        List<BufferedImage> frames = new ArrayList<>();
        try {
            for (int i = 0; i < 5; i++) {
                String framePath = "/images/frame_" + String.format("%03d", i) + ".png";
                BufferedImage frame = ImageIO.read(getClass().getResource(framePath));
                int size = calculateHorseSize();
                BufferedImage scaledFrame = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledFrame.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(frame, 0, 0, size, size, null);
                g2d.dispose();
                frames.add(scaledFrame);
            }
            horseFrames.put(horse, frames);
            currentFrameIndices.put(horse, 0);
        } catch (Exception e) {
            System.err.println("Error loading horse frames: " + e.getMessage());
        }
    }

    private void updateAnimationFrames() {
        for (Horse horse : horseFrames.keySet()) {
            int currentIndex = currentFrameIndices.get(horse);
            List<BufferedImage> frames = horseFrames.get(horse);
            currentFrameIndices.put(horse, (currentIndex + 1) % frames.size());
        }
    }

    private void initializeHorses() {
        horsePositions.clear();
        horseFrames.clear();
        currentFrameIndices.clear();
        
        Horse[] horses = currentRace.getLanes();
        int laneHeight = (TRACK_HEIGHT - (2 * VERTICAL_SPACING)) / Math.max(1, horses.length);
        
        for (int i = 0; i < horses.length; i++) {
            Horse horse = horses[i];
            if (horse != null) {
                int y = VERTICAL_SPACING + (i * laneHeight);
                horsePositions.put(horse, new Point(START_X, y));
                loadHorseFrames(horse);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentRace == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(trackBackground, 0, 0, getWidth(), TRACK_HEIGHT, null);
        
        Track track = currentRace.getTrack();
        g2d.setFont(trackInfoFont);
        g2d.setColor(Color.WHITE);
        String trackInfo = String.format("Track: %s | Shape: %s | Condition: %s", 
            track.getName(), 
            track.getShape(), 
            track.getCondition());
        g2d.drawString(trackInfo, START_X, 20);
        
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(TRACK_THICKNESS));
        int numLanes = currentRace.getLanes().length;
        int laneHeight = (TRACK_HEIGHT - (2 * VERTICAL_SPACING)) / Math.max(1, numLanes);
        
        for (int i = 1; i < numLanes; i++) {
            int y = VERTICAL_SPACING + (i * laneHeight);
            g2d.drawLine(START_X, y, getWidth() - START_X, y);
        }
        
        g2d.setColor(Color.WHITE);
        int finishX = getWidth() - START_X - 10;
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(finishX, VERTICAL_SPACING - 10, finishX, TRACK_HEIGHT - VERTICAL_SPACING + 10);
        
        int horseSize = calculateHorseSize();
        for (Horse horse : horsePositions.keySet()) {
            Point pos = horsePositions.get(horse);
            List<BufferedImage> frames = horseFrames.get(horse);
            int currentFrame = currentFrameIndices.get(horse);
            
            if (frames != null && !frames.isEmpty()) {
                BufferedImage currentSprite = frames.get(currentFrame);
                
                AffineTransform transform = new AffineTransform();
                transform.translate(pos.x + horseSize, pos.y); // Move to position + width
                transform.scale(-1, 1); // Mirror horizontally
                
                if (horse.hasFallen()) {
                    transform.rotate(Math.PI, horseSize / 2.0, horseSize / 2.0);
                }
                
                g2d.drawImage(currentSprite, transform, null);
                
                g2d.setFont(nameFont);
                if (horse.hasFallen()) {
                    g2d.setColor(Color.RED);
                    g2d.drawString(horse.getName(), pos.x + horseSize + 5, pos.y + horseSize/2);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(horse.getName(), pos.x + horseSize + 6, pos.y + horseSize/2 + 1);
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(horse.getName(), pos.x + horseSize + 5, pos.y + horseSize/2);
                }
            }
        }
    }

    private void updatePositions() {
        if (currentRace == null) return;
        
        Horse[] horses = currentRace.getLanes();
        Track track = currentRace.getTrack();
        boolean allFinished = true;
        int horseSize = calculateHorseSize();
        
        for (Horse horse : horses) {
            if (horse != null) {
                Point pos = horsePositions.get(horse);
                if (pos != null) {
                    int maxX = getWidth() - horseSize - START_X;
                    int progress = horse.getDistanceTravelled();
                    int newX = START_X + (int)((double)progress / track.getLength() * maxX);
                    
                    if (progress < track.getLength()) {
                        pos.x = newX;
                        allFinished = false;
                    }
                }
            }
        }
        
        if (allFinished) {
            animationTimer.stop();
        }
    }

    public void showRaceResults(Race race) {
        List<Bet> raceBets = betManager.getRaceBets(race.getRaceID());
        
        StringBuilder summary = new StringBuilder();
        summary.append("<html><body style='width: 400px'>");
        summary.append("<h2>Race Summary</h2>");
        summary.append("<p>Race ID: ").append(race.getRaceID()).append("</p>");
        summary.append("<p>Winner: ").append(race.getFinishOrder().get(0).getName()).append("</p>");
        
        summary.append("<br><h3>Confidence Changes:</h3>");
        summary.append("<table style='width:100%'>");
        summary.append("<tr><th>Horse</th><th>Change</th></tr>");
        
        Map<Horse, Double> confidenceChanges = race.getConfidenceChanges();
        for (Map.Entry<Horse, Double> entry : confidenceChanges.entrySet()) {
            Horse horse = entry.getKey();
            double change = entry.getValue();
            String changeStr = String.format("%.2f", change * 100);
            if (change > 0) {
                changeStr = "+" + changeStr;
            }
            
            summary.append("<tr>");
            summary.append("<td>").append(horse.getName()).append("</td>");
            summary.append("<td>").append(changeStr).append("%</td>");
            summary.append("</tr>");
        }
        summary.append("</table>");
        
        if (raceBets.isEmpty()) {
            summary.append("<br><p>No bets were placed on this race.</p>");
        } else {
            // Add betting summary section
            summary.append("<br><h3>Your Bets:</h3>");
            summary.append("<table style='width:100%'>");
            summary.append("<tr><th>Horse</th><th>Amount</th><th>Result</th><th>Winnings</th></tr>");
            
            double totalBets = 0.0;
            double totalWinnings = 0.0;
            
            for (Bet bet : raceBets) {
                totalBets += bet.getAmount();
                totalWinnings += bet.getWinnings();
                
                summary.append("<tr>");
                summary.append("<td>").append(bet.getHorseName()).append("</td>");
                summary.append("<td>$").append(String.format("%.2f", bet.getAmount())).append("</td>");
                summary.append("<td>").append(bet.isWon() ? "Won" : "Lost").append("</td>");
                summary.append("<td>$").append(String.format("%.2f", bet.getWinnings())).append("</td>");
                summary.append("</tr>");
            }
            
            summary.append("</table>");
            summary.append("<br><h3>Summary:</h3>");
            summary.append("<p>Total Bets: $").append(String.format("%.2f", totalBets)).append("</p>");
            summary.append("<p>Total Winnings: $").append(String.format("%.2f", totalWinnings)).append("</p>");
            summary.append("<p>Net Result: $").append(String.format("%.2f", totalWinnings - totalBets)).append("</p>");
        }
        
        summary.append("</body></html>");
        
        JOptionPane.showMessageDialog(this,
            summary.toString(),
            "Race Complete - Summary",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public void setRace(Race race) {
        this.currentRace = race;
        initializeHorses();
    }

    public void startRace() {
        initializeHorses();
        animationTimer.start();
    }

    public void stopRace() {
        animationTimer.stop();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, TRACK_HEIGHT);
    }
} 