package com.rize.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rize.test.model.Artist;
import com.rize.test.model.ArtistCategory;
import com.rize.test.respository.ArtistRepository;
import com.rize.test.service.ArtistSearchService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ArtistController.class)
public class ArtistControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistRepository artistRepository;

    @MockBean
    private ArtistSearchService artistSearchService;

    @Test
    public void createArtistTest() throws Exception {
        Artist artist = getTestArtist();
        when(artistRepository.save(any(Artist.class)))
                .thenAnswer((Answer<Artist>) invocation -> {
                    Artist arg = invocation.getArgument(0, Artist.class);
                    arg.setId(0);
                    return arg;
                })
                .thenReturn(artist);
        mvc.perform(MockMvcRequestBuilders
                        .post("/artists")
                        .content(objectMapper.writeValueAsString(artist))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated());
    }

    private Artist getTestArtist() {
        return Artist.builder()
                .firstName("Famous")
                .lastName("Guy")
                .birthday(LocalDate.of(2000, 1, 1))
                .category(ArtistCategory.ACTOR)
                .build();
    }

}
