package com.kaduchka.common;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

public class Record{
  private Long id;
  private String number;
  private Date date;
  private BigDecimal amount;
  private Collection<String> list;

  public Record() {
  }

  public Record(Long id, String number, Date date, BigDecimal amount, Collection<String> list) {
    this.id = id;
    this.number = number;
    this.date = date;
    this.amount = amount;
    this.list = list;
  }

  public Long getId() {
    return id;
  }

  public String getNumber() {
    return number;
  }

  public Date getDate() {
    return date;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Collection<String> getList() {
    return list;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public void setList(Collection<String> list) {
    this.list = list;
  }
}
