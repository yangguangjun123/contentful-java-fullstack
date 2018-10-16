package myproject.meetup.contentful.productcatalog.service;

import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.neo4j.driver.v1.Values.parameters;

@Service
public class Neo4jContentfulService {

    private Neo4jProperties neo4jProperties;
    private Driver driver;

    private static final Logger logger = LoggerFactory.getLogger(Neo4jContentfulService.class);

    @Autowired
    public Neo4jContentfulService(Neo4jProperties neo4jProperties) {
        this.neo4jProperties = neo4jProperties;
    }

    @PostConstruct
    public void init() {
        driver = GraphDatabase.driver(neo4jProperties.getDburl(), AuthTokens.basic(neo4jProperties.getDbuser(),
                neo4jProperties.getDbpassword()));
    }

    @PreDestroy
    public void destroy() {
        driver.close();
    }

    public void deleteAll() {
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }

    public void createEntryNode(String jsonEntryArrayString) {
        logger.info("receive entry json: " + jsonEntryArrayString);
        JSONArray jsonArray = new JSONArray(jsonEntryArrayString);
        logger.info("size of json array: " + jsonArray.length());
        jsonArray.toList().stream()
//                .peek(s -> logger.info("peek1: " + s.getClass().getCanonicalName()))
//                .filter(json -> json instanceof JSONObject)
//                    .peek(s -> logger.info("peek2: " + Objects.toString(s)))
//                          .map(JSONObject.class::cast)
                          .map(HashMap.class::cast)
                          .filter(jsonMap -> Objects.isNull(jsonMap.get("archived")) ||
                                  !Boolean.valueOf(jsonMap.get("archived").toString()))
                          .filter(jsonMap -> Objects.isNull(jsonMap.get("published")) ||
                                    Boolean.valueOf(jsonMap.get("published").toString()))
                          .forEach(this::processContentfulJSONMap);
    }

    private void processContentfulJSONMap(HashMap<String, Object> contentfulMap) {
//        String spaceId = (String) contentfulMap.get("spaceId");
//        String id = (String) contentfulMap.get("id");

        // process system map
        contentfulMap.entrySet()
                     .stream()
                     .filter(entry -> entry.getKey().equals("system"))
                     .map(entry -> entry.getValue())
                     .map(HashMap.class::cast)
                     .forEach(this::processContentfulJSONSystemMap);

        // process fields map
    }

