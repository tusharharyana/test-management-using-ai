package com.example.demo.controller;

import com.example.demo.service.SystemSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system-settings")
@CrossOrigin
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    public SystemSettingController(SystemSettingService systemSettingService) {
        this.systemSettingService = systemSettingService;
    }

    @GetMapping("/ai-evaluation")
    public ResponseEntity<Map<String, Boolean>> getAiEvaluationStatus() {

        boolean enabled = systemSettingService.isAiEvaluationEnabled();

        return ResponseEntity.ok(
                Map.of("enabled", enabled)
        );
    }

    @PutMapping("/ai-evaluation")
    public ResponseEntity<Map<String, Boolean>> updateAiEvaluationStatus(
            @RequestBody Map<String, Boolean> request
    ) {

        boolean enabled = Boolean.TRUE.equals(request.get("enabled"));

        boolean updated =
                systemSettingService.updateAiEvaluationEnabled(enabled);

        return ResponseEntity.ok(
                Map.of("enabled", updated)
        );
    }
}