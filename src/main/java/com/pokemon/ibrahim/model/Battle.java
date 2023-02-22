package com.pokemon.ibrahim.model;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class Battle {
    private Player player1;
    private Player player2;
    private int currentRound;
    private Player winner;

}
