package org.polytech.covid;

import java.time.Duration;
import java.util.List;

import io.github.bucket4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.polytech.covid.entity.VaccinationCenter;
import org.polytech.covid.repository.VaccinationCenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicVaccinationCenterRestControler {

    //Rajoute 10 tokens toutes les 10 minutes
    Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
    // Max 10 tokens
    Bandwidth limit = Bandwidth.classic(10, refill);
    Bucket bucket = Bucket.builder().addLimit(limit).build();
    
    @Autowired
    private VaccinationCenterRepository centerRepository;

    @GetMapping(path="api/public/centers")
    public ResponseEntity<List<VaccinationCenter>> getVaccinationCenter(
        @RequestParam(name="city", defaultValue = "") String city){
            if(bucket.tryConsume(1)) { // Utilise un token
                return ResponseEntity.ok(centerRepository.findAllByCityIgnoreCaseLike("%"+city+"%"));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    @GetMapping(value = "infos/429")
    public ResponseEntity<String> infos() {

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1); // Utilise un token et vérifie ce qu'il reste
        if(probe.isConsumed()) {
            return ResponseEntity.ok()
                    .header("X-Rate-Limit-Remaining", Long.toString(probe.getRemainingTokens()))
                    .body("Remaining tokens in bucket : " + (String)Long.toString(probe.getRemainingTokens()));
        }
        long delaiEnSeconde = probe.getNanosToWaitForRefill() / 1_000_000_000;
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", String.valueOf(delaiEnSeconde))
                .body("Remaining time before refreshing : " + String.valueOf(delaiEnSeconde));
    }
}
