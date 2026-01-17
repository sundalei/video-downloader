package com.sundalei.practice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "practiceClient", url = "https://jsonplaceholder.typicode.com", configuration = PracticeConfig.class)
public interface JsonPlaceHolderClient {

    @GetMapping("/posts")
    List<Object> getPosts();

    @GetMapping("/posts/{id}")
    Object getPost(@PathVariable("id") Long id);

    @GetMapping("/non-existing-path")
    Object getError();
}
