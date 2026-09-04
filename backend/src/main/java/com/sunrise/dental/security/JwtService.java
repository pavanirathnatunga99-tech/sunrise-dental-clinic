package com.sunrise.dental.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service; import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.time.*; import java.time.temporal.ChronoUnit; import java.util.Date;

@Service
public class JwtService {
 private final SecretKey key; private final long hours;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-hours:8}") long hours){this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));this.hours=hours;}
 public String create(String username,String role){Instant now=Instant.now();return Jwts.builder().subject(username).claim("role",role).issuedAt(Date.from(now)).expiration(Date.from(now.plus(hours,ChronoUnit.HOURS))).signWith(key).compact();}
 public String username(String token){return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();}
}
