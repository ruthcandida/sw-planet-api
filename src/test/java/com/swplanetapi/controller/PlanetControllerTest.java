package com.swplanetapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swplanetapi.domain.Planet;
import com.swplanetapi.service.PlanetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.swplanetapi.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.hamcrest.Matchers.hasSize;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PlanetService planetService;

  @Test
  public void createPlanetWithValidDataReturnsCreated() throws Exception {
    when(planetService.create(PLANET)).thenReturn(PLANET);

    mockMvc.perform(post("/planet").content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$").value(PLANET));
  }

  @Test
  public void createPlanetWithInvalidDataReturnsBadRequest() throws Exception {
    Planet emptyPlanet = new Planet();
    Planet invalidPlanet = new Planet("", "", "");

    when(planetService.create(PLANET)).thenReturn(PLANET);

    mockMvc
      .perform(
        post("/planet").content(objectMapper.writeValueAsString(emptyPlanet))
                .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnprocessableEntity());

    mockMvc
      .perform(
        post("/planet").content(objectMapper.writeValueAsString(invalidPlanet))
                .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void createPlanetWithExistingNameReturnConflict() throws Exception {
    when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

    mockMvc
      .perform(
        post("/planet").content(objectMapper.writeValueAsString(PLANET))
          .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isConflict());
  }

  @Test
  public void getPlanetByExistingIdReturnPlanet() throws Exception {
    PLANET.setId(1L);
    when(planetService.getById(anyLong())).thenReturn(Optional.of(PLANET));

    mockMvc
      .perform(
        get("/planet/{id}", PLANET.getId())
          .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(PLANET.getId()));
  }

  @Test
  public void getPlanetByUnExistingIdReturnPlanet() throws Exception {
    PLANET.setId(1L);
    when(planetService.getById(anyLong())).thenReturn(Optional.empty());

    mockMvc
      .perform(
        get("/planet/{id}", PLANET.getId())
          .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }

  @Test
  public void getPlanetByExistingNameReturnPlanet() throws Exception {
    when(planetService.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

    mockMvc
      .perform(
        get("/planet/name/" + PLANET.getName()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value(PLANET.getName()));
  }

  @Test
  public void getPlanetByUnExistingNameReturnPlanet() throws Exception {
    mockMvc
      .perform(
        get("/planet/name/nothing"))
      .andExpect(status().isNotFound());
  }

  @Test
  public void listPlanetsReturnFilteredPlanet() throws Exception {
    when(planetService.list(null, null)).thenReturn(PLANETS);
    when(planetService.list(TATOOINE.getClimate(), TATOOINE.getTerrain())).thenReturn(List.of(TATOOINE));

    mockMvc
      .perform(
        get("/planet/list"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(3)));

    mockMvc
      .perform(
        get("/planet/list?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0]").value(TATOOINE));


  }

  @Test
  public void listPlanetsReturnNoPlanet() throws Exception {
    when(planetService.list(any(), any())).thenReturn(List.of());

    mockMvc
      .perform(
        get("/planet/list"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(0)));

  }

  @Test
  public void removePlanetWithExistingIdReturnNoContent() throws Exception {
    mockMvc
      .perform(
        delete("/planet/{id}", 1L))
      .andExpect(status().isNoContent());

  }

  @Test
  public void removePlanetWithUnExistingIdReturnNoFound() throws Exception {
    doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(1L);

    mockMvc
      .perform(
        delete("/planet/{id}", 1L))
      .andExpect(status().isNotFound());

  }


}
