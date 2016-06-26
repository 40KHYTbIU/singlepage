package com.kaduchka.common;

import java.io.Serializable;
import java.util.Date;

public class Filter {
  private Long id;
  private String name;
  private Date date;

  public Filter() {
  }

  public Filter(Long id, String name, Date date) {
    this.id = id;
    this.name = name;
    this.date = date;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
