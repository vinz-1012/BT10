package vn.iotstar.services;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time:3600000}")
    private long jwtExpiration;

    /**
     * Trích xuất username (subject) từ JWT token.
     */
    public String extractUsername(String token) throws ParseException {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    /**
     * Trích xuất claim bất kỳ từ JWT token bằng Function resolver.
     */
    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        return claimsResolver.apply(claimsSet);
    }

    /**
     * Sinh token với thông tin cơ bản từ UserDetails.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Sinh token với JWSHeader, JWTClaimsSet.Builder() và SignedJWT từ Nimbus JOSE + JWT.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        try {
            // 1. Tạo JWSHeader với thuật toán HS256
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            // 2. Xây dựng Payload (JWTClaimsSet)
            Date now = new Date();
            Date validity = new Date(now.getTime() + jwtExpiration);

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(userDetails.getUsername())
                    .issueTime(now)
                    .expirationTime(validity);

            // Bổ sung các extra claims nếu có
            if (extraClaims != null) {
                extraClaims.forEach(claimsBuilder::claim);
            }

            JWTClaimsSet claimsSet = claimsBuilder.build();

            // 3. Tạo SignedJWT từ Header và ClaimsSet
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);

            // 4. Ký token bằng MACSigner với secretKey (tối thiểu 256-bit / 32 bytes)
            JWSSigner signer = new MACSigner(getSigningKey());
            signedJWT.sign(signer);

            // 5. Serialize thành chuỗi JWT (Header.Payload.Signature)
            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Lỗi khi ký JWT token bằng Nimbus: " + e.getMessage(), e);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của token bằng SignedJWT.parse() và MACVerifier.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            // 1. Parse token thành SignedJWT
            SignedJWT signedJWT = SignedJWT.parse(token);

            // 2. Xác minh chữ ký bằng MACVerifier
            JWSVerifier verifier = new MACVerifier(getSigningKey());
            if (!signedJWT.verify(verifier)) {
                return false;
            }

            // 3. Kiểm tra tính hợp lệ của claims
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Kiểm tra username có khớp với UserDetails không
            String username = claimsSet.getSubject();
            if (username == null || !username.equals(userDetails.getUsername())) {
                return false;
            }

            // Kiểm tra token đã hết hạn chưa
            Date expirationTime = claimsSet.getExpirationTime();
            return expirationTime != null && expirationTime.after(new Date());

        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    /**
     * Lấy thời gian sống của token (tính bằng mili-giây).
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * Lấy mảng byte secret key (đảm bảo ít nhất 256 bits / 32 bytes cho thuật toán HS256).
     */
    private byte[] getSigningKey() {
        try {
            byte[] decoded = Base64.getDecoder().decode(secretKey);
            if (decoded.length >= 32) {
                return decoded;
            }
        } catch (Exception ignored) {
        }
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }
}
