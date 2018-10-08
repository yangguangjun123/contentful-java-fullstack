package myproject.meetup.contentful.productcatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:neo4j.properties")
@ConfigurationProperties
public class Neo4jProperties {

    @Value("${neo4j.dburl}")
    private String dburl;

    @Value("${neo4j.dbuser}")
    private String dbuser;

    @Value("${neo4j.dbpassword}")
    private String dbpassword;

    public String getDburl() {
        return dburl;
    }

    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    public String getDbuser() {
        return dbuser;
    }

    public void setDbuser(String dbuser) {
        this.dbuser = dbuser;
    }

    public String getDbpassword() {
        return dbpassword;
    }

    public void setDbpassword(String dbpassword) {
        this.dbpassword = dbpassword;
    }
}
