package com.example.demo.repository;

import com.example.demo.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository
        extends JpaRepository<SystemSetting, Long> {
}