package myproject.contentful.productcatalog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SuppressWarnings("unused")
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "neo4j")
public class Neo4jProperties {
    private String dburl;
    private String dbuser;
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
