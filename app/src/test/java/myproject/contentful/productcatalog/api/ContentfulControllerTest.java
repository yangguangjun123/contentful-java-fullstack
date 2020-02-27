package myproject.contentful.productcatalog.api;

import myproject.contentful.productcatalog.service.ContentfulService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentfulController.class)
public class ContentfulControllerTest {
    @MockBean
    private ContentfulService contentfulService;

//    @Autowired
//    private String spaceId;

    @Autowired
    private MockMvc mockMvc;

    private static final Logger logger = LoggerFactory.getLogger(ContentfulControllerTest.class);

    @Test
    public void shouldDeleteContentfulSpaceById() throws Exception {
        logger.info("running shouldDeleteContentfulSpaceById");

        mockMvc.perform(put("/contentful/space/updateCach")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("success")));
        verify(contentfulService, times(1)).init();
    }
}
