package com.epam.finaltask.restcontroller.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("username")
    public String addUsernameToModel(Principal principal) {

        return principal != null ? principal.getName() : null;
    }
}
