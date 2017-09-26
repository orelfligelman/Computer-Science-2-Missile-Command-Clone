/*
 * CSM 2670 Homework05 
 *
 * Zachary Thoams and Ben Tyrell
 *
 * File: Exercise01.java
 * This program is a clone of the game missile command. The game starts out and 
 * the player has 30 rockets spread across 3 towers, they are to use these rockets 
 * to defend the 6 cities and themselves from the falling rockets. Level one starts
 * with 10 rockets coming down, and each level after that one more rocket is added.
 * When the player beats levels that are a multiple of 10, they move to a new phase.
 * At the beginning of a phase the rocket count is set back to 10, but the rockets 
 * speed up. The scores also get multiplied by the current phase. The scores start
 * out as 100 for missiles the player distroys. At the end of the level, the level
 * the player gets a bonus of 200 points for every rocket they did not fire and
 * 500 for every city they have left. Everytime they score a multiple of 10000 points 
 * they get a new city back, which is added at the start of each level. Every multiple
 * of 30000 they get a new anti-air. Firing anti-air distroys all in flight missiles,
 * but no points are added.
 */
package csm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Missile Command Game.
 * @author Zach
 */
public class Ex01Main extends JFrame {

  private static int stepC = 70;
  private static int stepS = 60;
  private static final int LINE_WIDTH = 6;
  private static int wHeight;
  private static int wWidth;
  private static final int MARKER_STROKE = 1;
  private static final int MARKER_WIDTH = 4;
  private int[][] buildingX = new int[9][1];
  private static int[] towerY;
  private static int TOWER_HEIGHT;
  private static final Random RAND = new Random();
  private static final int[] DESTROIED_TARGETS = new int[9];
  private static Position[] TARGET_LOCATIONS = new Position[9];
  private static int COUNT = 0;
  private static int NUM_TO_DROP;
  private static int TOP_POSITION;
  private static int TARGET;
  private static int DROP_VEL = 5;
  private static boolean START = false;
  private static int BOMBS_PER_LEVEL = 10;
  private static int LAST_LEVEL = 10;
  private static int START_BOMB_NUM = 10;
  private static boolean LEVEL_COMPLETE = false;
  private static Font ft;
  private static FontMetrics ftInfo;
  private static int FRAME = 0;
  private static String TITLE = "MISSILE COMMAND";
  private static String COMPLETE = "LEVEL COMPLETE";
  private static String OVER = "GAME OVER";
  private static final int NUM_TOWERS = 3;
  private static final int NUM_TARGETS = 9;
  private static boolean GAME_OVER = false;
  private static String TOP_BANNER;
  private static int SCORE = 0;
  private static int LEVEL = 1;
  private static int EXTRA_CITIES = 0;
  private static int ANTI_AIR = 1;
  private static int TOP_BANNER_HEIGHT;
  private static boolean CONTINUE = false;
  private static int NEW_ANTI_AIR = 0;
  private static int NEW_CITY = 0;
  private static int PHASE = 1;
  private static final int NUM_ROCKETS = 10;

  /**
   * Holds the information of what is happening on the ground. Any interaction
   * with the cities or the tower is done through this class.
   */
  private class Bases {

    // Holder for the number of rockets at each tower
    private final int[] rocketCount = new int[NUM_TOWERS];
    // Keeps track of what towers or cities are destroyed
    // Towers 0-2, Cities 3-8
    private final boolean[] destroyed = new boolean[NUM_TARGETS];
    private final int[] centers = new int[NUM_TARGETS];
    int fontHeight;

    /**
     * Constructor for the bases. Sets all the targets as not destroyed, and
     * makes the towers have a full rocket count.
     */
    public Bases() {
      Arrays.fill(destroyed, false);
      Arrays.fill(rocketCount, NUM_ROCKETS);
    }

    /**
     * Simulates a rocket being fired from one of the towers.
     *
     * @param tower The tower from which the rocket was fired
     * @return True if a rocket was fired, False if not.
     */
    public boolean fire(int tower) {
      // If the tower has any rockets left to fire.
      if (rocketCount[tower] >= 1) {
        // Fire a rocket
        rocketCount[tower]--;
        return true;
      }
      return false;
    }

    /**
     * At the end of each level, the rocket count in each of the towers needs
     * reset.
     *
     */
    public void reset() {
      // Goes through all the rocket counts and set them to starting value
      for (int i = 0; i < NUM_TOWERS; i++) {
        rocketCount[i] = NUM_ROCKETS;
        DESTROIED_TARGETS[i] = 0;
      }
    }

