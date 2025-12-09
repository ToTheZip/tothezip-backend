package com.ssafy.tothezip.user.controller;

import com.ssafy.tothezip.user.model.UserDto;
import com.ssafy.tothezip.user.model.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserService userService;

    @GetMapping("/regist")
    public String regist() {
        return "user/regist";
    }

    @PostMapping("/regist")
    public String regist(@ModelAttribute UserDto userDto) {
        return "redirect:/user/login";
    }

}
