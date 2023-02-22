package com.pokemon.ibrahim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.ibrahim.mapper.PokemonMapper;
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
    private List<Player> players = new ArrayList<>(2);

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
    public void createNewPlayer(Player player){
       players.add(player);
    }

    public int attack(){
        Random random = new Random();
        return random.nextInt(10) + 1;
    }
    public int specialAttack(){
        int min = 5;
        int max = 15;
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }
}
