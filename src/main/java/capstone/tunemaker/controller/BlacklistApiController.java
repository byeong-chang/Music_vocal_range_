package capstone.tunemaker.controller;

import capstone.tunemaker.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BlacklistApiController {

    private final TokenBlacklistService tokenBlacklistService;

    @GetMapping("/blacklist")
    public void printBlacklist() {
        tokenBlacklistService.printBlacklist();;
    }

}
