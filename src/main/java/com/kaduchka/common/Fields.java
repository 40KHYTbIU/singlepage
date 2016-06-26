package com.kaduchka.common;

public enum Fields {
  ID("id"),
  NAME("name"),
  DATE("date");

  private String name;

  Fields(String name){
    this.name = name;
  }

  public String getName(){
    return this.name;
  }
}
