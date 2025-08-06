package com.organlink.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple Hospital Test Controller to verify Spring Boot registration
 */
@RestController
@RequestMapping("/api/v1/hospital-test")
@CrossOrigin(origins = {"http://localhost:5174", "http://localhost:5173"})
public class HospitalTestController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Hospital Module is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Hospital Module Status");
        response.put("data", Map.of(
            "module", "Hospital",
            "status", "ACTIVE",
            "features", new String[]{"OCR", "IPFS", "Blockchain", "Multi-tenant"}
        ));
        return ResponseEntity.ok(response);
    }
}
