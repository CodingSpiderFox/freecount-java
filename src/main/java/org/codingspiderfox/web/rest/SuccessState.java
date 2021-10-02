package org.codingspiderfox.web.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NotNull
@Getter
@Setter
public class SuccessState {

    @NotNull
    private Boolean success;
}
