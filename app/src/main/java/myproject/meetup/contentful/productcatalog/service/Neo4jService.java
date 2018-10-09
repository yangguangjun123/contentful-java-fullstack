package myproject.meetup.contentful.productcatalog.service;

import myproject.meetup.contentful.productcatalog.config.Neo4jProperties;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Neo4jService {

    private Neo4jProperties neo4jProperties;
    private Driver driver;

    @Autowired
    public Neo4jService(Neo4jProperties neo4jProperties) {
        this.neo4jProperties = neo4jProperties;
        driver = GraphDatabase.driver(neo4jProperties.getDburl(), AuthTokens.basic(neo4jProperties.getDbuser(),
                neo4jProperties.getDbpassword()));
    }

    public void deleteAll() {
        try (Session session = driver.session()) {
            session.run("MATCH (n) DETACH DELETE n");
        }
    }

    public void execute(List<String> cypherStatements) {

    }
}
