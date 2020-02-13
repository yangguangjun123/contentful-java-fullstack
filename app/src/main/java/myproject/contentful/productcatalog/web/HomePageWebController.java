package myproject.contentful.productcatalog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomePageWebController {

    @RequestMapping("/")
    public @ResponseBody String home() {
        return "index";
    }
}
