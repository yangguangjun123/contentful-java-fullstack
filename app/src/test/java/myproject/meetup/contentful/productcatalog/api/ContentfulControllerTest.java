package myproject.meetup.contentful.productcatalog.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ActiveProfiles("dev")
@RunWith(MockitoJUnitRunner.class)
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
