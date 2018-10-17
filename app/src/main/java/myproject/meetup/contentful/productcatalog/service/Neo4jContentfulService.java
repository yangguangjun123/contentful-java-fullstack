package myproject.meetup.contentful.productcatalog.service;

import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
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
import java.util.Objects;

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
        deleteAll();
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
                          .map(HashMap.class::cast)
                          .filter(jsonMap -> Objects.isNull(jsonMap.get("archived")) ||
                                  !Boolean.valueOf(jsonMap.get("archived").toString()))
                          .filter(jsonMap -> Objects.isNull(jsonMap.get("published")) ||
                                    Boolean.valueOf(jsonMap.get("published").toString()))
                          .forEach(this::processContentfulJSONMap);
    }

    private void processContentfulJSONMap(HashMap<String, Object> contentfulMap) {
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS[X]");
        LocalDateTime createdAt = LocalDateTime.parse((String) systemMap.get("createdAt"), formatter);
        LocalDateTime updatedAt = LocalDateTime.parse((String) systemMap.get("updatedAt"), formatter);
        LocalDateTime publishedAt = LocalDateTime.parse((String) systemMap.get("publishedAt"), formatter);
        String entityNode = ("CREATE (n:#label# { id : {id} }) SET n.createdAt = $createdAt, " +
                "n.updatedBy = $updatedAt, " + "n.publishedAt = $publishedAt");
        try( Session session = driver.session()) {
            session.run(entityNode.replace("#label#", type), parameters( "id", id,
                    "createdAt", createdAt, "updatedAt", updatedAt, "publishedAt", publishedAt ));
        }

        JSONObject contentTypeJSON = systemJSON.getJSONObject("contentType");
        String contentTypeId = contentTypeJSON.query("/system/id").toString();
        boolean contentTypeArchived = contentTypeJSON.getBoolean("archived");
        boolean contentTypePublished = contentTypeJSON.getBoolean("published");
        String contentTypeRelation = contentTypeJSON.query("/system/type").toString();
        String contentTypeLinkType = contentTypeJSON.query("/system/linkType").toString();
        String contentTypeNode = "MERGE (n:#contentTypeLinkType# { id : {contentTypeId} }) ON CREATE SET " +
                "n.archived = $contentTypeArchived, n.published = $contentTypePublished";
        String contentTypeRelationship = ("MATCH(n:#type# { id : {id} }) " +
                             "MATCH(m:#contentTypeLinkType# { id : {contentTypeId} }) " +
                             "CREATE (n)-[:#contentTypeRelation# { linkType: {contentTypeLinkType} }]->(m)");

        try( Session session = driver.session()) {
            session.run(contentTypeNode.replace("#contentTypeLinkType#", contentTypeLinkType),
                    parameters( "contentTypeId", contentTypeId,
                            "contentTypeArchived", contentTypeArchived,
                            "contentTypePublished", contentTypePublished));

            session.run(contentTypeRelationship.replace("#type#", type)
                                               .replace("#contentTypeLinkType#", contentTypeLinkType)
                                               .replace("#contentTypeRelation#", contentTypeRelation),
                    parameters( "id", id,
                            "contentTypeId", contentTypeId,
                            "contentTypeLinkType", contentTypeLinkType));
        }

        // createdBypublishedByPublished
        JSONObject createdByJSON = systemJSON.getJSONObject("createdBy");
        String createdById = createdByJSON.query("/system/id").toString();
        boolean createdByArchived = createdByJSON.getBoolean("archived");
        boolean createdByPublished = createdByJSON.getBoolean("published");
        String createdByRelation = createdByJSON.query("/system/type").toString();
        String createdByLinkType = createdByJSON.query("/system/linkType").toString();
        String createdByNode = "MERGE (n:#createdByLinkType# { id : {createdById} }) ON CREATE SET " +
                "n.archived = $createdByArchived, n.published = $createdByPublished";
        String createdByRelationship = "MATCH(n:#type# { id : $id }) " +
                "MATCH(m:#createdByLinkType# { id : {createdById} })" +
                "CREATE (n)-[:#createdByRelation# { linkType: {createdByLinkType} }]->(m)";
        try( Session session = driver.session()) {
            session.run(createdByNode.replace("#createdByLinkType#", createdByLinkType),
                    parameters( "createdById", createdById,
                            "createdByArchived", createdByArchived,
                            "createdByPublished", createdByPublished));

            session.run(createdByRelationship.replace("#type#", type)
                            .replace("#createdByLinkType#", createdByLinkType)
                            .replace("#createdByRelation#", createdByRelation),
                    parameters( "id", id,
                            "createdById", createdById,
                            "createdByLinkType", createdByLinkType));
        }

        // updatedBy
        JSONObject updatedByJSON = systemJSON.getJSONObject("updatedBy");
        String updatedById = updatedByJSON.query("/system/id").toString();
        boolean updatedByArchived = updatedByJSON.getBoolean("archived");
        boolean updatedByPublished = updatedByJSON.getBoolean("published");
        String updatedByRelation = updatedByJSON.query("/system/type").toString();
        String updatedByLinkType = updatedByJSON.query("/system/linkType").toString();
        String updatedByNode = "MERGE (n:#updatedByLinkType# { id : {updatedById} }) ON CREATE SET " +
                "n.archived = $updatedByArchived, n.published = $updatedByPublished";
        String updatedByRelationship = "MATCH(n:#type# { id : {id} }) " +
                "MATCH(m:#updatedByLinkType# { id : {updatedById} })" +
                "CREATE (n)-[:#updatedByRelation# { linkType: {updatedByLinkType} }]->(m)";
        try( Session session = driver.session()) {
            session.run(updatedByNode.replace("#updatedByLinkType#", updatedByLinkType),
                    parameters( "updatedById", updatedById,
                            "updatedByArchived", updatedByArchived,
                            "updatedByPublished", updatedByPublished));

            session.run(updatedByRelationship.replace("#type#", type)
                            .replace("#updatedByLinkType#", updatedByLinkType)
                            .replace("#updatedByRelation#", updatedByRelation),
                    parameters( "id", id,
                            "updatedById", updatedById,
                            "updatedByLinkType", updatedByLinkType));
        }

        // publishedBy
        JSONObject publishedByJSON = systemJSON.getJSONObject("publishedBy");
        String publishedById = publishedByJSON.query("/system/id").toString();
        boolean publishedByArchived = publishedByJSON.getBoolean("archived");
        boolean publishedByPublished = publishedByJSON.getBoolean("published");
        String publishedByRelation = publishedByJSON.query("/system/type").toString();
        String publishedByLinkType = publishedByJSON.query("/system/linkType").toString();
        String publishedByNode = "MERGE (n:#publishedByLinkType# { id : {publishedById} }) ON CREATE SET " +
                "n.archived = $publishedByArchived, n.published = $publishedByPublished";
        String publishedByRelationship = "MATCH(n:#type# { id : {id} }) " +
                "MATCH(m:#publishedByLinkType# { id : {publishedById} })" +
                "CREATE (n)-[:#publishedByRelation# { linkType: {publishedByLinkType} }]->(m)";

        try( Session session = driver.session()) {
            session.run(publishedByNode.replace("#publishedByLinkType#", publishedByLinkType),
                    parameters( "publishedById", publishedById,
                            "publishedByArchived", publishedByArchived,
                            "publishedByPublished", publishedByPublished));

            session.run(publishedByRelationship.replace("#type#", type)
                            .replace("#publishedByLinkType#", publishedByLinkType)
                            .replace("#publishedByRelation#", publishedByRelation),
                    parameters( "id", id,
                            "publishedById", publishedById,
                            "publishedByLinkType", publishedByLinkType));
        }

    }
}