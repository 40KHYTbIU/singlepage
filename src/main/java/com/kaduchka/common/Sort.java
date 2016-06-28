package com.kaduchka.common;

import java.io.Serializable;

public class Sort implements Serializable {


  private Fields sortField;
  private Direction sortDirection;

  public Sort(Fields sortField, Direction sortDirection) {
    this.sortField = sortField;
    this.sortDirection = sortDirection;
  }

  public Sort() {
  }

  public Fields getSortField() {
    return sortField;
  }

  public void setSortField(Fields sortField) {
    this.sortField = sortField;
  }

  public Direction getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(Direction sortDirection) {
    this.sortDirection = sortDirection;
  }
}
