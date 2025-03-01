package edu.neu.cs6510.sp25.t1.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT security.
 */
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtConfigProperties {
  private String secretKey;
  private long expirationMs;

  // Getters and Setters
  public String getSecretKey() { return secretKey; }
  public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

  public long getExpirationMs() { return expirationMs; }
  public void setExpirationMs(long expirationMs) { this.expirationMs = expirationMs; }
}

