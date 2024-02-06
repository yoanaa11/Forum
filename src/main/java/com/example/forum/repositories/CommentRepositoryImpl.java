package com.example.forum.repositories;

import com.example.forum.exceptions.EntityNotFoundException;
import com.example.forum.models.Comment;
import com.example.forum.models.Post;
import com.example.forum.models.Reaction_comments;
import com.example.forum.models.User;
import com.example.forum.repositories.contracts.CommentRepository;
import com.example.forum.utils.CommentFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> getAllCommentsByPostId(int postId, CommentFilterOptions commentFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, postId);

            if (post == null) {
                throw new EntityNotFoundException("Post", "id", String.valueOf(postId));
            }

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            filters.add(" post.id = :postId ");
            params.put("postId", postId);


            commentFilterOptions.getContent().ifPresent(value -> {
                filters.add("content like :content");
                params.put("content", String.format("%%%s%%", value));
            });

            StringBuilder queryString = new StringBuilder("from Comment");

            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(commentFilterOptions));

            Query<Comment> query = session.createQuery(queryString.toString(), Comment.class);
            query.setProperties(params);
            List<Comment> list = query.list();

            if (list.isEmpty()) {
                throw new EntityNotFoundException("Comments");
            }

            return list;
        }
    }

    @Override
    public Comment getCommentById(int commentId) {
        try (Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, commentId);

            if (comment == null) {
                throw new EntityNotFoundException("Comment", "id", String.valueOf(commentId));
            }

            return comment;
        }
    }

    @Override
    public void createComment(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateComment(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteComment(int commentId, User user) {
        Comment commentToDelete = getCommentById(commentId);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            for (Reaction_comments reaction : commentToDelete.getReactions()) {
                session.remove(reaction);
            }

            session.remove(commentToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(CommentFilterOptions commentFilterOptions) {
        if (commentFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (commentFilterOptions.getSortBy().get()) {
            case "content":
                orderBy = "content";
                break;
        }

        orderBy = String.format(" order by %s", orderBy);

        if (commentFilterOptions.getSortOrder().isPresent() && commentFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}