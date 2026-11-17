package com.gestetud.external;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import java.net.http.HttpClient;
import java.time.Duration;
@Configuration
@EnableConfigurationProperties(ExternalExamApiProperties.class)
public class ExternalExamClientConfig {
 @Bean RestClient externalExamRestClient(RestClient.Builder builder,ExternalExamApiProperties properties){
   HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofMillis(properties.getConnectTimeoutMs())).build();
   JdkClientHttpRequestFactory factory=new JdkClientHttpRequestFactory(http);
   factory.setReadTimeout(Duration.ofMillis(properties.getReadTimeoutMs()));
   return builder.baseUrl(properties.getBaseUrl()).requestFactory(factory).build();
 }
}
