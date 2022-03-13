package com.rize.test.controller;

import com.rize.test.model.Artist;
import com.rize.test.model.ArtistCategory;
import com.rize.test.respository.ArtistRepository;
import com.rize.test.service.ArtistSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/artists")
public class ArtistController {

    final ArtistRepository artistRepository;
    final ArtistSearchService artistSearchService;

    public ArtistController(ArtistRepository artistRepository, ArtistSearchService artistSearchService) {
        this.artistRepository = artistRepository;
        this.artistSearchService = artistSearchService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        return artistRepository.findById(Integer.valueOf(id)).map(
                ResponseEntity::ok
        ).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<?> index(@RequestParam(required = false) ArtistCategory category,
                                   @RequestParam(required = false) @Min(1) @Max(12) Integer birthdayMonth,
                                   @RequestParam(required = false) String search) {

        log.info("looking for category {} birthday month {} name {}", category, birthdayMonth, search);
        var results = artistSearchService.findByCriteria(category, birthdayMonth, search);
        return ResponseEntity.ok(results);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody Artist artist, HttpServletRequest request) {
        log.info("create artist {}", artist);
        Artist saved = artistRepository.save(artist);
        return ResponseEntity.created(URI.create(String.format("%s/%d", request.getRequestURI(), saved.getId())))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        artistRepository.deleteById(Integer.valueOf(id));
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        log.info("Bad request {}", ex.getMessage());
        var errors = ex.getAllErrors();
        log.info("validation errors: {}", errors);
        return ResponseEntity.badRequest()
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleGenericError(Exception ex) {
        log.info("Unknown error {}", ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(ex.getMessage());
    }
}
