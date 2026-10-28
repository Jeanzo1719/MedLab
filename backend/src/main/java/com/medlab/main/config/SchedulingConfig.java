package com.medlab.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Activa @Scheduled. Va en su propia clase para no modificar MedlabApplication. */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}