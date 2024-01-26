package com.example.forum.models;

import jakarta.persistence.*;

@Entity
@Table(name = "reactions_posts")
public class Reaction_posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reaction_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_reaction")
    private com.example.forum.models.enums.Reaction reaction= com.example.forum.models.enums.Reaction.UNDEFINED;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Reaction_posts() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public com.example.forum.models.enums.Reaction getReaction() {
        return reaction;
    }

    public void setReaction(com.example.forum.models.enums.Reaction reaction) {
        this.reaction = reaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
