package com.rize.test.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@Entity
@Table(name = "artists")
public class Artist implements Serializable {

    private static final long serialVersionUID = 5357727966764186023L;

    public Artist(Integer id, Instant createdAt, Instant updatedAt, String firstName, String middleName, String lastName, ArtistCategory category, LocalDate birthday, String email, String notes) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.category = category;
        this.birthday = birthday;
        this.email = email;
        this.notes = notes;
    }

    @Id
    @SequenceGenerator(name = "H2_SEQ", sequenceName = "H2_SEQ")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "H2_SEQ")
    private Integer id;

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "timestamp with time zone")
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "timestamp with time zone")
    @UpdateTimestamp
    private Instant updatedAt;

    @NotEmpty(message = "first name cannot be empty")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @NotEmpty(message = "last name cannot be empty")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ArtistCategory category;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "email")
    private String email;

    @Column(name = "notes")
    private String notes;

    public Artist() {

    }
}
