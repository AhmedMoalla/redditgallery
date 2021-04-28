package com.novencia.bltech.scraper.batch.reader;

import com.novencia.bltech.api.dto.ExplorerFunctionParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "explorer-client", url = "http://${explorer.host}:${explorer.port}")
public interface ExplorerFunctionClient {

    @PostMapping(path = "/explorer")
    String getPosts(ExplorerFunctionParams params);
}
