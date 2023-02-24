package com.pokemon.ibrahim.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Player {
    private String name;
    private Pokemon pokemon;
    private int wonRounds;
}
