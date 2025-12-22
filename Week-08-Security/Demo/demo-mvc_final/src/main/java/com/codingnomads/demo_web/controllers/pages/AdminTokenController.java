package com.codingnomads.demo_web.controllers.pages;

import com.codingnomads.demo_web.services.ApiTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Deprecated: merged into AdminController under /admin.
 * Keeping the class non-annotated to avoid component scanning.
 */
@RequiredArgsConstructor
public class AdminTokenController {

    private final ApiTokenService apiTokenService;

    // moved to AdminController
}
