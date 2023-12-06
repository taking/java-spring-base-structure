package kr.taking.backend.configuration.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * <pre>
 * ClassName : MongoConfig
 * Type : class
 * Description : Mongo 연결에 필요한 정보를 포함하고 있는 클래스입니다.
 * Related : All
 * </pre>
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "taking";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

}