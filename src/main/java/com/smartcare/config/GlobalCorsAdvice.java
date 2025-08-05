package com.smartcare.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;

@ControllerAdvice
@CrossOrigin(origins = "*")
public class GlobalCorsAdvice {
    // This will apply to all controllers
}