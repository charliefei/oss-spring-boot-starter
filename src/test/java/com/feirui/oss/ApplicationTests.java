package com.feirui.oss;

import com.feirui.oss.service.CloudStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ApplicationTests {

    @Resource
    private CloudStorageService cloudStorageService;

    @Test
    void contextLoads() {
        System.out.println(cloudStorageService);
    }

}
