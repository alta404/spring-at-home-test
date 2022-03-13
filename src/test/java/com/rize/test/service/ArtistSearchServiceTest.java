package com.rize.test.service;

import com.rize.test.model.Artist;
import com.rize.test.model.ArtistCategory;
import com.rize.test.respository.ArtistRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.rize.test.service.ArtistSearchService.LIKE_SEARCH_FORMAT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class ArtistSearchServiceTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistSearchService artistSearchService;

    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
        Stream.of(ArtistCategory.values())
                .parallel()
                .forEach(
                artistCategory -> IntStream.range(0, 1000)
                        .peek(val -> log.debug("In Thread {}", Thread.currentThread().getName()))
                        .boxed()
                        .map(i -> getSemiRandomArtist(i, artistCategory))
                        .forEach(artist -> artistRepository.save(artist))
        );
    }

    private Artist getSemiRandomArtist(int i, ArtistCategory artistCategory) {
        int month = i%12 + 1;
        int day = i%30 + 1;
        int year = 1990 + i%30;
        var birthday = LocalDate.of(year, month, day);
        return Artist.builder()
                .firstName(String.format("name_%d", i))
                .category(artistCategory)
                .lastName(String.format("name_%d", i))
                .birthday(birthday)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    public void testFormat() {
        var formatted = String.format(LIKE_SEARCH_FORMAT, "John");
        log.info("formatted to {}", formatted);
        assertEquals("%John%", formatted);
    }

    @Test
    public void testSpecifications() {
        var all = artistRepository.findAll();
        log.info("all artists {}", all.size());
        var testCategory = ArtistCategory.ACTOR;
        var testMonth = 3;
        var testName = String.format("name_%d", testMonth + 5);
        var spec = artistSearchService.byCategoryBirthMonthAndName(testCategory, testMonth, testName);
        var result = artistRepository.findAll(spec);
        log.info("found {} artists matching search criteria ", result.size());
        assertThat(result.size()).isGreaterThan(0);
        var countWrongResults = result.stream()
                .filter(value -> value.getBirthday().getMonth().getValue() != testMonth)
                .filter(value -> !value.getCategory().equals(testCategory))
                .filter(value -> !value.getFirstName().toLowerCase().contains(testName))
                .filter(value -> !value.getLastName().toLowerCase().contains(testName))
                .count();
        assertEquals(0, countWrongResults, "results not matching search criteria!");
    }
}
