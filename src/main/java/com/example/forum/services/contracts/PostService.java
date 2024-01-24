package com.example.forum.services.contracts;

import com.example.forum.utils.PostFilterOptions;
import com.example.forum.models.Post;
import com.example.forum.models.User;

import java.util.List;
import java.util.Set;

public interface PostService {

    List<Post> getAll(PostFilterOptions postFilterOptions);

    Post getById(int id);

    Post getByTitle(String title);

    Set<Post> getByUser(User user);

    void create(Post post, User user);

    void update(Post post, User user);

    void delete(int id, User user);
}