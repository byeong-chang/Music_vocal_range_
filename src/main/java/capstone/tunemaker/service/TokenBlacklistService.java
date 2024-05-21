package capstone.tunemaker.service;

import capstone.tunemaker.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final Map<String, String> blacklist = new ConcurrentHashMap<>();
    private final JWTUtil jwtUtil;

    public void blacklist(String token){
        blacklist.put(token, "");
    }

    public String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.split(" ")[1];
        }
        return null;
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredTokens() {
        for (String token : blacklist.keySet()) {
            if (jwtUtil.isExpired(token)) {
                blacklist.remove(token);
            }
        }
    }

    public void printBlacklist(){
        for (Map.Entry<String, String> stringStringEntry : blacklist.entrySet()) {
            log.error("stringStringEntry.getKey() = {}",  stringStringEntry.getKey());
            log.error("stringStringEntry.getValue() = {}",  stringStringEntry.getValue());
        }
    }

}
