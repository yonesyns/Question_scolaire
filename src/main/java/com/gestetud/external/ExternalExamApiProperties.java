package com.gestetud.external;
import org.springframework.boot.context.properties.ConfigurationProperties;
@ConfigurationProperties(prefix="integrations.exam-api")
public class ExternalExamApiProperties {
    private String baseUrl="http://localhost:8080/mock-exam-api";
    private boolean enabled=true;
    private int connectTimeoutMs=2000;
    private int readTimeoutMs=3000;
    public String getBaseUrl(){return baseUrl;} public void setBaseUrl(String value){baseUrl=value;}
    public boolean isEnabled(){return enabled;} public void setEnabled(boolean value){enabled=value;}
    public int getConnectTimeoutMs(){return connectTimeoutMs;} public void setConnectTimeoutMs(int value){connectTimeoutMs=value;}
    public int getReadTimeoutMs(){return readTimeoutMs;} public void setReadTimeoutMs(int value){readTimeoutMs=value;}
}
