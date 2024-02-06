package com.example.forum.services;
import com.example.forum.models.Tag;
import com.example.forum.repositories.TagRepositoryImpl;
import com.example.forum.services.TagServiceImpl;
import com.example.forum.utils.TagFilterOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TagServiceImplTest {

    @Mock
    private TagRepositoryImpl tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void get_all_tags_with_no_filters() {
        List<Tag> expectedTags = new ArrayList<>();
        expectedTags.add(new Tag());
        expectedTags.add(new Tag());

        when(tagRepository.getAllTags(null)).thenReturn(expectedTags);

        List<Tag> actualTags = tagService.getAllTags(null);

        assertEquals(expectedTags.size(), actualTags.size());
        for (int i = 0; i < expectedTags.size(); i++) {
            assertEquals(expectedTags.get(i), actualTags.get(i));
        }
    }
    @Test
    void getTagById_TagExists_ReturnsTag() {
        int tagId = 1;
        Tag expectedTag = new Tag();

        when(tagRepository.getTagById(tagId)).thenReturn(expectedTag);

        Tag actualTag = tagService.getTagById(tagId);

        assertEquals(expectedTag, actualTag);
    }

    @Test
    void getTagById_TagDoesNotExist_ReturnsNull() {
        int tagId = 1;

        when(tagRepository.getTagById(tagId)).thenReturn(null);

        Tag actualTag = tagService.getTagById(tagId);

        assertEquals(null, actualTag);
    }

}