    /**
     * Sums the total number of rockets left at the end of the level for
     * scoring.
     *
     * @return The total number of rockets
     */
    public int rocketsLeft() {
      return rocketCount[0] + rocketCount[1] + rocketCount[2];
    }

    /**
     * Counts the number of cities left in the game.
     *
     * @return The number of cities left in the game.
     */
    public int citiesLeft() {
      int cityCount = 0;
      // Goes through all the cities
      for (int city = 3; city < NUM_TARGETS; city++) {
        // If it is not destroyed
        if (!destroyed[city]) {
          // Add to count
          cityCount++;
        }
      }
      return cityCount;
    }

    /**
     * If an integer multiple of 10000 points is scored, a city is added back.
     */
    public void addCity() {
      // Goes through the cities
      for (int city = 3; city < NUM_TARGETS; city++) {
        // If it finds a destroyed city
        if (destroyed[city]) {
          // Restores the city
          destroyed[city] = false;
          DESTROIED_TARGETS[city] = 0;
          return;
        }
      }
    }

    /**
     * Simulates that either a tower or a city has been directly hit.
     *
     * @param target The index of the target that was hit
     */
    public void hit(int target) {
      // Marks the target as destroyed
      destroyed[target] = true;
      // If it was a tower destroys all of the rockets
      if (target < NUM_TOWERS) {
        rocketCount[target] = 0;
      }
    }

    /**
     * The game is over when all of the cities have been destroyed, checks to
     * see if all of the cities have been destroyed.
     *
     * @return True if the game is over false otherwise
     */
    public boolean gameOver() {
      // Goes through all the cities
      for (int build = 3; build < NUM_TARGETS; build++) {
        // If it finds one city that is not destroyed
        if (!destroyed[build]) {
          // Then the game is not over
          return false;
        }
      }
      return true;
    }

    /**
     * Draws the ground state. This is the towers and the cities
     *
     * @param graphic The Graphics object for the drawing
     */
    public void draw(Graphics graphic) {
      // Sets the font based on the window
      if (wWidth < wHeight) {
        ft = new Font("Monospaced", Font.PLAIN, wWidth / 32);
      } else {
        ft = new Font("Monospaced", Font.PLAIN, wHeight / 32);
      }
      graphic.setFont(ft);

      // Used to access font info for printing
      ftInfo = graphic.getFontMetrics(ft);

      // Constant for the center of the buildings
      centers[0] = wWidth / 16;
      centers[1] = wWidth / 2;
      centers[2] = wWidth / 16 * 15;
      centers[3] = wWidth / 32 * 6;
      centers[4] = wWidth / 32 * 9;
      centers[5] = wWidth / 32 * 12;
      centers[6] = wWidth / 32 * 20;
      centers[7] = wWidth / 32 * 23;
      centers[8] = wWidth / 32 * 26;
      // Constants for the cities
      int cityWidth = wWidth / 32;
      int cityHeight = wHeight / 32 * 29;
      int[] cityY = new int[]{wHeight, cityHeight, wHeight};

      // Constants for the towers
      TOWER_HEIGHT = wHeight / 8 * 7;
      fontHeight = wHeight / 64 * 62;
      towerY = new int[]{wHeight, TOWER_HEIGHT, wHeight};

      // Saves the location of the tops of the building, so they can be used as targets
      for (int target = 0; target < NUM_TARGETS; target++) {
        if (target <= 2) {
          TARGET_LOCATIONS[target] = new Position(centers[target], TOWER_HEIGHT);
        } else {
          TARGET_LOCATIONS[target] = new Position(centers[target], cityHeight);
        }
      }

      // Goes through and draws all of the buildings
      for (int build = 0; build < NUM_TARGETS; build++) {
        // If it is a tower
        if (build <= 2) {
          buildingX[build] = new int[]{centers[build] - centers[0], centers[build],
            centers[build] + centers[0]};
          // Sets the color for the towers to green
          graphic.setColor(Color.green);
          // Draws the tower
          graphic.fillPolygon(buildingX[build], towerY, 3);
          // Sets the color to black
          graphic.setColor(Color.black);
          // Draws the number of rockets left at the tower on the tower
          graphic.drawString("" + rocketCount[build],
                  buildingX[build][0] + (buildingX[build][2] - buildingX[build][0]
                  - ftInfo.stringWidth("" + rocketCount[build])) / 2,
                  (wHeight + towerY[1]) / 2);
          // It is a city
        } else {
          buildingX[build] = new int[]{centers[build] - cityWidth, centers[build],
            centers[build] + cityWidth};
          // If the city is not destroyed, draws the city
          if (DESTROIED_TARGETS[build] == 0) {
            graphic.setColor(Color.CYAN);
            graphic.fillPolygon(buildingX[build], cityY, 3);
          }
        }
      }
    }
  }

