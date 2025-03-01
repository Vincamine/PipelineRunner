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

  /**
   * Get the secret key.
   *
   * @return Secret key
   */
  public String getSecretKey() {
    return secretKey;
  }

  /**
   * Set the secret key.
   *
   * @param secretKey Secret key
   */
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  /**
   * Get the expiration time in milliseconds.
   *
   * @return Expiration time in milliseconds
   */
  public long getExpirationMs() {
    return expirationMs;
  }

  /**
   * Set the expiration time in milliseconds.
   *
   * @param expirationMs Expiration time in milliseconds
   */
  public void setExpirationMs(long expirationMs) {
    this.expirationMs = expirationMs;
  }
}

