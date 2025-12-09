package com.ssafy.tothezip.notice.controller;

import com.ssafy.tothezip.notice.model.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
@AllArgsConstructor
@Slf4j
public class NoticeController {

    private NoticeService noticeService;
}