  /**
   * Keeps track of all of the rockets that are in flight.
   */
  private class InFlightRocket {

    // Starting position
    private Position start;
    // Target position
    private Position end;
    // Currently located at
    private Position curPos;
    private Position temp;
    // Velocity int x direction
    private double velocityX;
    // Velocity in y direction
    private double velocityY;
    // A list of all the locations traveled
    private final List<Path> fullPath = new LinkedList<>();
    // Direction heading up or down
    private int direction;
    // Target number
    int target;

    /**
     * Constructor for a rocket that is in flight.
     *
     * @param startX Starting x cor
     * @param startY Starting y cor
     * @param endX Ending x cor
     * @param endY Ending y cor
     * @param vel velocity the rocket is to travel
     * @param dir direction the rocket is traveling
     * @param target The number of its target
     */
    public InFlightRocket(int startX, int startY, int endX, int endY, int vel,
            int dir, int target) {
      direction = dir;
      start = new Position(startX, startY);
      end = new Position(endX, endY);
      curPos = new Position(startX, startY);
      // Decomposes the flight path of the rocket
      int xv;
      int yv;
      xv = startX - endX;
      yv = startY - endY;
      double totalDist;
      totalDist = Math.sqrt(xv * xv + yv * yv);
      double numSteps;
      numSteps = (double) (totalDist / vel);
      velocityX = ((double) (endX - startX) / numSteps);
      velocityY = ((double) (startY - endY) / numSteps);
      this.target = target;
    }

    /**
     * Constructor for rockets coming down. Calls other constructor with
     * completed list of args
     *
     * @param start The starting position of the rocket
     * @param end The ending position of the rocket
     * @param target The target number
     */
    public InFlightRocket(Position start, Position end, int target) {
      this((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY(),
              DROP_VEL, -1, target);
    }

    /**
     * Simulates the motion of a rocket in a frame.
     */
    public void move() {
      // Holder for the current position of the rocket
      temp = new Position(curPos.getX(), curPos.getY());
      // Moves the rocket in the x direction
      curPos.setX(curPos.getX() + velocityX);
      // If the rocket is going up
      if (direction == 1) {
        // If moving the rocket will move it past the target, set location to target
        if (end.getY() >= curPos.getY() - velocityY) {
          curPos.setY(end.getY());
          curPos.setX(end.getX());

          // Else just move the rocket up
        } else {
          curPos.setY(curPos.getY() - velocityY);
        }
        //If the rocket is coming down
      } else {
        // If the motion in the move it past it's target if so set to target
        if (end.getY() <= curPos.getY() - velocityY) {
          curPos.setY(end.getY());
          curPos.setX(end.getX());

          // else move the rocket down
        } else {
          curPos.setY(curPos.getY() - velocityY);
        }
      }
      // Adds the old postion and the new postion to the list of paths
      fullPath.add(new Path(new Position(temp.getX(), temp.getY()),
              new Position(curPos.getX(), curPos.getY())));
    }

    /**
     * Gives the target of the rocket.
     *
     * @return Target of the rocket
     */
    public int target() {
      return target;
    }

    /**
     * Draws the in flight rocket.
     *
     * @param graphic Graphics object for drawing.
     */
    public void draw(Graphics graphic) {
      Position pos1;
      Position pos2;
      // If the rocket is going up set the trail to red
      if (direction == 1) {
        graphic.setColor(Color.red);
        // IF the rocket is going down set trail to Magenta
      } else {
        graphic.setColor(Color.MAGENTA);
      }
      // Graphics2D need for thicker lines
      Graphics2D graphic2 = (Graphics2D) graphic;
      // Sets the thickness
      graphic2.setStroke(new BasicStroke(LINE_WIDTH));

      // Goes through all of the sub paths in the full path
      for (Path path : fullPath) {
        // Position holders
        pos1 = path.getStart();
        pos2 = path.getEnd();
        // Draws the sub path
        graphic2.draw(new Line2D.Float((int) pos1.getX(), (int) pos1.getY(),
                (int) pos2.getX(), (int) pos2.getY()));
      }

      // If it is a rocket that the player fired, place marker on target
      if (direction == 1) {
        graphic2.setStroke(new BasicStroke(MARKER_STROKE));
        graphic2.drawOval((int) end.getX() - MARKER_WIDTH / 2,
                (int) end.getY() - MARKER_WIDTH / 2, MARKER_WIDTH, MARKER_WIDTH);
      }
    }

    /**
     * Test to see whether a rocket has reached it's target.
     *
     * @return True if reached
     */
    public boolean toTarget() {
      if (direction == 1) {
        return curPos.getY() <= end.getY();
      } else {
        return curPos.getY() >= end.getY();
      }

    }

    /**
     * Returns current position of the rocket.
     *
     * @return Position of the rocket
     */
    public Position cur() {
      return curPos;
    }

    /**
     * Returns the direction of the rocket.
     *
     * @return 1 or -1
     */
    public int dir() {
      return direction;
    }
  }

