package com.rize.test.service;

import com.rize.test.model.Artist;
import com.rize.test.model.ArtistCategory;
import com.rize.test.respository.ArtistRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Optional.ofNullable;

@Service
public class ArtistSearchService {

    static final String LIKE_SEARCH_FORMAT = "%%%s%%";

    final ArtistRepository artistRepository;

    public ArtistSearchService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<Artist> findByCriteria(ArtistCategory category, Integer birthdayMonth, String search) {
        return artistRepository.findAll(byCategoryBirthMonthAndName(category, birthdayMonth, search));
    }

    Specification<Artist> byCategoryBirthMonthAndName(ArtistCategory category, Integer month, String name) {
        return categorySpec(category)
                .and(monthSpec(month))
                .and(nameSpec(name));
    }

    private Specification<Artist> categorySpec(ArtistCategory category) {
        return (root, query, criteriaBuilder) -> ofNullable(category)
                .map(c -> criteriaBuilder.equal(root.get("category"), c))
                .orElse(null);
    }

    private Specification<Artist> monthSpec(Integer month) {
        return (root, query, criteriaBuilder) -> ofNullable(month)
                .map(m -> criteriaBuilder.equal(criteriaBuilder.function("MONTH", Integer.class, root.get("birthday")), m))
                .orElse(null);
    }

    private Specification<Artist> nameSpec(String name) {
        return (root, query, criteriaBuilder) -> ofNullable(name)
                .map(str -> String.format(LIKE_SEARCH_FORMAT, name).toLowerCase())
                .map(n -> criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), n),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), n)))
                .orElse(null);
    }
}
