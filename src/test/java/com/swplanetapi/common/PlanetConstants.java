package com.swplanetapi.common;

import com.swplanetapi.domain.Planet;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {

  public static final Planet PLANET = new Planet("name", "climate", "terrain");
  public static final Planet PLANET_QUERY = new Planet("climate", "terrain");
  public static final Planet INVALID_PLANET = new Planet("", "", "");
  public static final Long PLANET_ID = 12l;

  public static final Planet TATOOINE = new Planet("Tatooine", "arid", "desert");
  public static final Planet ALDERAAN = new Planet("Alderaan", "temperate", "grasslands, mountains");
  public static final Planet YAVINIV = new Planet("Yavin IV", "temperate, tropical", "jungle, rainforeste");

  public static final List<Planet> PLANETS = new ArrayList<>(){
    {
      add(TATOOINE);
      add(ALDERAAN);
      add(YAVINIV);
    }
  };

}