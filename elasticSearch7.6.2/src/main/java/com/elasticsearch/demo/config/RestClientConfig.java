package com.elasticsearch.demo.config;


import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;


@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {


    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {

        //    ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//            .connectedTo("localhost:9200", "localhost:9291")
//            .useSsl()
//            .withProxy("localhost:8888")
//            .withPathPrefix("ela")
//            .withConnectTimeout(Duration.ofSeconds(5))
//            .withSocketTimeout(Duration.ofSeconds(3))
//            .withDefaultHeaders(defaultHeaders)
//            .withBasicAuth(username, password)
//            .withHeaders(() -> {
//                HttpHeaders headers = new HttpHeaders();
//                headers.add("currentTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//                return headers;
//            })
//            . // ... other options
//            .build();

        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
//                .withConnectTimeout(Duration.ofSeconds(5))
//                .withSocketTimeout(Duration.ofSeconds(3))
//                .withDefaultHeaders(new HttpHeaders())
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
