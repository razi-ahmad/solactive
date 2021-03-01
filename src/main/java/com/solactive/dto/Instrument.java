package com.solactive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instrument implements Serializable {
    @NotNull(message = "Instrument identifier is missing")
    private String instrument;

    @NotNull(message = "Instrument price is missing")
    private Double price;

    @NotNull(message = "Instrument time stamp is missing")
    private Long timestamp;
}
