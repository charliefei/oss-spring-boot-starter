package com.feirui.oss;

import com.feirui.oss.service.CloudStorageService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Resource
    private CloudStorageService cloudStorageService;

    @Test
    void contextLoads() {
        System.out.println(cloudStorageService);
    }

}
