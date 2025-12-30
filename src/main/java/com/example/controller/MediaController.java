package com.example.controller;

import com.example.dto.PostUrlResponse;
import com.example.service.MediaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/media")
public class MediaController {

  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @GetMapping("/{profile}")
  public List<PostUrlResponse> listMediaContents(
      @PathVariable("profile") String profile,
      @RequestParam(name = "daysOld", required = false) Long daysOld) {
    return mediaService.listMediaContents(profile, daysOld);
  }
}
