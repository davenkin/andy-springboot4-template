package com.company.andy.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Component;

import static com.company.andy.common.util.Constants.ORG_EQUIPMENTS_CACHE;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClearingInitializer {

    @Caching(evict = {
            @CacheEvict(value = ORG_EQUIPMENTS_CACHE, allEntries = true),
    })
    public void clearCaches() {
        log.info("Cleared caches.");
    }
}
