package com.maliuvanchuk;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleDBConfig {
	
	@Value("${db.username}")
	private String username;
	
	@Value("${db.password}")
	private String password;
	
	
	@Value("${db.name}")
	private String name;

    @Bean
    public DataSource dataSource()  {    

        URI dbUri;
        try {
            String url = "jdbc:postgresql://localhost:5432/" + name;
            String dbProperty = System.getenv("DATABASE_URL");
            if(dbProperty != null) {
                dbUri = new URI(dbProperty);

                username = dbUri.getUserInfo().split(":")[0];
                password = dbUri.getUserInfo().split(":")[1];
                url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            }     

            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(url);
            basicDataSource.setUsername(username);
            basicDataSource.setPassword(password);

            return basicDataSource;

        } catch (URISyntaxException e) {
            return null;
        }
    }
}