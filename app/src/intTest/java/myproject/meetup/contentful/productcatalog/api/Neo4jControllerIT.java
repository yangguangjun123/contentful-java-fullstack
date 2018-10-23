package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("integration")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Neo4jControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ContentfulProperties contentfulProperties;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders headers = new HttpHeaders();

    private static final Logger logger = LoggerFactory.getLogger(Neo4jControllerIT.class);

    @Before
    public void setUp() {
        HttpComponentsClientHttpRequestFactory clientRequestFactory = new HttpComponentsClientHttpRequestFactory();
        // set the read timeot, this value is in miliseconds
        clientRequestFactory.setReadTimeout(60000);
        testRestTemplate.getRestTemplate().setRequestFactory(clientRequestFactory);

        setupTestData();
    }

    @After
    public void tearDown() {
        setupTestData();
    }

    @Test
    public void shouldDeleteAllEntities() {
        // given
        logger.info("shouldDeleteAllEntities");
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        String expected = String.format("{result: success}");

        // when
        ResponseEntity<String> response = testRestTemplate.exchange(
                createURLWithPort("/contentful/neo4j/delete/all"),
                HttpMethod.DELETE, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void shouldCreateNeo4jNodeFromContentfulEntry() {
        // given
        testRestTemplate.exchange(
                createURLWithPort("/contentful/neo4j/delete/all"),
                HttpMethod.DELETE, new HttpEntity<>("", headers), String.class);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contententry/get/objectKey/");
        restUrlBuilder.append(contentfulProperties.getSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getSpaceEnvironment());
        ResponseEntity<String> response = testRestTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentEntry");

        // when
//        Awaitility.await().pollDelay(Duration.ONE_SECOND).until(() -> true);
//        Thread.sleep(1000);
        String expected = "{result:success}";
        HttpEntity<String> entityForNeo4j = new HttpEntity<String>(JSONObject.valueToString(arr), headers);
        response = testRestTemplate.exchange(createURLWithPort("/contentful/neo4j/create/node/label/Entry"),
                HttpMethod.POST, entityForNeo4j, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }


    private void setupTestData() {
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        testRestTemplate.exchange(createURLWithPort("/contentful/neo4j/delete/all"),
                HttpMethod.DELETE, entity, String.class);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contententry/get/objectKey/");
        restUrlBuilder.append(contentfulProperties.getSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getSpaceEnvironment());
        ResponseEntity<String> response = testRestTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentEntry");
        HttpEntity<String> entityForNeo4j = new HttpEntity<String>(JSONObject.valueToString(arr), headers);
        testRestTemplate.exchange(createURLWithPort("/contentful/neo4j/create/node/label/Entry"),
                HttpMethod.POST, entityForNeo4j, String.class);
    }
}
