package com.vinaacademy.platform.feature.log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinaacademy.platform.feature.common.utils.JsonUtils;
import com.vinaacademy.platform.feature.log.Log;
import com.vinaacademy.platform.feature.log.LogMapper;
import com.vinaacademy.platform.feature.log.LogRepository;
import com.vinaacademy.platform.feature.log.dto.LogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
class InternalLogService {
    private final LogRepository logRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String name, String event, String username, String description, Object oldData, Object newData) {
        try {
            LogDto logDto = LogDto.builder()
                    .name(name)
                    .event(event)
                    .username(username)
                    .description(description)
                    .oldData(JsonUtils.object2Json(objectMapper, oldData))
                    .newData(JsonUtils.object2Json(objectMapper, newData))
                    .build();
            Log log = LogMapper.INSTANCE.toEntity(logDto);
            logRepository.save(log);
        } catch (Exception e) {
            log.error("Error when add log {}", e.getMessage());
        }
    }
}