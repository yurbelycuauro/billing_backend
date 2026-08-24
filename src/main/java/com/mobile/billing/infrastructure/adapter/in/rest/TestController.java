package com.mobile.billing.infrastructure.adapter.in.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/test")
@AllArgsConstructor
public class TestController {

    @GetMapping(value = "/rankindTest", produces = MediaType.APPLICATION_JSON_VALUE)
public String getMethodName(@RequestParam String param) {
    int[] scores = {100, 80, 80, 60, 40, 20};
    int k = 4; // Límite de ranking permitido
    int jugadores = 0;

    // Ordenar de mayor a menor
    int[] points = Arrays.stream(scores)
            .boxed()
            .sorted(Comparator.reverseOrder())
            .mapToInt(Integer::intValue)
            .toArray();

    int ranking = 1;

    for (int i = 0; i < points.length; i++) {
        // Si el puntaje es 0, no califica
        if (points[i] == 0) {
            break;
        }

        // Si es un puntaje menor al anterior, el ranking actualiza a la posición real (i + 1)
        if (i > 0 && points[i] < points[i - 1]) {
            ranking = i + 1;
        }

        // Si el ranking está dentro del límite k (por ejemplo top 4), califica
        if (ranking <= k) {
            jugadores++;
        } else {
            break; // Ya superó el ranking k, no hace falta seguir recorriendo
        }
    }

        return "{\"cantidadJugadores\": " + jugadores + "}";
    }
    
    @GetMapping(value = "/testRanking", produces = MediaType.APPLICATION_JSON_VALUE)
    public String testRanking(){
        return "Test Ranking";
    }



}
