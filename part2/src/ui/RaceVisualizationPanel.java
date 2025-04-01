package ui;

import models.Race;
import models.Horse;
import models.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

public class RaceVisualizationPanel extends JPanel {
    private Race currentRace;
    private Map<Horse, Point> horsePositions;
    private Map<Horse, ImageIcon> horseSprites;
    private BufferedImage trackBackground;
    private Timer animationTimer;
    private final int HORSE_SIZE = 64; // Size of horse sprites
    private final int TRACK_HEIGHT = 400;
    private final int VERTICAL_SPACING = 80; // Space between lanes
    private final int LANE_HEIGHT = 60;
    private final int START_X = 50; // Starting X position

    public RaceVisualizationPanel() {
        setLayout(null); // Using null layout for absolute positioning
        setBackground(new Color(34, 139, 34)); // Green background for grass
        horsePositions = new HashMap<>();
        horseSprites = new HashMap<>();
        
        // Load track background
        try {
            // You'll need to create and add a track background image
            trackBackground = ImageIO.read(getClass().getResource("/images/track_background.png"));
        } catch (Exception e) {
            System.err.println("Error loading track background: " + e.getMessage());
            // Create a default background if image loading fails
            trackBackground = new BufferedImage(800, TRACK_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = trackBackground.createGraphics();
            g2d.setColor(new Color(139, 69, 19)); // Brown color for dirt track
            g2d.fillRect(0, 0, 800, TRACK_HEIGHT);
            g2d.dispose();
        }

        // Initialize animation timer
        animationTimer = new Timer(50, e -> {
            updatePositions();
            repaint();
        });
    }

    public void setRace(Race race) {
        this.currentRace = race;
        initializeHorses();
    }

    private void initializeHorses() {
        horsePositions.clear();
        horseSprites.clear();
        
        Horse[] horses = currentRace.getLanes();
        Track track = currentRace.getTrack();
        
        for (int i = 0; i < horses.length; i++) {
            Horse horse = horses[i];
            if (horse != null) {
                // Set initial position for each horse
                int y = VERTICAL_SPACING + (i * LANE_HEIGHT);
                horsePositions.put(horse, new Point(START_X, y));
                
                // Load horse sprite (you'll need to create and add horse sprite GIFs)
                try {
                    String spritePath = "/images/horse_sprite_" + (i + 1) + ".gif";
                    URL spriteUrl = getClass().getResource(spritePath);
                    if (spriteUrl != null) {
                        horseSprites.put(horse, new ImageIcon(spriteUrl));
                    } else {
                        // Create a default colored rectangle if sprite not found
                        BufferedImage defaultSprite = new BufferedImage(HORSE_SIZE, HORSE_SIZE, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = defaultSprite.createGraphics();
                        g2d.setColor(new Color(
                            (int)(Math.random() * 256),
                            (int)(Math.random() * 256),
                            (int)(Math.random() * 256)
                        ));
                        g2d.fillRect(0, 0, HORSE_SIZE, HORSE_SIZE);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(String.valueOf(horse.getSymbol()), HORSE_SIZE/2 - 5, HORSE_SIZE/2 + 5);
                        g2d.dispose();
                        horseSprites.put(horse, new ImageIcon(defaultSprite));
                    }
                } catch (Exception e) {
                    System.err.println("Error loading horse sprite: " + e.getMessage());
                }
            }
        }
    }

    private void updatePositions() {
        if (currentRace == null) return;
        
        Horse[] horses = currentRace.getLanes();
        Track track = currentRace.getTrack();
        boolean allFinished = true;
        
        for (Horse horse : horses) {
            if (horse != null) {
                Point pos = horsePositions.get(horse);
                if (pos != null) {
                    // Calculate new X position based on horse's progress
                    int maxX = getWidth() - HORSE_SIZE - START_X;
                    int progress = horse.getDistanceTravelled();
                    int newX = START_X + (int)((double)progress / track.getLength() * maxX);
                    
                    if (horse.hasFallen()) {
                        // Add falling animation effect
                        pos.y += 5;
                        if (pos.y > getHeight()) {
                            pos.y = getHeight() - HORSE_SIZE;
                        }
                    } else if (progress < track.getLength()) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw track background
        g2d.drawImage(trackBackground, 0, 0, getWidth(), TRACK_HEIGHT, null);
        
        // Draw lane dividers
        g2d.setColor(Color.WHITE);
        for (int i = 1; i < currentRace.getLanes().length; i++) {
            int y = VERTICAL_SPACING + (i * LANE_HEIGHT);
            g2d.drawLine(START_X, y, getWidth() - START_X, y);
        }
        
        // Draw finish line
        g2d.setColor(Color.BLACK);
        int finishX = getWidth() - START_X - 10;
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(finishX, VERTICAL_SPACING - 20, finishX, VERTICAL_SPACING + (currentRace.getLanes().length * LANE_HEIGHT) + 20);
        
        // Draw horses
        for (Horse horse : horsePositions.keySet()) {
            Point pos = horsePositions.get(horse);
            ImageIcon sprite = horseSprites.get(horse);
            if (sprite != null) {
                sprite.paintIcon(this, g2d, pos.x, pos.y);
            }
        }
    }

    public void startRace() {
        // Reset horse positions
        initializeHorses();
        // Start animation
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