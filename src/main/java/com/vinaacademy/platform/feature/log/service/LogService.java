package com.vinaacademy.platform.feature.log.service;

import com.vinaacademy.platform.feature.log.dto.LogDto;
import org.springframework.data.domain.Page;

public interface LogService {
    void log(String name, String event, Object oldData, Object newData);

    void log(String name, String event, String description, Object oldData, Object newData);

//    Page<LogDto> search(SearchRequest request);

}
