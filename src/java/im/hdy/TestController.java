package im.hdy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hdy on 2017/9/5.
 */
//@Controller
//@EnableAutoConfiguration
//@ComponentScan("im.hdy.*")
@SpringBootApplication
@ComponentScan
public class TestController{

    public static void main(String[] args) {
        SpringApplication.run(TestController.class, args);
    }

}

//@SpringBootApplication
//@ComponentScan
//public class TestController extends SpringBootServletInitializer {
//
//    public static void main(String[] args) {
//        SpringApplication.run(TestController.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(TestController.class);
//    }
//}

