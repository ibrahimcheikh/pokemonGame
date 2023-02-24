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
import java.util.Objects;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PokemonService {
    private final PokemonMapper pokemonMapper;
    private final WebClient webClient;
    private List<Player> players = new ArrayList<>();
    public List<Pokemon> getAllPokemons() throws JsonProcessingException {
        String apiUrl = "https://pokeapi.co/api/v2/pokemon?limit=50&offset=0";
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
        for (JsonNode node : Objects.requireNonNull(jsonNode).path("results")) {
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
    public List<Player> getAllPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player createNewPlayer(Player player) {
        players.add(player);
        return player;
    }

    public Player updatePlayer(String name, Player updatedPlayer) {
        Player existingPlayer = findPlayerByName(name);
        if (existingPlayer == null) {
            return null;
        }
        existingPlayer.setPokemon(updatedPlayer.getPokemon());
        return existingPlayer;
    }

    public Player findPlayerByName(String name) {
        return players.stream()
                .filter(player -> player.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    // Normal attack damage between 1-10 HP
    public int normalAttack() {
        return (int) (Math.random() * 10) + 1;
    }
    // Special attack damage between 5-15 HP, takes 2 turns to execute
    public int specialAttack() {
        return (int) (Math.random() * 11) + 5;
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
            sb.append("\nPlayer ").append(player1.getName()).append(" attacks Player ").append(player2.getName()).append("\n");
            player2.getPokemon().takeDamage(normalAttack());
            sb.append("\nPlayer ").append(player2.getName()).append(" [health]:").append(player2.getPokemon().getHealth()).append("\n");
            //If player 2 looses
            if (player2.getPokemon().getHealth() <= 0) {
                //Reset
                player1.setWonRounds(player1.getWonRounds() + 1);
                this.resetHealth(player1);
                this.resetHealth(player2);
                sb.append("\nTHE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is ").append(player1.getName()).append("\n");
                break;
            }
            // Player 2 attacks
            sb.append("\nPlayer ").append(player2.getName()).append(" attacks Player ").append(player1.getName()).append("\n");
            player1.getPokemon().takeDamage(normalAttack());
            sb.append("\nPlayer ").append(player1.getName()).append("[health]:").append(player1.getPokemon().getHealth()).append("\n");
            if (player1.getPokemon().getHealth() <= 0) {
                player2.setWonRounds(player2.getWonRounds() + 1);
                //Reset
                this.resetHealth(player1);
                this.resetHealth(player2);
                sb.append("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is ").append(player2.getName()).append("\n");
                break;
            }
            // Check if special attack is available for Player 1
            if (turnsCount % 2 == 1 && player1.getPokemon().getHealth() > 0) {
                sb.append("\nPlayer ").append(player1.getName()).append(" is launching [SPECIAL ATTACK]\n");
                player2.getPokemon().takeDamage(specialAttack());
                sb.append("\nPlayer ").append(player2.getName()).append("[health]:").append(player2.getPokemon().getHealth()).append("\n");
                if (player2.getPokemon().getHealth() <= 0) {
                    player1.setWonRounds(player1.getWonRounds() + 1);
                    //Reset
                    this.resetHealth(player1);
                    this.resetHealth(player2);
                    sb.append("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is ").append(player1.getName()).append("\n");
                    break;
                }
            }
            // Check if special attack is available for Player 2
            if (turnsCount % 2 == 1 && player2.getPokemon().getHealth() > 0) {
                sb.append("\nPlayer ").append(player2.getName()).append(" is launching [SPECIAL ATTACK]\n");
                player1.getPokemon().takeDamage(specialAttack());
                sb.append("\nPlayer ").append(player1.getName()).append("[health]:").append(player1.getPokemon().getHealth()).append("\n");
                if (player1.getPokemon().getHealth() <= 0) {
                    player2.setWonRounds(player2.getWonRounds() + 1);
                    //Reset
                    this.resetHealth(player1);
                    this.resetHealth(player2);
                    sb.append("\n******THE ROUND HAS ENDED AND THE WINNER OF THIS ROUND is ").append(player2.getName()).append("\n");
                    break;
                }
            }
        }
    }

    public Battle start(List<Player> players) {
        StringBuilder sb = new StringBuilder();
        Battle battle = new Battle(players,null,sb);
        int firstPlayer = this.chooseFirstPlayer();
        // Handle empty player list
        if (players == null || players.isEmpty()) {
            sb.append("\nThe battle cannot start without players !!!**********************\n");
            System.out.println(sb);
            return null;
        }

        // Handle single player list
        if (players.size() == 1) {
            sb.append("\nThe battle cannot start with only one player !!!**********************\n");
            System.out.println(sb);
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
                sb.append("\n******THE GAME HAS ENDED AND THE WINNER OF THIS GAME is ").append(winner.getName()).append("\n");
                sb.append("**");
                break;
            }
        }
        System.out.println(sb);
        return battle;
    }
}
