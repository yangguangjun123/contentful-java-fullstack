package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.config.ContentfulProperties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(classes = ContentfulService.class,
//        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentfulControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ContentfulProperties contefulProperties;

    private HttpHeaders headers = new HttpHeaders();

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnContentfulSpaceById() throws JSONException {
        // given
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        String expected = String.format("{id:%s,name:%s}", contefulProperties.getWorkshopSpaceId(),
                                contefulProperties.getWorkshopSpaceName());
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void shouldDeleteContentfulSpaceById() {
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        String expected = "{result:success}";
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/delete/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.DELETE, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Ignore
    public void shouldCreateContentfulSpaceForGivenId() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        map.add("name", contefulProperties.getWorkshopSpaceName());
        HttpEntity<Object> entity = new HttpEntity(map, headers);
        String expected = String.format("{id:%s,name:%s}", contefulProperties.getWorkshopSpaceId(),
                contefulProperties.getWorkshopSpaceName());
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/space/create/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.POST, entity, String.class);

        // verify
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void shouldRetrieveAllContentTypesBySpaceId() {
        // given
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contenttype/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentType");
        assertTrue(arr.length() >= 0);
        arr.toList().stream()
                    .map(s -> (Map<String,?>) s)
                    .forEach( o -> {
                        assertEquals(contefulProperties.getWorkshopSpaceId(), o.get("spaceId"));
                        assertEquals("master", o.get("environmentId"));
                        assertTrue(((String) o.get("id")).length() > 0);
                        assertTrue(((String) o.get("name")).length() > 0);
                    });
    }

    @Test
    public void shouldRetrieveAllContentEntriesBySpaceId() {
        // given
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contententry/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentEntry");
        assertTrue(arr.length() >= 0);
        arr.toList().stream()
                .map(s -> (Map<String,?>) s)
                .forEach( o -> {
                    assertEquals(contefulProperties.getWorkshopSpaceId(), o.get("spaceId"));
                    assertEquals("master", o.get("environmentId"));
                    assertTrue(((String) o.get("id")).length() > 0);
                });
    }

    @Test
    public void shouldRetrieveAllContentAssets() {
        // given
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contentasset/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentAsset");
        assertTrue(arr.length() >= 0);
        arr.toList().stream()
                .map(s -> (Map<String,?>) s)
                .forEach( o -> {
                    assertEquals(contefulProperties.getWorkshopSpaceId(), o.get("spaceId"));
                    assertEquals("master", o.get("environmentId"));
                    assertTrue(((String) o.get("id")).length() > 0);
                });
    }

    @Test
    public void shouldRetrieveAllContentLocalesBySpaceId() {
        // given
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        StringBuilder restUrlBuilder = new StringBuilder("/contentful/contentlocale/get/objectKey/");
        restUrlBuilder.append(contefulProperties.getWorkshopSpaceId());
        restUrlBuilder.append("/");
        restUrlBuilder.append(contefulProperties.getWorkshopManagementAccessToken());
        restUrlBuilder.append("/");
        restUrlBuilder.append("master");

        // when
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(restUrlBuilder.toString()),
                HttpMethod.GET, entity, String.class);

        // verify
        JSONObject obj = new JSONObject(response.getBody());
        JSONArray arr = obj.getJSONArray("contentLocale");
        assertTrue(arr.length() > 0);
        arr.toList().stream()
                .map(s -> (Map<String,?>) s)
                .forEach( o -> {
                    assertEquals(contefulProperties.getWorkshopSpaceId(), o.get("spaceId"));
                    assertEquals("master", o.get("environmentId"));
                    assertTrue(((String) o.get("id")).length() > 0);
                    assertTrue(((String) o.get("name")).length() > 0);
                });
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
