package com.ebarapp.ebar.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "review")

public class Review extends BaseEntity {

    @Column(name = "value")
    @NotNull
    @Max(value = 5)
    @Min(value = 0)
    private Double value;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "creation_date")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @PastOrPresent
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client creator;

}
