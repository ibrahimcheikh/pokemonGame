package com.pokemon.ibrahim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.ibrahim.mapper.PokemonMapper;
import com.pokemon.ibrahim.model.Battle;
import com.pokemon.ibrahim.model.Player;
import com.pokemon.ibrahim.model.Pokemon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PokemonService {
    private final PokemonMapper pokemonMapper;
    private final WebClient webClient;
    private String apiUrl = "https://pokeapi.co/api/v2/pokemon?limit=50&offset=0";
    //In memory storage for players
    private List<Player> players = new ArrayList<>();
    private Battle battle;
    private int specialAttackCounter;

    public List<Pokemon> getAllPokemons() throws JsonProcessingException {
        JsonNode jsonNode = webClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        return objectMapper.readTree(response);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to parse response body", e);
                    }
                })
                .block();

        List<Pokemon> pokemons = new ArrayList<>();
        for (JsonNode node : jsonNode.path("results")) {
            String url = node.path("url").asText();
            String pokemonResponse = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            Pokemon pokemon = pokemonMapper.mapJsonToPokemonEntity(pokemonResponse);
            pokemons.add(pokemon);
        }
        return pokemons;
    }

    public void createNewPlayer(Player player) {
        players.add(player);
        System.out.println(player + "player added***");
        System.out.println(players + "Players added***");
    }

    public int normalAttack() {
        // Normal attack damage between 1-10 HP
        return (int) (Math.random() * 10) + 1;
    }

    public int specialAttack() {
        // Special attack damage between 5-15 HP, takes 2 turns to execute
        int damage = (int) (Math.random() * 11) + 5;
        this.specialAttackCounter = 0;
        return damage;
    }

    public void resetHealth(Player player){
        player.getPokemon().setHealth(20);
    }

    public Battle start(List<Player> players) {
        this.battle = new Battle(players, 3, null);
        Player player1 = battle.getPlayers().get(0);
        Player player2 = battle.getPlayers().get(1);
        int turnsCount = 0;
        int rounds = 3;
        System.out.println("***************The battle has started !!!**********************");
        while (rounds > 0 ) {
            while ( this.battle.getWinner() == null) {
                turnsCount++;
                // Player 1 attacks
                System.out.println("*************** Player " + player1.getName() + " attacks Player " + player2.getName()+ "\n");
                player2.getPokemon().takeDamage(normalAttack());
                System.out.println("***************Player " + player2.getName() + " [health]:" + player2.getPokemon().getHealth());
                //If player 2 looses
                if (player2.getPokemon().getHealth() <= 0) {
                    //reset pokemon healths and rounds
                    player1.setWonRounds(player1.getWonRounds() + 1);
                    this.resetHealth(player1);
                    this.resetHealth(player2);
                    System.out.println("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player1.getName());
                    break;
                }
                // Player 2 attacks
                System.out.println("*************** Player " + player2.getName() + " attacks Player " + player1.getName());
                player1.getPokemon().takeDamage(normalAttack());
                System.out.println("***************Player " + player1.getName() + "[health]:" + player1.getPokemon().getHealth());
                if (player1.getPokemon().getHealth() <= 0) {
                    player2.setWonRounds(player2.getWonRounds() + 1);
                    this.resetHealth(player1);
                    this.resetHealth(player2);
                    System.out.println("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player2.getName());
                    break;
                }
                // Check if special attack is available for Player 1
                if (turnsCount % 2 == 0 && player1.getPokemon().getHealth() > 0) {
                    System.out.println("*************** Player " + player1.getName() + " is launching [SPECIAL ATTACK]");
                    player2.getPokemon().takeDamage(specialAttack());
                    System.out.println("***************Player " + player2.getName() + " [health]:" + player2.getPokemon().getHealth());
                    if (player2.getPokemon().getHealth() <= 0) {
                        player1.setWonRounds(player1.getWonRounds() + 1);
                        this.resetHealth(player1);
                        this.resetHealth(player2);
                        System.out.println("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player1.getName());
                        break;
                    }
                }
                // Check if special attack is available for Player 2
                if (turnsCount % 2 == 1 && player2.getPokemon().getHealth() > 0) {
                    System.out.println("*************** Player " + player2.getName() + " is launching [SPECIAL ATTACK]");
                    player1.getPokemon().takeDamage(specialAttack());
                    System.out.println("***************Player " + player1.getName() + " [health]:" + player1.getPokemon().getHealth());
                    if (player1.getPokemon().getHealth() <= 0) {
                        player2.setWonRounds(player2.getWonRounds() + 1);
                        this.resetHealth(player1);
                        this.resetHealth(player2);
                        System.out.println("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player2.getName());
                        break;
                    }
                }
            }
                    rounds--;
                    if (player1.getWonRounds() == 2) {
                        battle.setWinner(player1);
                        System.out.println("******THE GAME HAS ENDED AND THE WINNER OF THIS GAME is " + player1.getName());
                        break;
                    }
                    if (player2.getWonRounds() == 2) {
                        battle.setWinner(player1);
                        System.out.println("******THE GAME HAS ENDED AND THE WINNER OF THIS GAME is " + player1.getName());
                        break;
                    }
                     if (player1.getWonRounds() < 2 && player2.getWonRounds() < 2 && rounds == 1 ){
                        System.out.println("******THE GAME HAS ENDED ,NO ONE WAS ABLE TO WIN 2/3 ROUNDS ");
                        break;
                    }
        }

                    return battle;
    }

}
/**
 * while (rounds > 0 ) {
 *     while ( this.battle.getWinner() == null) {
 *         // code for player turns
 *     }
 *     rounds--;
 *     if (player1.getWonRounds() == 2) {
 *         // code for declaring player1 as winner
 *     }
 *     else if (player2.getWonRounds() == 2) {
 *         // code for declaring player2 as winner
 *     }
 *     else{
 *         System.out.println("\n******NO ONE WON THE GAME  ");
 *         break;
 *     }
 * }
 */