  /**
   * Controls the explosion of all of the rockets.
   */
  private class Exploding {

    // Number of frams the explosion lasts
    private int framesToLive = 60;
    // Center of the explosion
    private Position center;
    // Increase in the size of the explosion by fram
    private int size = 2;

    /**
     * Constructor for the explosion.
     *
     * @param center Position object for the center of the explosion
     */
    public Exploding(Position center) {
      this.center = new Position(center.getX(), center.getY());
    }

    /**
     * Increases the size of the explosion and decreases the frames left to
     * live.
     */
    public void move() {
      framesToLive--;
      size += 2;
    }

    /**
     * Draws the explosion.
     *
     * @param graphic The Graphics object for the drawing.
     */
    public void draw(Graphics graphic) {
      // Sets explosion color
      graphic.setColor(Color.yellow);
      // Draws the explosion
      graphic.fillOval((int) center.getX() - size / 2,
              (int) center.getY() - size / 2, size, size);
    }

    /**
     * Checks to see if the explosion is done.
     *
     * @return True if done
     */
    public boolean timeUp() {
      return framesToLive <= 0;
    }

    /**
     * The radius of the explosion.
     *
     * @return int radius
     */
    public int blastRadius() {
      return size / 2;
    }

    /**
     * The center of the explosion.
     *
     * @return Position center of explosion
     */
    public Position center() {
      return center;
    }
  }

  /**
   * Calculates the distance between two input positions.
   *
   * @param pos1 Position point 1
   * @param pos2 Position point 2
   *
   * @return The distance between two points
   */
  private static double distance(Position pos1, Position pos2) {
    double difX = pos1.getX() - pos2.getX();
    double difY = pos1.getY() - pos2.getY();

    return Math.sqrt(difX * difX + difY * difY);

  }

  /**
   * Controls the drawing on the screen.
   */
  private class DrawPanel extends JPanel {

    // Holds fired rockets
    private final List<InFlightRocket> fired = new LinkedList<>();
    // Mouse positons
    private int mouseX;
    private int mouseY;
    // Tracks to see if buttons have been pressed
    private boolean pressedR = false;
    private boolean leftPressed = false;
    private boolean centerPressed = false;
    private boolean rightPressed = false;
    // Holder for objects to be removed
    private List deadList = new LinkedList();
    private List deadList2 = new LinkedList();
    private List addList = new LinkedList();
    private List deadList3 = new LinkedList();
    // Holder for inAir explosions
    private List<Exploding> inAir = new LinkedList<>();
    // Holder for at building explosions
    private List<Exploding> atBuilding = new LinkedList<>();

