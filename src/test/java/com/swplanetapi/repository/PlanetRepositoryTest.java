package com.swplanetapi.repository;

import com.swplanetapi.domain.Planet;
import com.swplanetapi.domain.QueryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.swplanetapi.common.PlanetConstants.PLANET;
import static com.swplanetapi.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DataJpaTest
public class PlanetRepositoryTest {
    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);
    }

    @Test
    public void createPlanetWithValidDataReturnsPlanet() {
        Planet planet = planetRepository.save(PLANET);
        Planet response = testEntityManager.find(Planet.class, planet.getId());

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(PLANET.getName());
        assertThat(response.getClimate()).isEqualTo(PLANET.getClimate());
        assertThat(response.getTerrain()).isEqualTo(PLANET.getTerrain());
    }

    @Test
    public void createPlanetWithInvalidDataThrowsException() {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> planetRepository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);;
    }

    @Test
    public void createPlanetWithExistingNameThrowsException() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanetByExistingIdReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> response = planetRepository.findById(planet.getId());

        assertThat(response).isNotEmpty();
        assertThat(response.get().getId()).isEqualTo(planet.getId());
    }

    @Test
    public void getPlanetByUnExistingIdReturnsEmpty() {
        Optional<Planet> response = planetRepository.findById(1L);

        assertThat(response).isEmpty();
    }

    @Test
    public void getPlanetByExistingNameReturnsPlanet() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> response = planetRepository.findByName(planet.getName());

        assertThat(response).isNotEmpty();
        assertThat(response.get().getName()).isEqualTo(planet.getName());
    }

    @Test
    public void getPlanetByUnExistingNameReturnsEmpty() {
        Optional<Planet> response = planetRepository.findByName("name'");

        assertThat(response).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanetReturnsFilteredPlanets() {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);
        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
        assertThat(responseWithFilters.get(0).getName()).isEqualTo(TATOOINE.getName());
    }

    @Test
    public void listPlanetReturnsEmptyPlanets() {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());

        List<Planet> responseWithoutFilters = planetRepository.findAll(query);

        assertThat(responseWithoutFilters).isEmpty();
        assertThat(responseWithoutFilters).hasSize(0);
    }


    @Test
    public void removePlanetWithExistingIdRemovePlanetFromDatabase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        assertThatCode(() -> planetRepository.deleteById(planet.getId())).doesNotThrowAnyException();
    }
}
