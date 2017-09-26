/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package csm;

/**
 * Keeps track of the position of objects in the game.
 */
public class Position {

  private double xval;
  private double yval;

  /**
   * Constructor for the position.
   *
   * @param xval x cor
   * @param yval y cor
   */
  public Position(double xval, double yval) {
    this.xval = xval;
    this.yval = yval;
  }

  /**
   * Gives user x val.
   *
   * @return xval
   */
  public double getX() {
    return xval;
  }

  /**
   * Gives the user the yval.
   *
   * @return yval
   */
  public double getY() {
    return yval;
  }

  /**
   * Sets the xval.
   *
   * @param xval x cor
   */
  public void setX(double xval) {
    this.xval = xval;
  }

  /**
   * Sets the yval.
   *
   * @param yval y cor
   */
  public void setY(double yval) {
    this.yval = yval;
  }

}
