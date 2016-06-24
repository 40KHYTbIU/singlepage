package com.kaduchka.controllers;

import com.kaduchka.common.Record;
import com.kaduchka.dao.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class RecordsController {

  @Autowired
  RecordRepository recordRepository;

  @RequestMapping("/records")
  public Collection<Record> getList(
    @RequestParam(value="filter", required = false) String filter,
    @RequestParam(value="sortField", required = false) String sortField,
    @RequestParam(value="offset", required = true) int offset,
    @RequestParam(value="limit", required = true) int limit
  ) {
    //TODO: add filter support
    return recordRepository.getRecords(null, sortField, offset, limit);
  }

}
