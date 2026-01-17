package com.sundalei.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.sundalei.dto.PostUrlResponse;
import com.sundalei.model.Post;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class MediaService {

  private static final Logger log = LoggerFactory.getLogger(MediaService.class);
  private static final String API_URL = "https://onlyfans.com/api2/v2";

  private final RestClient restClient;
  private final ApiAuthService apiAuthService;
  private final ObjectMapper objectMapper;

  public MediaService(
      RestClient restClient, ApiAuthService apiAuthService, ObjectMapper objectMapper) {
    this.restClient = restClient;
    this.apiAuthService = apiAuthService;
    this.objectMapper = objectMapper;
  }

  public List<PostUrlResponse> listMediaContents(String profile, Long daysOld) {
    log.info("gather posts of {}", profile);

    // Get user info
    JsonNode userInfo = getUserInfo(profile);
    if (userInfo == null || !userInfo.has("id")) {
      log.error("Could not find user: {}", profile);
      return Collections.emptyList();
    }
    String userId = userInfo.get("id").asString();
    log.info("Found user ID: {}", userId);

    String endpoint = "/users/" + userId + "/posts";

    // Get all posts with pagination
    List<Post> posts = getAllPosts(endpoint, daysOld);
    log.info("Found {} posts for {}", posts.size(), profile);

    // Save posts to file
    savePostsToFile(posts, profile);

    // Filter posts and convert to simplified format
    List<PostUrlResponse> simplifiedPosts = new ArrayList<>();

    // Return only last 5 posts if more than 5, in descending order
    List<Post> postsToReturn = posts;
    if (posts.size() > 5) {
      // Reverse the list to get descending order (most recent first)
      List<Post> reversedPosts = new ArrayList<>(posts);
      Collections.reverse(reversedPosts);
      // Take first 5 (most recent)
      postsToReturn = reversedPosts.subList(0, 5);
    }

    // Convert each post to the simplified format
    for (Post post : postsToReturn) {
      if (post.isCanViewMedia() && post.getMedia() != null) {
        for (com.sundalei.model.Media media : post.getMedia()) {
          if (media.isCanView() && media.getFiles() != null && media.getFiles().getFull() != null) {
            simplifiedPosts.add(
                new PostUrlResponse(post.getPostedAt(), media.getFiles().getFull().getUrl()));
          }
        }
      }
    }

    return simplifiedPosts;
  }

  private JsonNode getUserInfo(String profile) {
    String endpoint = "/users/" + profile;
    Map<String, String> queryParams = Collections.emptyMap();
    HttpHeaders headers = apiAuthService.createSignedHeaders(endpoint, queryParams);

    ResponseEntity<JsonNode> response = restClient
        .get()
        .uri(API_URL + endpoint)
        .headers(h -> h.addAll(headers))
        .retrieve()
        .toEntity(JsonNode.class);

    return response.getBody();
  }

  private List<Post> getAllPosts(String endpoint, Long daysOld) {
    List<Post> allPosts = new ArrayList<>();
    int limit = 50;
    String paginationCursor = null; // Used for pagination cursor
    String initialAfterPublishTime = null; // Used for age filter

    // Calculate initialAfterPublishTime from daysOld if provided
    if (daysOld != null && daysOld > 0) {
      // Convert days to milliseconds and subtract from current time
      long timestamp = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000);
      // Format as ISO timestamp with nanoseconds
      initialAfterPublishTime = String.format("%.6f", timestamp / 1000.0);
      log.info(
          "Applying age filter: {} days (afterPublishTime: {})", daysOld, initialAfterPublishTime);
    }

    while (true) {
      UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(API_URL + endpoint)
          .queryParam("limit", "50")
          .queryParam("order", "publish_date_asc");

      // Use initial afterPublishTime for first request, then pagination cursor for
      // subsequent
      String afterPublishTime = (paginationCursor != null) ? paginationCursor : initialAfterPublishTime;
      if (afterPublishTime != null) {
        builder.queryParam("afterPublishTime", afterPublishTime);
      }

      Map<String, String> queryParams = builder.build().getQueryParams().toSingleValueMap();
      HttpHeaders headers = apiAuthService.createSignedHeaders(endpoint, queryParams);

      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {

      }

      ResponseEntity<JsonNode> response = restClient
          .get()
          .uri(builder.toUriString())
          .headers(h -> h.addAll(headers))
          .retrieve()
          .toEntity(JsonNode.class);

      JsonNode body = response.getBody();
      List<Post> pagePosts = new ArrayList<>();

      if (body != null) {
        if (body.isArray()) {
          for (JsonNode node : body) {
            pagePosts.add(objectMapper.convertValue(node, Post.class));
          }
        }
      }

      if (pagePosts.isEmpty()) {
        break;
      }

      allPosts.addAll(pagePosts);

      if (pagePosts.size() < limit) {
        break;
      }

      // Set cursor for next page
      Post lastPost = pagePosts.get(pagePosts.size() - 1);
      paginationCursor = lastPost.getPostedAtPrecise();
    }
    return allPosts;
  }

  private void savePostsToFile(List<Post> posts, String profile) {
    // Generate filename with current date
    String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
    String filename = "posts_retrieval_" + currentDate + ".txt";

    try (FileWriter writer = new FileWriter(filename)) {
      log.info("Saving {} posts to file: {}", posts.size(), filename);

      for (Post post : posts) {
        // Write separator
        writer.write("-----\n");

        // Write datetime
        writer.write("Datetime: " + post.getPostedAt() + "\n");

        // Write pretty-printed post content
        writer.write("Post content:\n");
        String prettyPost = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(post);
        writer.write(prettyPost + "\n");

        // Write separator
        writer.write("-----\n\n");
      }

      log.info("Successfully saved posts to file: {}", filename);
    } catch (IOException e) {
      log.error("Failed to save posts to file: {}", filename, e);
    }
  }
}
