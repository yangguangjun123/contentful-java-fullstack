package myproject.meetup.contentful.productcatalog.api;

import myproject.meetup.contentful.productcatalog.service.ContentfulNeo4jService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/neo4j")
public class Neo4jController {

    private ContentfulNeo4jService neo4jContentfulService;

    @Autowired
    public Neo4jController(ContentfulNeo4jService neo4jService) {
        this.neo4jContentfulService = neo4jService;
    }

    @RequestMapping(path = "/delete/all", method= RequestMethod.DELETE)
    public String deleteAll() {
        neo4jContentfulService.deleteAll();
        JSONObject response = new JSONObject("{result: success}");
        return response.toString();
    }

    @RequestMapping(path = "/create/node/label/{label}", method= RequestMethod.POST)
    public String createEntity(@PathVariable String label, @RequestBody String body) {
        if("Entry".equals(label)) {
            neo4jContentfulService.createEntryNode(body);
        }
        JSONObject response = new JSONObject("{result: success}");
        return response.toString();
    }

}
