package myproject.contentful.productcatalog.api;

import myproject.contentful.productcatalog.service.Neo4jDatabaseService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/contentful", produces={"application/json"})
@SuppressWarnings("unused")
public class ContentfulAnalyticsController {

    private final Neo4jDatabaseService neo4jContentfulService;

    @Autowired
    public ContentfulAnalyticsController(Neo4jDatabaseService neo4jContentfulService) {
        this.neo4jContentfulService = neo4jContentfulService;
    }

    @RequestMapping(path = "/analytics/orphan", method= RequestMethod.GET)
    public String getContenfulOrphans() {
        JSONArray orphans = neo4jContentfulService.getOrphanNodes();
        JSONObject response = new JSONObject();
        response.put("orphans", orphans);
        return response.toString();
    }

}