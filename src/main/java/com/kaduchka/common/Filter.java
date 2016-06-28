package com.kaduchka.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Filter implements Serializable {
  private Long filterId;
  private String filterNumber;
  private Date filterDate;
  private BigDecimal filterAmount;

  public Filter() {
  }

  public Long getFilterId() {
    return filterId;
  }

  public void setFilterId(Long filterId) {
    this.filterId = filterId;
  }

  public String getFilterNumber() {
    return filterNumber;
  }

  public void setFilterNumber(String filterNumber) {
    this.filterNumber = filterNumber;
  }

  public Date getFilterDate() {
    return filterDate;
  }

  public void setFilterDate(Date filterDate) {
    this.filterDate = filterDate;
  }

  public BigDecimal getFilterAmount() {
    return filterAmount;
  }

  public void setFilterAmount(BigDecimal filterAmount) {
    this.filterAmount = filterAmount;
  }
}
