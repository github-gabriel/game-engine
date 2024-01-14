# Game Engine

Das ist meine erste Game Engine (in Java),
nach [dieser](https://www.youtube.com/watch?v=VS8wlS9hF8E&list=PLRIWtICgwaX0u7Rf9zkZhLoLuZVfUksDP&ab_channel=ThinMatrix)
Tutorial Serie mit [LWJGL](https://www.lwjgl.org/), einer Library die Zugang zu verschiedenen APIs für Spiele
Entwicklung mit Java bietet. Jedoch wird für diese Engine LWJGL 3 anstatt von LWJGL 2 verwendet.
Bisher besteht die Szene, die alle Möglichkeiten der Engine demonstriert, aus einem einfachen
Terrain und mehreren Entities. "Bewegen" kann der Spieler sich mit Hilfe der Kamera, die sich
mit der Maus und WASD steuern lässt.

## Setup

1. Clone this repository

```
git clone https://github.com/github-gabriel/game-engine.git
```

2. Navigate to the .jar file

```
cd .\game-engine\out\artifacts\game_engine_jar\
```

3. Run the .jar file

```
java -jar .\game-engine.jar
```

## Resourcen

Meine gesamten Rechnungen und Notizen, zur mathematischen Funktionsweise der Engine
sind [dieser PDF](Resources/GameEngine_Mathematik.pdf) zu entnehmen.

Für weitere grundlegende Informationen über die Funktionsweise von Engines im Allgemeinen,
siehe [diese Markdown](3D%20Engine.md) mit Informationen zur allgemeineren Funktionsweise von Engines.

Um die Dokumentation der Engine als Javadoc einzusehen, besuche die [Javadocs Index](docs/index.html) Datei,
von der aus man sich die Dokumentationen aller Klassen ansehen kann.