    /**
     * Construction for the panel.
     */
    public DrawPanel() {
      super.setBackground(Color.black);

      super.addMouseListener(new MouseAdapter() {

        /**
         * Overrides what happens when the mouse is clicked. When the mouse is
         * clicked. If the game has not yet started, the game will start. If the
         * game has started and it is at the end of the level it will continue
         * to next level.
         *
         * @param ev MouseEvent object
         */
        @Override
        public void mousePressed(MouseEvent ev) {
          // If the game has started and the level is over.
          if (START && LEVEL_COMPLETE) {
            // Continue to next level
            CONTINUE = true;
            // The game has not yet started
          } else {
            // Start the game
            START = true;
          }
          // Update the screen
          repaint();
        }
      });

      super.addKeyListener(new KeyAdapter() {
        /**
         * Changes what happens when a button is pressed. There are 3 choices
         * for firing awd, and one choice for anti air r. The rest of the keys
         * are ignored.
         *
         * @param ev KeyEvent object
         */
        @Override
        public void keyPressed(KeyEvent ev) {

          // IF the level is not complete
          if (!LEVEL_COMPLETE) {
            // See what key was pressed
            switch (ev.getKeyCode()) {
              // A key pressed
              case KeyEvent.VK_A:
                // If the key was not already pressed, and there are rockets left
                // to fire.
                if (!leftPressed && bases.fire(0)) {
                  // Fire a rocket from a at mouse position
                  fired.add(new InFlightRocket(buildingX[0][1], towerY[1], mouseX,
                          mouseY, stepS, 1, 10));
                }
                // Mark the key as currently pressed
                leftPressed = true;
                break;
              // W pressed
              case KeyEvent.VK_W:
                // If the key was not already pressed, and there are rockets left
                // to fire.
                if (!centerPressed && bases.fire(1)) {
                  // Fire a rocket from a at mouse position
                  fired.add(new InFlightRocket(buildingX[1][1], towerY[1], mouseX, 
                          mouseY, stepC, 1, 10));
                  //System.out.println("W");
                }
                centerPressed = true;
                break;
              // D pressed
              case KeyEvent.VK_D:
                // If the key was not already pressed, and there are rockets left
                // to fire.
                if (!rightPressed && bases.fire(2)) {
                  // Fire a rocket from a at mouse position
                  fired.add(new InFlightRocket(buildingX[2][1], towerY[1], mouseX, 
                          mouseY, stepS, 1, 10));
                }
                // Mark the button as currently pressed
                rightPressed = true;
                break;
              // R pressed
              case KeyEvent.VK_R:
                // If R was not already pressed and there are still anti-air to use
                if (!pressedR && ANTI_AIR > 0) {
                  // Use anti-air
                  ANTI_AIR--;
                  // Destroy all in ait rockets
                  for (InFlightRocket rocket : fired) {
                    atBuilding.add(new Exploding(rocket.cur()));
                    deadList3.add(rocket);
                  }
                  fired.removeAll(deadList3);
                  deadList3.clear();
                  repaint();
                }
                break;
                
              default:
                break;
            }
          }
        }

        /**
         * If a pressed key is released, mark it as released.
         *
         * @param ev key release event
         */
        @Override
        public void keyReleased(KeyEvent ev) {
          if (!LEVEL_COMPLETE) {
            switch (ev.getKeyCode()) {
              case KeyEvent.VK_A:
                leftPressed = false;
                break;
              case KeyEvent.VK_W:
                centerPressed = false;
                break;
              case KeyEvent.VK_D:
                rightPressed = false;
                break;
              case KeyEvent.VK_R:
                pressedR = false;
                break;
              default:
                break;
            }
          }
        }
      });
      super.addMouseMotionListener(new MouseMotionAdapter() {
        /**
         * When the mouse is pressed update its position in the global
         * variables.
         *
         * @param ev MouseEvent object
         */
        @Override
        public void mouseMoved(MouseEvent ev
        ) {
          mouseX = ev.getX();
          if (ev.getY() < TOWER_HEIGHT) {
            mouseY = ev.getY();
          } else {
            mouseY = TOWER_HEIGHT - 1;
          }

          repaint();
        }
      }
      );

      Timer animTimer = new Timer(33, new ActionListener() {

        /**
         * Holds all the actions that are to be preformed at 30 frames a second.
         *
         * @param ev Action event object
         */
        @Override
        public void actionPerformed(ActionEvent ev) {
          // If the game has started
          if (START) {
            // Check to see if more anti air need added
            if (NEW_ANTI_AIR >= 30000) {
              ANTI_AIR++;
              NEW_ANTI_AIR = SCORE % 30000;
            }
            // Check to see if cities need added back
            if (NEW_CITY >= 10000) {
              EXTRA_CITIES++;
              NEW_CITY = SCORE % 10000;
            }
            deadList = new LinkedList();
            deadList2 = new LinkedList();
            addList = new LinkedList();
            if (bases.citiesLeft() == 0) {
              BOMBS_PER_LEVEL = 0;
            }
            // Creates a pause between rockets dropped from above.
            if (COUNT % 30 == 0 && bases.citiesLeft() > 0) {
              // Generate a number between 1-8
              NUM_TO_DROP = RAND.nextInt(8) + 1;
              // If the number is 1-4
              if (NUM_TO_DROP <= 4) {
                // If there are at least this number bombs
                if (NUM_TO_DROP <= BOMBS_PER_LEVEL) {
                  // Decrease the count by generated number
                  BOMBS_PER_LEVEL -= NUM_TO_DROP;
                  // If there are less bombs left than generated number
                } else {
                  // Set the num to drop to the total left
                  NUM_TO_DROP = BOMBS_PER_LEVEL;
                  // Make is so there are not any bombs left
                  BOMBS_PER_LEVEL = 0;
                }
                // Fires the selected number of bombs
                for (int bombCount = 0; bombCount < NUM_TO_DROP; bombCount++) {
                  // Randomly chooses a location on the top of the screen.
                  TOP_POSITION = RAND.nextInt(wWidth);
                  // Randomly guesses a target
                  TARGET = RAND.nextInt(9);
                  // Continues to guess a target until it finds one that is 
                  // not destroyed.
                  while (DESTROIED_TARGETS[TARGET] != 0) {
                    TARGET = RAND.nextInt(9);
                  }
                  // Fires the rocket
                  fired.add(new InFlightRocket(new Position(TOP_POSITION, 
                          TOP_BANNER_HEIGHT * 3 / 4 + LINE_WIDTH), 
                          TARGET_LOCATIONS[TARGET], TARGET));
                }
              }
            }

            // Goes through all of the launched rockets
            for (InFlightRocket rocket : fired) {
              // Moves the rocket
              rocket.move();

              // If the rocket has reached its target
              if (rocket.toTarget()) {
                // Check to see if it was a dropped bomb
                // The user should not get points for explosions on bases
                if (rocket.dir() == -1) {
                  // Mark the target as hit
                  bases.hit(rocket.target());
                  // Add to explosions happening at buildings
                  atBuilding.add(new Exploding(rocket.cur()));
                  // Mark target as destroyed
                  DESTROIED_TARGETS[rocket.target()] = 1;
                  // in air explosion 
                } else {
                  inAir.add(new Exploding(rocket.cur()));
                }
                // in either case the rocket blew up so add to deadlist
                deadList.add(rocket);
              }
            }

            // Moves in air explosions
            for (Exploding exp : inAir) {
              exp.move();
              // Goes through all in flight rockets
              for (InFlightRocket rocket : fired) {
                // If the rocket is falling
                if (rocket.dir() == -1) {
                  // If the rocket is in the blast radius of the explosion
                  if (exp.blastRadius() >= distance(exp.center(), rocket.cur())) {
                    // remove rocket
                    deadList.add(rocket);
                    // Add to in air explosions
                    addList.add(new Exploding(rocket.cur()));
                    // GIve the user points
                    SCORE += (100 * PHASE);
                    NEW_ANTI_AIR += (100 * PHASE);
                    NEW_CITY += (100 * PHASE);
                  }
                }
              }
              // If it is done exploding remove it
              if (exp.timeUp()) {
                deadList2.add(exp);
              }
            }
            inAir.addAll(addList);
            addList.clear();

            // Goes through all the explosions at the buildings 
            for (Exploding exp : atBuilding) {
              exp.move();
              // Goes through all the rockets
              for (InFlightRocket rocket : fired) {
                // If the rocket is coming down
                if (rocket.dir() == -1) {
                  // if the rocket is in the blast radius, blow it up
                  if (exp.blastRadius() >= distance(exp.center(), rocket.cur())) {
                    deadList.add(rocket);
                    addList.add(new Exploding(rocket.cur()));
                  }
                }
              }
              // If the explosion is done blow it up
              if (exp.timeUp()) {
                deadList2.add(exp);
              }
            }
            atBuilding.addAll(addList);

            // If the animation is done and there are no more bombs to drop
            if (fired.isEmpty() && BOMBS_PER_LEVEL == 0 && inAir.isEmpty()
                    && atBuilding.isEmpty()) {
              // The level is over
              LEVEL_COMPLETE = true;
            }

            // If the game is over and there are not any extra cities
            if (bases.gameOver() && EXTRA_CITIES == 0) {
              // Set the game to over 
              GAME_OVER = true;
            }

            COUNT++;
          }
          // Update the window
          repaint();

        }
      });
      // Starts the animation
      animTimer.start();
    }

