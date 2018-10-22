package myproject.meetup.contentful.productcatalog.service;

import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
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
import java.util.Set;

import static org.neo4j.driver.v1.Values.parameters;

@Service
public class ContentfulNeo4jService {

    private Neo4jProperties neo4jProperties;
    private Driver driver;

    private static final Logger logger = LoggerFactory.getLogger(ContentfulNeo4jService.class);

    @Autowired
    public ContentfulNeo4jService(Neo4jProperties neo4jProperties) {
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

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings("unchecked")
    private void processContentfulJSONMap(Map<String, Object> contentfulMap) {
        // process system map
        contentfulMap.entrySet()
                     .stream()
                     .filter(entry -> entry.getKey().equals("system"))
                     .map(entry -> entry.getValue())
                     .map(HashMap.class::cast)
                     .forEach(this::processContentfulSystemMap);

        @SuppressWarnings("unchecked")
        String type = ((Map<String, Object>) contentfulMap.get("system")).get("type").toString();

        // process fields map
        processContentfulFieldsMap((String) contentfulMap.get("id"), type,
                (Map<String, Object> ) contentfulMap.get("fields"));

    }

    private void processContentfulFieldsMap(String id, String type, Map<String, Object> fieldsMap) {
        // process fields containing sys key
        fieldsMap.entrySet()
                .stream()
                .filter(this::isSysField)
                .forEach(entry -> processSysField(id, type, entry));

        // process non-sys fields
        StringBuilder cypherCommandBuilder = new StringBuilder("MERGE (n:#label# { id : {id} }) ");
        List<Object> parameterList = new ArrayList<>();
        parameterList.add("id");
        parameterList.add(id);

        if(fieldsMap.entrySet().stream().filter(this::isNonSysField).count() > 0) {
            cypherCommandBuilder.append("SET ");
        }
        fieldsMap.entrySet()
                .stream()
                .filter(this::isNonSysField)
                .forEach(entry -> {
                    Map<String, Object> entryMap = new HashMap<>();
                    entryMap.put(entry.getKey(), entry.getValue());
                    cypherCommandBuilder.append(String.format("n.`%s` = $%s, ", getKeyPath(entryMap),
                            getShortPropertyName(getKeyPath(entryMap))));
                    parameterList.add(getShortPropertyName(getKeyPath(entryMap)));
                    parameterList.add(getValueOfNestedMap(entryMap));
                });

        // set all properties in one Cypher statement
        String cypherCommandStr = cypherCommandBuilder.toString().trim();
        if(cypherCommandStr.endsWith(",")) {
            cypherCommandStr = cypherCommandStr.substring(0, cypherCommandStr.length() -1);
        }
        logger.info("parameter list size: " + parameterList.size());
        logger.info("parameter list: " + parameterList);
        logger.info("run Cypher statement: " + cypherCommandStr.replace("#label#", type)
                .replace("{id}", id));
        try( Session session = driver.session()) {
            session.run(cypherCommandStr.replace("#label#", type),
                    parameters( parameterList.toArray(new Object[parameterList.size()])) );
        }
    }

    private String getShortPropertyName(String keyPath) {
        Objects.nonNull(keyPath);
        String[] names = keyPath.split("_");
        return names[0];
    }

    private void processSysField(String id, String type, Map.Entry<String, Object> entry) {
        logger.info(String.format("id: %s, type: %s, entry key: %s, entry value: %s", id, type,
                entry.getKey(), entry.getValue().toString()));
        JSONObject entryJson = new JSONObject();
        entryJson.put(entry.getKey(), entry.getValue());
        JSONArray array = new JSONArray(JsonPath.parse(entryJson.toString()).read("$..[?(@.sys)]").toString());
        array.toList().stream()
                      .map(HashMap.class::cast)
                      .map(m ->new JSONObject(m))
                      .forEach(json -> {
                          String linkType= json.query("/sys/linkType").toString();
                          String relationshipType= json.query("/sys/type").toString();
                          String linkedId= json.query("/sys/id").toString();
                          logger.info(String.format("linkType: %s,  relationshipType: %s, linkedId: %s",
                                  linkType, relationshipType, linkedId));
                          String createdRelationship = ("MERGE(n:#type# { id : {id} }) " +
                                  "MERGE(m:#linkType# { id : {linkedId} }) " +
                                  "MERGE (n)-[:#relationshipType# { linkType: {linkType} }]->(m)");
                          try( Session session = driver.session()) {
                              session.run(createdRelationship.replace("#type#", type)
                                              .replace("#linkType#", linkType)
                                              .replace("#relationshipType#", relationshipType),
                                      parameters( "id", id, "linkedId", linkedId, "linkType", linkType));
                          }
                      });
    }

    @SuppressWarnings("unchecked")
    public String getKeyPath(Map<String, Object> map) {
        Set<Map.Entry<String, Object>> entrySet =  map.entrySet();
        Map.Entry<String, Object>  entry = entrySet.iterator().next();
        if(!(entry.getValue() instanceof Map)) {
            return entry.getKey();
        }
        return entry.getKey() + "_" + getKeyPath((Map<String, Object>) entry.getValue());
    }

    @SuppressWarnings("unchecked")
    public Object getValueOfNestedMap(Map<String, Object> map) {
        Set<Map.Entry<String, Object>> entrySet =  map.entrySet();
        Map.Entry<String, Object>  entry = entrySet.iterator().next();
        if(!(entry.getValue() instanceof Map)) {
            return entry.getValue();
        }
        return getValueOfNestedMap((Map<String, Object>) entry.getValue());
    }

    private void processNonSysField(String id, String type, Map.Entry<String, Object> entry) {
        Map<String, Object> entryMap = new HashMap<>();
        entryMap.put(entry.getKey(), entry.getValue());
        String updateNode = ("MERGE (n:#label# { id : {id} }) SET n.`#propertyName#` = $value");
        logger.info("set Neo4j property for non-sys field: " + updateNode.replace("#label#", type).
                replace("#propertyName#", getKeyPath(entryMap)).
                replace("{id}", id).replace("#label#", type));
        try( Session session = driver.session()) {
            session.run(updateNode.replace("#label#", type).replace("#propertyName#", getKeyPath(entryMap)),
                    parameters( "id", id, "value", getValueOfNestedMap(entryMap) ));
        }
    }

    private boolean isNonSysField(Map.Entry<String, Object> stringObjectEntry) {
        JSONObject json = new JSONObject();
        json.append(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        JSONArray found = new JSONArray(JsonPath.parse(json.toString()).read("$..[?(@.sys)]").toString());
        return found.isEmpty();
    }

    private boolean isSysField(Map.Entry<String, Object> stringObjectEntry) {
        JSONObject json = new JSONObject();
        json.append(stringObjectEntry.getKey(), stringObjectEntry.getValue());
        JSONArray found = new JSONArray(JsonPath.parse(json.toString()).read("$..[?(@.sys)]").toString());
        return !found.isEmpty();
    }

    private void processContentfulSystemMap(Map<String, Object> systemMap) {
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
                    "spacePublished", spacePublished));
        }

