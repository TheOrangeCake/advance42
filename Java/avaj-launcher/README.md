# avaj-launcher

A Java aircraft flight simulator that models how different aircraft types move through changing weather conditions. The simulation reads a scenario file, registers all aircraft to a weather tower, and runs for a specified number of turns — each turn the weather changes and every aircraft updates its position accordingly.

Created by Nguyen NGUYEN (hoannguy) from 42 Lausanne.

---

## Design Patterns

| Pattern | Where Used |
|---------|-----------|
| **Singleton** | `AircraftFactory`, `WeatherProvider`, `Scenario` |
| **Factory** | `AircraftFactory.newAircraft()` creates Helicopter, JetPlane, or Balloon |
| **Observer** | `Tower` (subject) notifies registered `Flyable` observers on weather change |
| **Strategy** | Each aircraft type implements `updateConditions()` differently |

---

## Getting Started

**Requires:** Java (JDK)

```bash
# Compile
make build

# Run with the provided scenario
make run

# Clean build artifacts and output
make clean
```

Or manually:
```bash
javac -d classes $(find . -name "*.java")
java -cp classes Simulator scenario.txt
```

---

## Scenario File Format

```
<number_of_turns>
<Type> <Name> <longitude> <latitude> <height>
...
```

**Example `scenario.txt`:**
```
25
Balloon B1 2 3 20
JetPlane J1 23 44 32
Helicopter H1 654 33 20
Helicopter H2 22 33 44
Balloon B2 102 22 34
```

- First line: positive integer — number of simulation turns
- Each subsequent line: one aircraft (type, name, and 3D coordinates)
- Height is capped at 100; longitude and latitude must be non-negative
- Accepted types (case-insensitive): `Balloon`, `JetPlane`, `Jet Plane`, `Helicopter`

---

## Aircraft Behavior

Each aircraft reacts differently to weather each turn:

| Weather | Helicopter | JetPlane | Balloon |
|---------|-----------|---------|--------|
| SUN | longitude +10, height +2 | latitude +10, height +2 | longitude +2, height +4 |
| RAIN | longitude +5 | latitude +5 | height −5 |
| FOG | longitude +1 | latitude +1 | height −3 |
| SNOW | height −12 | height −7 | height −15 |

An aircraft **lands** (and unregisters from the tower) when its height drops to 0 or below. The simulation ends early if all aircraft have landed.

---

## Output Format

Results are written to `simulation.txt`:

```
-------------------------------------
Tower says: Balloon#B1(1) registered to weather tower.
Tower says: JetPlane#J1(2) registered to weather tower.
...
-------------------------------------
Balloon#B1(1):    SUNNY.    Longitude: 4,   Latitude: 3,    Height: 24
JetPlane#J1(2):   SUNNY.    Longitude: 33,  Latitude: 54,   Height: 34
...
-------------------------------------
Balloon#B1(1) landing.
Tower says: Balloon#B1(1) unregistered from weather tower.
-------------------------------------
```

---

## Project Structure

```
avaj-launcher/
├── Makefile
├── scenario.txt
└── src/
    ├── Simulator.java                 # Entry point
    ├── flyables/
    │   ├── Flyable.java               # Abstract base
    │   └── aircrafts/
    │       ├── Aircraft.java          # Common aircraft logic
    │       ├── Helicopter.java
    │       ├── JetPlane.java
    │       ├── Balloon.java
    │       └── AircraftFactory.java
    ├── weathers/
    │   ├── WeatherProvider.java       # Singleton; caches weather per coordinate
    │   └── towers/
    │       ├── Tower.java             # Observer base
    │       └── WeatherTower.java
    ├── utils/
    │   ├── Coordinates.java           # 3D position with bounds enforcement
    │   ├── FileReader.java            # Parses scenario file
    │   ├── FileWriter.java            # Writes to simulation.txt
    │   └── Scenario.java             # Singleton holding simulation state
    └── exceptions/
        ├── BadFileException.java
        ├── BadFlyableException.java
        ├── BadWeatherException.java
        └── BadProgrammerException.java
```

---

## Implementation Notes

- **Coordinate bounds:** longitude and latitude are clamped to `[0, Integer.MAX_VALUE]`; height is clamped to `[0, 100]`
- **Weather caching:** `WeatherProvider` assigns random weather the first time a coordinate is queried, then returns the cached value for that coordinate hash
- **Concurrent modification:** aircraft that land mid-turn are removed from a copy of the observer list to avoid `ConcurrentModificationException`
- **Error handling:** critical errors delete `simulation.txt` and exit with code 1