    /**
     * Draws everything on the screen.
     *
     * @param graphic Graphics object
     */
    @Override
    protected void paintComponent(Graphics graphic) {
      int topPush;
      int citiesLeft;
      String message;
      super.paintComponent(graphic);
      // If the level is complete
      if (LEVEL_COMPLETE) {
        // If the game is over
        if (GAME_OVER) {
          // Display game over message
          graphic.setColor(Color.RED);
          ft = new Font("Monospaced", Font.PLAIN, wWidth / OVER.length());
          graphic.setFont(ft);
          ftInfo = graphic.getFontMetrics(ft);
          graphic.drawString(OVER, (wWidth - ftInfo.stringWidth(OVER)) / 2, wHeight / 2);
        } else {
          // Display success message
          graphic.setColor(Color.GREEN);
          ft = new Font("Monospaced", Font.PLAIN, wWidth / COMPLETE.length());
          graphic.setFont(ft);
          ftInfo = graphic.getFontMetrics(ft);
          topPush = TOP_BANNER_HEIGHT + ftInfo.getHeight();
          // If a phase has been completed
          if (LAST_LEVEL == 19) {
            graphic.setColor(Color.blue);
            message = "PHASE " + (PHASE) + " COMPLETE";
            graphic.drawString(message, (wWidth - ftInfo.stringWidth(message)) / 2, topPush);

            // Else just a level was completed
          } else {
            graphic.drawString(COMPLETE, (wWidth - ftInfo.stringWidth(COMPLETE)) / 2, topPush);
          }

          // Set the sub text information
          String continueMessage = "Click to continue";
          ft = new Font("Monospaced", Font.PLAIN, wWidth / continueMessage.length());
          graphic.setFont(ft);
          ftInfo = graphic.getFontMetrics(ft);
          // If one or more cities were given back display this
          if (bases.citiesLeft() < 6 && EXTRA_CITIES >= 1) {
            message = "CITY GIVEN BACK";
            graphic.setColor(Color.CYAN);
            graphic.drawString(message, (wWidth - ftInfo.stringWidth(message)) / 2, 
                    topPush + ftInfo.getHeight());
          }
          graphic.setColor(Color.gray);
          // Display bonus for left over rockets
          message = "ROCKET BONUS: " + (200 * bases.rocketsLeft() * PHASE);
          graphic.drawString(message, (wWidth - ftInfo.stringWidth(message)) / 2, 
                  topPush + 2 * ftInfo.getHeight());
          // Display bonus for left over cities
          message = "CITY BONUS: " + (bases.citiesLeft() * 500 * PHASE);
          graphic.drawString(message, (wWidth - ftInfo.stringWidth(message)) / 2, 
                  topPush + 3 * ftInfo.getHeight());
          graphic.drawString(continueMessage, 
                  (wWidth - ftInfo.stringWidth(continueMessage)) / 2, 
                  topPush + 4 * ftInfo.getHeight());
          // When the user clicks to continue
          if (CONTINUE) {
            int used = 0;
            // Add all the cities back that can be
            for (int i = 0; i < EXTRA_CITIES; i++) {
              if (bases.citiesLeft() < 6) {
                bases.addCity();
                used++;
              }
            }
            EXTRA_CITIES -= used; // Take away the num added back
            CONTINUE = false;
            // Update score for left over rockets
            SCORE += (200 * bases.rocketsLeft() * PHASE);
            NEW_ANTI_AIR += (200 * bases.rocketsLeft() * PHASE);
            NEW_CITY += (200 * bases.rocketsLeft() * PHASE);
            // Update score for left over cities
            SCORE += (bases.citiesLeft() * 500 * PHASE);
            NEW_ANTI_AIR += (bases.citiesLeft() * 500 * PHASE);
            NEW_CITY += (bases.citiesLeft() * 500 * PHASE);
            // If the phase is not complete added another rocket
            if (LAST_LEVEL < 19) {
              LAST_LEVEL++;
              // If the phase is complete
            } else {
              // Increase the speed of the rockets
              DROP_VEL += 2;
              // Reset back to 10 rockets
              LAST_LEVEL = START_BOMB_NUM;
              // Increase phase number
              PHASE++;
            }
            // Increase level
            LEVEL++;
            // Reset number of bombs to drop
            BOMBS_PER_LEVEL = LAST_LEVEL;
            LEVEL_COMPLETE = false;
            bases.reset();
          }
        }
      }

      // If the game has started 
      if (START) {
        // draw the background
        bases.draw(graphic);

        // Set the text color and font size
        graphic.setColor(Color.gray);
        if (wHeight < wWidth) {
          ft = new Font("Monospaced", Font.PLAIN, wHeight / 32);
        } else {
          ft = new Font("Monospaced", Font.PLAIN, wHeight / 32);
        }
        graphic.setFont(ft);
        ftInfo = graphic.getFontMetrics(ft);
        TOP_BANNER_HEIGHT = ftInfo.getHeight();
        // Draws the top banner
        TOP_BANNER = "SCORE: " + SCORE + " LV: " + LEVEL + " PHASE: " + PHASE 
                + " EXTRA CITIES: " + EXTRA_CITIES + "  ANTI-AIR: " + ANTI_AIR;
        graphic.drawString(TOP_BANNER, 0, ftInfo.getHeight() * 3 / 4);

        // Draws all in flight rockets
        for (InFlightRocket rocket : fired) {
          rocket.draw(graphic);
        }
        // Makes sure that the rocket reaches the target before exploding
        fired.removeAll(deadList);
        // Draws all in air explosions
        for (Exploding exp : inAir) {
          exp.draw(graphic);
        }
        // Draws all at building explosions
        for (Exploding exp : atBuilding) {
          exp.draw(graphic);
        }
        inAir.removeAll(deadList2);
        atBuilding.removeAll(deadList2);

        // Displays title screen
      } else if (FRAME != 0) {
        graphic.setColor(Color.red);
        ft = new Font("Monospaced", Font.PLAIN, wWidth / TITLE.length());
        graphic.setFont(ft);
        ftInfo = graphic.getFontMetrics(ft);
        graphic.drawString(TITLE, (wWidth - ftInfo.stringWidth(TITLE)) / 2, 
                wWidth / TITLE.length());

        
        
        
        
        String startMessage = "Click the mouse to start";
        graphic.setColor(Color.gray);
        ft = new Font("Monospaced", Font.PLAIN, wWidth / startMessage.length());
        graphic.setFont(ft);
        ftInfo = graphic.getFontMetrics(ft);
        String instruct1 = "a : left fire";
        String instruct3 = "d : right fire";
        graphic.drawString(instruct1, (wWidth - ftInfo.stringWidth(instruct3)) / 2, 
                wWidth / TITLE.length() + wWidth / instruct3.length());
        String instruct2 = "w : center fire";
        graphic.drawString(instruct2, (wWidth - ftInfo.stringWidth(instruct3)) / 2, 
                wWidth / TITLE.length() + 2 * wWidth / instruct3.length());
        
        graphic.drawString(instruct3, (wWidth - ftInfo.stringWidth(instruct3)) / 2, 
                wWidth / TITLE.length() + 3 * wWidth / instruct3.length());
        String instruct4 = "r : anti - air";
        graphic.drawString(instruct4, (wWidth - ftInfo.stringWidth(instruct3)) / 2, 
                wWidth / TITLE.length() + 4 * wWidth / instruct3.length());
        graphic.drawString(startMessage, (wWidth - ftInfo.stringWidth(startMessage)) / 2, 
                wWidth / TITLE.length() + 5 * wWidth / instruct3.length());
      }
      FRAME++;

    }
  }

