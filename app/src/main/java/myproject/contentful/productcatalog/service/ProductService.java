package myproject.contentful.productcatalog.service;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public String getDefault() {
        return "default_product";
    }
}
