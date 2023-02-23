package com.pokemon.ibrahim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.ibrahim.mapper.PokemonMapper;
import com.pokemon.ibrahim.model.Battle;
import com.pokemon.ibrahim.model.Player;
import com.pokemon.ibrahim.model.Pokemon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    // Normal attack damage between 1-10 HP
    public int normalAttack() {
        return (int) (Math.random() * 10) + 1;
    }
    // Special attack damage between 5-15 HP, takes 2 turns to execute
    public int specialAttack() {
        int damage = (int) (Math.random() * 11) + 5;
        return damage;
    }

    public void resetHealth(Player player){
        player.getPokemon().setHealth(20);
    }

    public int chooseFirstPlayer() {
        Random rand = new Random();
        return rand.nextDouble() < 0.5 ? 0 : 1;
    }

    public void handleRounds(Player player1, Player player2, StringBuilder sb,int turnsCount ,Battle battle){
        while ( battle.getWinner() == null) {
            turnsCount++;
            // Player 1 attacks
            sb.append("\nPlayer " + player1.getName() + " attacks Player " + player2.getName() + "\n");
            player2.getPokemon().takeDamage(normalAttack());
            sb.append("\nPlayer " + player2.getName() + " [health]:" + player2.getPokemon().getHealth() + "\n");
            //If player 2 looses
            if (player2.getPokemon().getHealth() <= 0) {
                //Reset
                player1.setWonRounds(player1.getWonRounds() + 1);
                this.resetHealth(player1);
                this.resetHealth(player2);
                turnsCount=0;
                sb.append("\nTHE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player1.getName() + "\n");
                break;
            }
            // Player 2 attacks
            sb.append("\nPlayer " + player2.getName() + " attacks Player " + player1.getName() + "\n");
            player1.getPokemon().takeDamage(normalAttack());
            sb.append("\nPlayer " + player1.getName() + "[health]:" + player1.getPokemon().getHealth() + "\n");
            if (player1.getPokemon().getHealth() <= 0) {
                player2.setWonRounds(player2.getWonRounds() + 1);
                //Reset
                this.resetHealth(player1);
                this.resetHealth(player2);
                turnsCount=0;
                sb.append("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player2.getName() + "\n");
                break;
            }
            // Check if special attack is available for Player 1
            if (turnsCount % 2 == 1 && player1.getPokemon().getHealth() > 0) {
                sb.append("\nPlayer " + player1.getName() +" is launching [SPECIAL ATTACK]\n");
                player2.getPokemon().takeDamage(specialAttack());
                sb.append("\nPlayer " + player2.getName() + "[health]:" + player2.getPokemon().getHealth() + "\n");
                if (player2.getPokemon().getHealth() <= 0) {
                    player1.setWonRounds(player1.getWonRounds() + 1);
                    //Reset
                    this.resetHealth(player1);
                    this.resetHealth(player2);
                    turnsCount=0;
                    sb.append("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player1.getName() + "\n");
                    break;
                }
            }
            // Check if special attack is available for Player 2
            if (turnsCount % 2 == 1 && player2.getPokemon().getHealth() > 0) {
                sb.append("\nPlayer " + player2.getName() +" is launching [SPECIAL ATTACK]\n");
                player1.getPokemon().takeDamage(specialAttack());
                sb.append("\nPlayer " + player1.getName() + "[health]:" + player1.getPokemon().getHealth() + "\n");
                if (player1.getPokemon().getHealth() <= 0) {
                    player2.setWonRounds(player2.getWonRounds() + 1);
                    //Reset
                    this.resetHealth(player1);
                    this.resetHealth(player2);
                    turnsCount=0;
                    sb.append("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is " + player2.getName() + "\n");
                    break;
                }
            }
        }
    }

    public Battle start(List<Player> players) {
        StringBuilder sb = new StringBuilder();
        Battle battle = new Battle(players,null,sb);
        Random rand = new Random();
        int firstPlayer = this.chooseFirstPlayer();
        // Handle empty player list
        if (players == null || players.isEmpty()) {
            sb.append("\nThe battle cannot start without players !!!**********************\n");
            System.out.println(sb.toString());
            return null;
        }

        // Handle single player list
        if (players.size() == 1) {
            sb.append("\nThe battle cannot start with only one player !!!**********************\n");
            System.out.println(sb.toString());
            return null;
        }
        Player player1 = battle.getPlayers().get(firstPlayer);
        Player player2 = battle.getPlayers().get(1 - firstPlayer);
        int turnsCount = 0;
        int rounds = 3;
        sb.append("\n**The battle has started !!!***\n");
        while (rounds > 0 ) {
            this.handleRounds(player1, player2, sb,turnsCount,battle);
            rounds--;
            if (player1.getWonRounds() == 2 || player2.getWonRounds() == 2) {
                Player winner = player1.getWonRounds() == 2 ? player1 : player2;
                battle.setWinner(winner);
                sb.append("**");
                sb.append("\n******THE GAME HAS ENDED AND THE WINNER OF THIS GAME is " + winner.getName() + "\n");
                sb.append("**");
                break;
            }
            //If one of the players only won one game and we dont have anymore rounds aka
            //No one won
            if (player1.getWonRounds() < 2 && player2.getWonRounds() < 2 && rounds == 1) {
                sb.append("**");
                sb.append("\n******THE GAME HAS ENDED ,NO ONE WAS ABLE TO WIN 2/3 ROUNDS \n");
                sb.append("**");
                break;
            }
        }
        System.out.println(sb.toString());
        return battle;
    }
}
