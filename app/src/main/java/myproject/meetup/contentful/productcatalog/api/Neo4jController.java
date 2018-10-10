package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import myproject.meetup.contentful.productcatalog.service.ContentfulService;
import myproject.meetup.contentful.productcatalog.service.Neo4jService;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo4j")
public class Neo4jController {

    private Neo4jService neo4jService;

    @Autowired
    public Neo4jController(Neo4jService neo4jService) {
        this.neo4jService = neo4jService;
    }

    @RequestMapping(path = "/deleteAll", method= RequestMethod.DELETE)
    public String deleteAll() {
        neo4jService.deleteAll();
        JSONObject response = new JSONObject("{result: success}");
        return response.toString();
    }


}
