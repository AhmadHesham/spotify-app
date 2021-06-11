package auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.Map;

public class JWT {
    public static byte[] key = DatatypeConverter.parseBase64Binary("hello hey");

    public static String signToken(Map<String, Object> claims) {
        return Jwts.builder()
//                .setPayload(payload)
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public static Claims decodeJWT(String jwt) throws Exception {
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt).getBody();


        System.out.println(claims.get("expiration"));
        Date expiration = new Date((Long) claims.get("expiration"));



        if(expiration.compareTo(new Date())<0) {
            throw new JwtException("JWT token expired");
        }
        return claims;
    }

    public static void main(String[] args) {

        try {
            System.out.println(decodeJWT("eyJhbGciOiJIUzI1NiJ9.eyJ0b2tlbl9lbWFpbCI6InlhcmFzYW1haGFAaG90bWFpbC5jb20iLCJ0b2tlbl91c2VyX2lkIjoiMiIsInRva2VuX3JhdGluZyI6IjEwIiwidG9rZW5fbmFtZSI6InlhcmEgc2FtYWhhIiwiZXhwaXJhdGlvbiI6MTYxOTc1NzczMDUyMSwidG9rZW5fdXNlcm5hbWUiOiJ5YXJhIiwidG9rZW5fdHlwZSI6ImFydGlzdCJ9.PTdtj99Bq0Sx3U3WMHxMq3UUo3jOF8BUBEjey_iN_1I"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
