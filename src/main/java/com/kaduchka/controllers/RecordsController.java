package com.kaduchka.controllers;

import com.kaduchka.common.*;
import com.kaduchka.dao.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@RestController
public class RecordsController {

    @Autowired
    RecordRepository recordRepository;

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Direction.class, new DirectionPropertyEditor());
        webDataBinder.registerCustomEditor(Fields.class, new FieldsPropertyEditor());
    }

    @RequestMapping("/records")
    public Collection<Record> getList(
            Filter filter,
            Sort sort,
            @RequestParam("offset") int offset,
            @RequestParam("limit") int limit
    ) {
        return recordRepository.getRecords(filter, sort, offset, limit);
    }

}
