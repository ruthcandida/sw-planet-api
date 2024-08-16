package com.swplanetapi.service;

import com.swplanetapi.domain.Planet;
import com.swplanetapi.domain.QueryBuilder;
import com.swplanetapi.repository.PlanetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Example;

import java.util.*;

import static com.swplanetapi.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PlanetServiceTest {
    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository repository;

    @Test
    public void createPlanetWithValidDataReturnsPlanet() {
        when(repository.save(PLANET)).thenReturn(PLANET);

        Planet response = planetService.create(PLANET);

        assertThat(response).isEqualTo(PLANET);
    }

    @Test
    public void createPlanetWithInvalidDataReturnsThrowsException() {
        when(repository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanetByExistingIdReturnsPlanet() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(PLANET));

        Optional<Planet> response = planetService.getById(PLANET_ID);

        assertThat(response).isNotEmpty();
        assertThat(response.get()).isEqualTo(PLANET);
    }

    @Test
    public void getPlanetByUnExistingIdReturnsEmpty() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Planet> response = planetService.getById(PLANET_ID);

        assertThat(response).isEmpty();
    }

    @Test
    public void getPlanetByExistingNameReturnsPlanet() {
        when(repository.findByName(anyString())).thenReturn(Optional.of(PLANET));

        Optional<Planet> response = planetService.getByName(PLANET.getName());

        assertThat(response).isNotEmpty();
        assertThat(response.get().getName()).isEqualTo(PLANET.getName());
    }

    @Test
    public void getPlanetByUnExistingNameReturnsPlanet() {
        final String name = "UnExisting Name";
        when(repository.findByName(anyString())).thenReturn(Optional.empty());

        Optional<Planet> response = planetService.getByName(name);

        assertThat(response).isEmpty();
    }

    @Test
    public void listPlanetsReturnsPlanetAllPlanets() {
        List<Planet> planets = new ArrayList<>() {
            {
                add(PLANET);
            }
        };
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PLANET.getClimate(), PLANET.getTerrain()));
        when(repository.findAll(query)).thenReturn(planets);

        List<Planet> response = planetService.list(PLANET.getClimate(), PLANET.getTerrain());

        assertThat(response).isNotEmpty();
        assertThat(response).hasSize(1);
        assertThat(response.get(0)).isEqualTo(PLANET);
    }

    @Test
    public void listPlanetsReturnsPlanetNoPlanets() {
        when(repository.findAll(any())).thenReturn(Collections.emptyList());

        List<Planet> response = planetService.list(PLANET.getClimate(), PLANET.getTerrain());

        assertThat(response).isEmpty();
    }

    @Test
    public void removePlanetWithExistingIdDoesNotThrowAnyException() {
        assertThatCode(() -> planetService.remove(1l)).doesNotThrowAnyException();
    }

    @Test
    public void removePlanetWithUnExistingIdThrowsException() {
        doThrow(new RuntimeException()).when(repository).deleteById(99l);

        assertThatThrownBy(() -> planetService.remove(99l)).isInstanceOf(RuntimeException.class);
    }
}
