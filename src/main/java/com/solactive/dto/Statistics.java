package com.solactive.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Statistics implements Serializable {
    private Double avg;
    private Double max;
    private Double min;
    private Long count;


}
