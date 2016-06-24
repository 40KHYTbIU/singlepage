package com.kaduchka.common;

import java.util.Date;

public class Record {
  private long id;
  private String name;
  private Date date;

  public Record(long id, String name, Date date) {
    this.id = id;
    this.name = name;
    this.date = date;
  }

  public long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public Date getDate() {
    return date;
  }

}
