/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package csm;


/**
 * Keeps track of a start and end position.
 */
public class Path {

  Position start;
  Position end;

  /**
   * Constructor for a path.
   *
   * @param start The start position
   * @param end The end position
   */
  public Path(Position start, Position end) {
    this.start = start;
    this.end = end;
  }

  /**
   * Gives the user the start position.
   *
   * @return start position
   */
  public Position getStart() {
    return this.start;
  }

  /**
   * Gives the user the end position.
   *
   * @return end position
   */
  public Position getEnd() {
    return this.end;
  }

}
