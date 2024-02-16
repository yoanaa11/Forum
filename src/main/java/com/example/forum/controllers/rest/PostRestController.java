package com.example.forum.controllers.rest;

import com.example.forum.exceptions.AuthorizationException;
import com.example.forum.exceptions.DuplicateEntityException;
import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.helpers.AuthenticationHelper;
import com.example.forum.helpers.PostMapper;
import com.example.forum.helpers.TagMapper;
import com.example.forum.models.Post;
import com.example.forum.models.Tag;
import com.example.forum.models.User;
import com.example.forum.models.dtos.PostDto;
import com.example.forum.models.dtos.TagDto;
import com.example.forum.services.contracts.PostService;
import com.example.forum.utils.PostFilterOptions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    private final PostService postService;
    private final AuthenticationHelper authenticationHelper;
    private final PostMapper postMapper;
    private final TagMapper tagMapper;

    @Autowired
    public PostRestController(PostService postService, AuthenticationHelper authenticationHelper, PostMapper postMapper, TagMapper tagMapper) {
        this.postService = postService;
        this.authenticationHelper = authenticationHelper;
        this.postMapper = postMapper;
        this.tagMapper = tagMapper;
    }

    @GetMapping
    public List<Post> getAll(@RequestHeader HttpHeaders headers,
                             @RequestParam(required = false) String title,
                             @RequestParam(required = false) String createdBy,
                             @RequestParam(required = false) LocalDate creationTime,
                             @RequestParam(required = false) String sortBy,
                             @RequestParam(required = false) String sortOrder) {
        PostFilterOptions postFilterOptions = new PostFilterOptions(title, createdBy, creationTime, sortBy, sortOrder);

        try {
            this.authenticationHelper.tryGetUser(headers);
            return postService.getAll(postFilterOptions);
        } catch (AuthorizationException e) {
            try {
                List<Post> topCommentedPosts = postService.getTopCommentedPosts();
                List<Post> mostRecentPosts = postService.getMostRecentPosts();

                List<Post> combinedList = new ArrayList<>();
                combinedList.addAll(topCommentedPosts);
                combinedList.addAll(mostRecentPosts);

                return combinedList;
            } catch (EntityNotFoundException exception) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
            }
        }
    }

    @GetMapping("/{id}")
    public Post getById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            authenticationHelper.tryGetUser(headers);
            return this.postService.getById(id);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/search")
    public Post getByTitle(@RequestHeader HttpHeaders headers, @RequestParam String title) {
        try {
            authenticationHelper.tryGetUser(headers);
            return this.postService.getByTitle(title);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping
    public Post create(@RequestHeader HttpHeaders headers, @Valid @RequestBody PostDto postDto, @Valid @RequestBody TagDto tagDto) {
        try {
            User user = this.authenticationHelper.tryGetUser(headers);
            Post post = this.postMapper.fromDto(postDto);
            Tag tag = this.tagMapper.fromDto(tagDto);
            postService.create(post, user, tag);
            return post;
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Post update(@RequestHeader HttpHeaders headers, @PathVariable int id, @Valid @RequestBody PostDto postDto,
                       @PathVariable int tagId, @Valid @RequestBody TagDto tagDto) {
        try {
            User user = this.authenticationHelper.tryGetUser(headers);
            Post post = this.postMapper.fromDto(id, postDto);
            Tag tag=this.tagMapper.fromDto(tagId,tagDto);
            this.postService.update(post, user,tag);
            return post;
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DuplicateEntityException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = this.authenticationHelper.tryGetUser(headers);
            this.postService.delete(id, user);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (AuthorizationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}