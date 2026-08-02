package com.cmrs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CmrsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CmrsServerApplication.class, args);

        log.info("""
		\r----------------------------------------------------------
		接口文档访问地址:
		本地Knife4j地址:   http://localhost:8080/doc.html
		----------------------------------------------------------""");

    }

}