        // create content entry/asset/local/user
        List<String> cypherStatements = new ArrayList<>();
        String type = (String) systemMap.get("type");
        String id = (String) systemMap.get("id");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS[X]");
        LocalDateTime createdAt = LocalDateTime.parse((String) systemMap.get("createdAt"), formatter);
        LocalDateTime updatedAt = LocalDateTime.parse((String) systemMap.get("updatedAt"), formatter);
        LocalDateTime publishedAt = LocalDateTime.parse((String) systemMap.get("publishedAt"), formatter);
        String entityNode = ("MERGE (n:#label# { id : {id} }) SET n.createdAt = $createdAt, " +
                "n.updatedBy = $updatedAt, " + "n.publishedAt = $publishedAt");
        String spaceTypeRelationship = ("MATCH(n:Space { id : {spaceId} }) " +
                "MATCH(m:#type# { id : {id} }) " +
                "MERGE (m)-[:#spaceRelation# { linkType: {spaceLinkType} }]->(n)");
        try( Session session = driver.session()) {
            session.run(entityNode.replace("#label#", type), parameters( "id", id,
                    "createdAt", createdAt, "updatedAt", updatedAt, "publishedAt", publishedAt ));

            session.run(spaceTypeRelationship.replace("#type#", type)
                            .replace("#spaceRelation#", spaceRelation),
                    parameters( "spaceId", spaceId,
                            "id", id, "spaceLinkType", spaceLinkType));
        }

        if(Objects.nonNull(systemJSON.query("/contentType"))) {
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
                    "MERGE (n)-[:#contentTypeRelation# { linkType: {contentTypeLinkType} }]->(m)");
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
        }

        // createdBy
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
                "MERGE (n)-[:#createdByRelation# { linkType: {createdByLinkType} }]->(m)";
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
                "MERGE (n)-[:#updatedByRelation# { linkType: {updatedByLinkType} }]->(m)";
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
                "MERGE (n)-[:#publishedByRelation# { linkType: {publishedByLinkType} }]->(m)";

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

    public JSONArray getOrphanNodes() {
        JSONArray jsonArray = new JSONArray();
        Gson gson = new Gson();
        String cypherStatement = "MATCH (a) WHERE NOT (a)-[]-() RETURN a";
        try( Session session = driver.session()) {
            StatementResult result = session.run("MATCH (a) WHERE NOT (a)-[]-() RETURN a");
            while ( result.hasNext() ) {
                Record record = result.next();
                jsonArray.put(gson.toJson(record.asMap()));
            }
        }
        return jsonArray;
    }

    @SuppressWarnings("unchecked")
    public void createAssetNode(String jsonEntryArrayString) {
        logger.info("receive asset json: " + jsonEntryArrayString);
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
    @SuppressWarnings("unchecked")
    public void createLocaleNode(String jsonEntryArrayString) {
        logger.info("receive locale json: " + jsonEntryArrayString);
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
}
