package com.quant.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quant.twitter.TwitterService;

/**
 * Twitter监控控制器
 * 提供Twitter监控相关的REST API
 */
@RestController
@RequestMapping("/api/twitter")
public class TwitterController {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitterController.class);
    
    @Autowired
    private TwitterService twitterService;
    
    /**
     * 获取监控状态
     */
    @GetMapping("/status")
    public String getStatus() {
        logger.info("获取Twitter监控状态");
        return twitterService.getMonitoringStatus();
    }
    
    /**
     * 手动检查推文
     */
    @GetMapping("/check")
    public String checkNow() {
        logger.info("手动触发推文检查");
        try {
            twitterService.checkTweetsNow();
            return "手动推文检查已启动，请查看日志获取详细信息";
        } catch (Exception e) {
            logger.error("手动推文检查失败", e);
            return "手动推文检查失败: " + e.getMessage();
        }
    }
}
