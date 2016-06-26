package com.kaduchka.controllers;

import com.kaduchka.common.Fields;
import com.kaduchka.common.Filter;
import com.kaduchka.common.Record;
import com.kaduchka.common.Sort;
import com.kaduchka.dao.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Date;

@RestController
public class RecordsController {

  @Autowired
  RecordRepository recordRepository;

  @RequestMapping("/records")
  public Collection<Record> getList(
    @RequestParam(value = "idFilter", required = false) Long idFilter,
    @RequestParam(value = "nameFilter", required = false) String nameFilter,
    @RequestParam(value = "dateFilter", required = false) Date dateFilter,
    @RequestParam(value = "sortField", required = false) Fields sortField,
    @RequestParam(value = "sortDirection", required = false) Sort.Direction sortDirection,
    @RequestParam(value = "offset", required = true) int offset,
    @RequestParam(value = "limit", required = true) int limit
  ) {
    Sort sort = new Sort(sortField == null ? Fields.NAME : sortField, sortDirection == null? Sort.Direction.ASC: sortDirection);

    Filter filter = new Filter(idFilter, nameFilter, dateFilter);
    return recordRepository.getRecords(filter, sort, offset, limit);
  }

}
