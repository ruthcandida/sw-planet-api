package com.swplanetapi.controller;

import com.swplanetapi.domain.Planet;
import com.swplanetapi.service.PlanetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/planet")
public class PlanetController {

    @Autowired
    private PlanetService planetService;


    @PostMapping
    public ResponseEntity<Planet> create(@RequestBody @Valid Planet body) {
        Planet planet = planetService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(planet);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> getById(@PathVariable Long id) {
        return planetService.getById(id).map(planet -> ResponseEntity.ok(planet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> getByName(@PathVariable String name) {
        return planetService.getByName(name).map(planet -> ResponseEntity.ok(planet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/list")
    public ResponseEntity<List<Planet>> list(@RequestParam(required = false) String climate, String terrain) {
        List<Planet> planets = planetService.list(climate, terrain);
        return ResponseEntity.ok(planets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        planetService.remove(id);
        return ResponseEntity.noContent().build();
    }



}