  DrawPanel panel;
  Bases bases;

  /**
   * Constructor for the main class.
   */
  public Ex01Main() {
    super.setDefaultCloseOperation(EXIT_ON_CLOSE);
    //super.setSize(0, 0);
    super.setExtendedState(JFrame.MAXIMIZED_BOTH);

    bases = new Bases();

    panel = new DrawPanel();
    super.add(panel);
    //super.setResizable(false);
    wHeight = getHeight();
    wWidth = getWidth();
    super.addComponentListener(new ComponentAdapter() {
      /**
       * When the window is resized the text on the button will be resized as
       * well.
       *
       * @param evt The ComponentEvent object
       */
      @Override
      public void componentResized(ComponentEvent evt) {
        wHeight = getHeight();
        wWidth = getWidth();
      }
    });
  }

  /**
   * Makes the window request focus when created.
   *
   * @param vis boolean for setting to be seen
   */
  @Override
  public void setVisible(boolean vis) {
    super.setVisible(vis);
    panel.requestFocusInWindow();
  }

  /**
   * Main Method. 
   * 
   * @param args the command line arguments
   */
  public static void main(String[] args) {

    EventQueue.invokeLater(new Runnable() {

      /**
       * Runs the program when the screen is ready.
       */
      @Override
      public void run() {
        Ex01Main frame = new Ex01Main();
        frame.setVisible(true);
      }
    });
  }

}
