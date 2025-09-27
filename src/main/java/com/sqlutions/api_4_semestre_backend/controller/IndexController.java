package com.sqlutions.api_4_semestre_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sqlutions.api_4_semestre_backend.service.IndexService;

@RestController
@RequestMapping("/indexes")
public class IndexController {
    @Autowired
    IndexService indexService;

    @GetMapping
    public Integer getCityIndex() {
        return indexService.getCityIndex();
    }
}
