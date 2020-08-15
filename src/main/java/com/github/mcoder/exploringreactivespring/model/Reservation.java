package com.github.mcoder.exploringreactivespring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Reservation {

    @Id
    private Integer id;
    private String name;

}
