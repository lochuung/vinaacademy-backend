package com.vinaacademy.platform.feature.log.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinaacademy.platform.feature.common.utils.JsonUtils;
import com.vinaacademy.platform.feature.log.Log;
import com.vinaacademy.platform.feature.log.LogMapper;
import com.vinaacademy.platform.feature.log.LogRepository;
import com.vinaacademy.platform.feature.log.dto.LogDto;
import jakarta.servlet.http.HttpServletRequest;
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

    private final HttpServletRequest httpServletRequest;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String name, String event, String username, String description, Object oldData, Object newData) {
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");

        try {
            LogDto logDto = LogDto.builder()
                    .name(name)
                    .event(event)
                    .username(username)
                    .description(description)
                    .oldData(JsonUtils.object2Json(objectMapper, oldData))
                    .newData(JsonUtils.object2Json(objectMapper, newData))
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
            Log log = LogMapper.INSTANCE.toEntity(logDto);
            logRepository.save(log);
        } catch (Exception e) {
            log.error("Error when add log {}", e.getMessage());
        }
    }
}