    private void processContentfulJSONSystemMap(HashMap<String, Object> systemMap) {
        JSONObject systemJSON = new JSONObject(systemMap);
        JSONObject spaceJson = systemJSON.getJSONObject("space");
        String spaceId = spaceJson.getString("id");
        boolean spaceArchived = spaceJson.getBoolean("archived");
        boolean spacePublished = spaceJson.getBoolean("published");
        String spaceRelation = spaceJson.query("/system/type").toString();
        String spaceLinkType = spaceJson.query("/system/linkType").toString();

        // create space
        String spaceCreation = "MERGE (n:Space { id : {spaceId} }) SET n.archived = $spaceArchived, " +
                "n.published = $spacePublished";
        try( Session session = driver.session()) {
            session.run(spaceCreation, parameters( "spaceId", spaceId, "spaceArchived", spaceArchived,
                    "spacePublished", spaceArchived));
        }

        // create content entry/asset/user
        List<String> cypherStatements = new ArrayList<>();
        String type = (String) systemMap.get("type");
        String id = (String) systemMap.get("id");
        LocalDateTime createdAt = LocalDateTime.parse((String) systemMap.get("createdAt"),
                DateTimeFormatter.ISO_INSTANT);
        LocalDateTime updatedAt = LocalDateTime.parse((String) systemMap.get("updatedAt"),
                DateTimeFormatter.ISO_INSTANT);
        LocalDateTime publishedAt = LocalDateTime.parse((String) systemMap.get("publishedAt"),
                DateTimeFormatter.ISO_INSTANT);
        cypherStatements.add("CREATE (n:$type, { id = $id, createdAt = $createdAt, updatedBy = $updatedAt, " +
                "publishedAt = $published }) ");

        JSONObject contentTypeJSON = (JSONObject) systemMap.get("contentType");
        String contentTypeId = contentTypeJSON.query("/system/id").toString();
        boolean contentTypeArchived = Boolean.valueOf(contentTypeJSON.getString("archived"));
        boolean contentTypePublished = Boolean.valueOf((String) contentTypeJSON.query("/system/published"));
        String contentTypeRelation = contentTypeJSON.query("/system/type").toString();
        String contentTypeLinkType = contentTypeJSON.query("/system/linkType").toString();
        cypherStatements.add("MERGE (n:$contentTypeLinkType, { id = $contentTypeId }) ON CREATE SET " +
                "archived = $contentTypeArchived, published = $contentTypePublished");
        cypherStatements.add("MATCH(n:$type, { id = $id }) " +
                             "MATCH(m:contentTypeLinkType, { id = $contentTypeId )" +
                             "CREATE (n)-[:$contentTypeRelation, { linkType: $contentTypeLinkType }]-(m)");

        // createdBy
        JSONObject createdByJSON = (JSONObject) systemMap.get("createdBy");
        String createdById = contentTypeJSON.query("/system/id").toString();
        boolean createdByArchived = Boolean.valueOf(createdByJSON.getString("archived"));
        boolean createdByPublished = Boolean.valueOf((String) createdByJSON.query("/system/published"));
        String createdByRelation = createdByJSON.query("/system/type").toString();
        String createdByLinkType = createdByJSON.query("/system/linkType").toString();
        cypherStatements.add("MERGE (n:${createdByLinkType}, { id = $cratedById }) ON CREATE SET " +
                "archived = $createdByArchived, published = $createdByPublished");
        cypherStatements.add("MATCH(n:$type, { id = $id }) " +
                "MATCH(m:$createdByLinkType, { id = $createdById })" +
                "CREATE (n)-[:${createdByRelation}, { linkType: $createdByLinkType }]-(m)");

        // publishedBy
        JSONObject updatedByJSON = (JSONObject) systemMap.get("updatedBy");
        String updatedById = contentTypeJSON.query("/system/id").toString();
        boolean updatedByArchived = Boolean.valueOf(updatedByJSON.getString("archived"));
        boolean updatedByPublished = Boolean.valueOf((String) updatedByJSON.query("/system/published"));
        String updatedByRelation = updatedByJSON.query("/system/type").toString();
        String updatedByLinkType = updatedByJSON.query("/system/linkType").toString();
        cypherStatements.add("MERGE (n:$updatedByLinkType, { id = $cratedById }) ON CREATE SET " +
                "archived = $updatedByArchived, published = $updatedByPublished");
        cypherStatements.add("MATCH(n:$type, { id = $id }) " +
                "MATCH(m:$updatedByLinkType, { id = $updatedById })" +
                "CREATE (n)-[:${updatedByRelation}, { linkType: $updatedByLinkType }]-(m)");

        // published
        JSONObject publishedByJSON = (JSONObject) systemMap.get("publishedBy");
        String publishedById = contentTypeJSON.query("/system/id").toString();
        boolean publishedByArchived = Boolean.valueOf(publishedByJSON.getString("archived"));
        boolean publishedByPublished = Boolean.valueOf((String) publishedByJSON.query("/system/published"));
        String publishedByRelation = publishedByJSON.query("/system/type").toString();
        String publishedByLinkType = publishedByJSON.query("/system/linkType").toString();
        cypherStatements.add("MERGE (n:$publishedByLinkType, { id = $cratedById }) ON CREATE SET " +
                "archived = $publishedByArchived, published = $publishedByPublished");
        cypherStatements.add("MATCH(n:$type, { id = $id }) " +
                "MATCH(m:publishedByLinkType, { id = ${publishedById })" +
                "CREATE (n)-[:$publishedByRelation, { linkType: $publishedByLinkType }]-(m)");

        try ( Session session = driver.session() ) {
            session.writeTransaction( new TransactionWork<Optional<Object>>() {
                @Override
                public Optional<Object> execute(Transaction tx ) {
                    cypherStatements.forEach( s -> tx.run(s));
                    return Optional.empty();
                }
            } );
        }
    }
}
