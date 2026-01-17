package com.sundalei.practice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@EnableFeignClients(basePackages = "com.sundalei.practice")
@ActiveProfiles("test")
public class OpenFeignPracticeTest {

    @Autowired
    private JsonPlaceHolderClient client;

    @Test
    public void testGetPosts() {
        System.out.println("Running testGetPosts...");
        var posts = client.getPosts();
        assertNotNull(posts);
        assertFalse(posts.isEmpty());
        System.out.println("Successfully fetched " + posts.size() + " posts.");
    }

    @Test
    public void testErrorDecoder() {
        System.out.println("Running testErrorDecoder...");
        Exception exception = assertThrows(RuntimeException.class, () -> {
            client.getError();
        });

        System.out.println("Caught expected exception: " + exception.getMessage());
        assertEquals("Practice: Resource not found!", exception.getMessage());
    }
}
