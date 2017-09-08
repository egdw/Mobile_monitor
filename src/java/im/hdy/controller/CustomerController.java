package im.hdy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hdy on 2017/9/5.
 */
@RestController
@RequestMapping("/")
public class CustomerController {

    private Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController() {
        logger.info("控制器已创建");
    }


    @RequestMapping(method = RequestMethod.GET)
    public String getCustomer() {
        return "index";
    }

}
