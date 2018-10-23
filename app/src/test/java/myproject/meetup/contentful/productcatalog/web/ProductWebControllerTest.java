package myproject.meetup.contentful.productcatalog.web;

import myproject.meetup.contentful.productcatalog.service.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
@WebMvcTest(ProductWebController.class)
public class ProductWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void shouldReturnDefaultProductFromProductService() throws Exception {
        // given
        when(productService.getDefault()).thenReturn("default_product");

        // when
        // verify
        mockMvc.perform(get("/product/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("default_product")));
    }
}
