package myproject.contentful.productcatalog.api;

import myproject.contentful.productcatalog.service.ContentfulService;
import myproject.contentful.productcatalog.service.Neo4jDatabaseService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class ContentfulControllerTest {

    @MockBean
    private ContentfulService contentfulService;

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
