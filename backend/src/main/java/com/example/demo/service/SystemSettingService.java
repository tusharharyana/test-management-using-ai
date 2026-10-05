package com.example.demo.service;

import com.example.demo.entity.SystemSetting;
import com.example.demo.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    public SystemSettingService(SystemSettingRepository systemSettingRepository) {
        this.systemSettingRepository = systemSettingRepository;
    }

    public boolean isAiEvaluationEnabled() {
        SystemSetting setting = getOrCreateSetting();
        return Boolean.TRUE.equals(setting.getAiEvaluationEnabled());
    }

    public boolean updateAiEvaluationEnabled(boolean enabled) {
        SystemSetting setting = getOrCreateSetting();

        setting.setAiEvaluationEnabled(enabled);

        systemSettingRepository.save(setting);

        return enabled;
    }

    private SystemSetting getOrCreateSetting() {
        return systemSettingRepository.findById(1L)
                .orElseGet(() -> {
                    SystemSetting setting = new SystemSetting();
                    setting.setAiEvaluationEnabled(false);
                    return systemSettingRepository.save(setting);
                });
    }
}