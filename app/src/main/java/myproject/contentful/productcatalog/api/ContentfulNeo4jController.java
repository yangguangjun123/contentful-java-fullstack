package myproject.contentful.productcatalog.api;

import myproject.contentful.productcatalog.service.Neo4jDatabaseService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping(value = "/contentful/neo4j", produces={"application/json","application/xml"})
public class ContentfulNeo4jController {

    private final Neo4jDatabaseService neo4jDatabaseService;

    @Autowired
    public ContentfulNeo4jController(Neo4jDatabaseService neo4jService) {
        this.neo4jDatabaseService = neo4jService;
    }

    @RequestMapping(path = "/delete/all", method= RequestMethod.DELETE)
    public String deleteAll() {
        neo4jDatabaseService.deleteAll();
        JSONObject response = new JSONObject("{result: success}");
        return response.toString();
    }

    @RequestMapping(path = "/create/node/label/{label}", method= RequestMethod.POST)
    public String createEntity(@PathVariable String label, @RequestBody String body) {
        if("Entry".equals(label)) {
            neo4jDatabaseService.createEntryNode(body);
        }
        JSONObject response = new JSONObject("{result: success}");
        return response.toString();
    }

}
