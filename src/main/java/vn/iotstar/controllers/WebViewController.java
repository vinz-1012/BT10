package vn.iotstar.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping("/auth/login")
    public String loginPage() {
        return "forward:/auth/login.html";
    }

    @GetMapping("/login")
    public String loginShortcut() {
        return "forward:/auth/login.html";
    }

    @GetMapping("/auth/signup")
    public String signupPage() {
        return "forward:/auth/signup.html";
    }

    @GetMapping("/signup")
    public String signupShortcut() {
        return "forward:/auth/signup.html";
    }

    @GetMapping("/user")
    public String userPage() {
        return "forward:/user/index.html";
    }

    @GetMapping("/user/me")
    public String userMePage() {
        return "forward:/user/me.html";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/auth/login";
    }
}
