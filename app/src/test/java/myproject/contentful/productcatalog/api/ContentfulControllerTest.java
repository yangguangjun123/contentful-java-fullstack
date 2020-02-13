package myproject.contentful.productcatalog.api;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ContentfulControllerTest {

    @InjectMocks
    private ContentfulController contentfulController;

//    @Autowired
//    private String spaceId;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contentfulController).build();
    }

    @Test
    public void shouldDeleteContentfulSpaceById() {
        // given

        // when

    }
}
