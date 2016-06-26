package com.kaduchka.common;

import java.io.Serializable;

public class Sort {
   public enum Direction {
    ASC,
    DESC;
  }

  private Fields field;
  private Direction direction;

  public Sort(Fields field, Direction direction) {
    this.field = field;
    this.direction = direction;
  }

  public Fields getField() {
    return field;
  }

  public void setField(Fields field) {
    this.field = field;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }
}
