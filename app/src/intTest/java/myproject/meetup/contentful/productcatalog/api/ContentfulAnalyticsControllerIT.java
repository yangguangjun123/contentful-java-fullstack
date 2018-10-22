package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import myproject.meetup.contentful.productcatalog.service.ContentfulNeo4jService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
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
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentfulAnalyticsControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private ContentfulProperties contentfulProperties;

    @Autowired
    private ContentfulNeo4jService contentfulNeo4jService;

    @Autowired
    private Neo4jProperties neo4jProperties;

    private Driver driver;

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

        driver = GraphDatabase.driver(neo4jProperties.getDburl(), AuthTokens.basic(neo4jProperties.getDbuser(),
                neo4jProperties.getDbpassword()));
        this.setupTestData();
    }

    @After
    public void tearDown() {
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }

    @Test
    public void shouldReturnOrphanContentfulRecords() {
        // given
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // when
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort(
                "/contentful/analytics/orphan"), HttpMethod.GET, entity, String.class);

        // verify
        JSONObject json = new JSONObject(response.getBody());
        Assert.assertTrue(json.getJSONArray("orphans").length() == 1);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private void setupTestData() {
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        testRestTemplate.exchange(createURLWithPort("/contentful/neo4j/delete/all"),
                HttpMethod.DELETE, entity, String.class);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contententry/get/objectKey/");
        restUrlBuilder.append(contentfulProperties.getWorkshopSpaceName());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contentfulProperties.getWorkshopSpaceEnvironment());
        ResponseEntity<String> response = testRestTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentEntry");
        HttpEntity<String> entityForNeo4j = new HttpEntity<String>(JSONObject.valueToString(arr), headers);
        testRestTemplate.exchange(createURLWithPort("/contentful/neo4j/create/node/label/Entry"),
                HttpMethod.POST, entityForNeo4j, String.class);

        try (Session session = driver.session()) {
            session.run("CREATE (n:Orphan {id : 'orphan-node'})");
        }
    }
}
