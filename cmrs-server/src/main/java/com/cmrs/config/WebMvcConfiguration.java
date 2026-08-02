package com.cmrs.config;

import com.cmrs.interceptor.JwtTokenUserInterceptor;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private JwtTokenUserInterceptor jwtTokenUserInterceptor;

    /**
     * 配置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有请求路径
        registry.addMapping("/**")
                // 允许的前端域名，根据Vue3项目地址修改
                .allowedOrigins("http://localhost:5173")
                // 允许的请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的请求头
                .allowedHeaders("*")
                // 允许携带凭证（如cookie、token）
                .allowCredentials(true)
                // 预检请求的有效期（秒）
                .maxAge(3600);
    }

    /**
     * 注册自定义拦截器
     * @param registry
     */
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenUserInterceptor)
                .addPathPatterns("/user/**", "/admin/**", "/repairman/**");
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("网络故障报修系统项目接口文档")
                        .version("1.0")
                        .description("网络故障报修系统项目接口文档"));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("管理端接口")
                .packagesToScan("com.cmrs.controller.admin")
                .build();
    }

    @Bean
    public GroupedOpenApi loginApi() {
        return GroupedOpenApi.builder()
                .group("登录接口")
                .pathsToMatch("/users/login")  // 根据请求路径匹配
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("用户端接口")
                .packagesToScan("com.cmrs.controller.user")  // 根据请求路径匹配
                .build();
    }

    @Bean
    public GroupedOpenApi repairmanApi() {
        return GroupedOpenApi.builder()
                .group("维修人员端接口")
                .packagesToScan("com.cmrs.controller.repairman")  // 根据请求路径匹配
                .build();
    }

    /**
     * 设置静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射...");
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
    }


//    /**
//     * 扩展消息转换器,将日期类型从列表转换为时间戳
//     * 这个是导致knife4j不能正常显示的罪魁祸首,特别要注意添加的位置
//     * @param converters 消息转换器列表
//     */
//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        log.info("扩展消息转换器...");
//        //创建一个消息转换器对象
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        //需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
//        converter.setObjectMapper(new JacksonObjectMapper());
//        //将自己的消息转化器加入容器中
//        converters.add(converters.size()-1,converter);
//    }
}
