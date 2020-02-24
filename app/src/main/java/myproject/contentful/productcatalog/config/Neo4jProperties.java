package myproject.contentful.productcatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@SuppressWarnings("unused")
@Configuration
//@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "neo4j")
public class Neo4jProperties {

    @Value("{GRAPHENEDB_BOLT_URL}")
    private String dburl;

    @Value("{GRAPHENEDB_BOLT_USER}")
    private String dbuser;

    @Value("{GRAPHENEDB_BOLT_PASSWORD}")
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
