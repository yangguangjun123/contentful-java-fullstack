package myproject.contentful.productcatalog.web;

import myproject.contentful.productcatalog.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/product")
public class ProductWebController {

    private ProductService productService;

    public ProductWebController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping("/")
    public @ResponseBody String home() {
        return productService.getDefault();
    }

}
