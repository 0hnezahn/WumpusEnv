# Multi-Agent Wumpus Environment

## Table of Contents

1. [Introduction](#introduction)
2. [World Description](#world-description)
    - [Environment](#environment)
    - [States](#states)
    - [Requirements](#requirements)
    - [Minimum Entity Counts](#minimum-entity-counts)
3. [Actions](#actions)
    - [Player Perception](#player-perception)
    - [Player Actions](#player-actions)
    - [Wumpus Actions](#wumpus-actions)
    - [Goals](#goals)
4. [Bots](#bots)
    - [Java Bot](#java-bot)
    - [Python Bot](#python-bot)
5. [Communication Protocol](#communication-protocol)
    - [Message Formats](#message-formats)
    - [Example Log](#example-log)

---

## Introduction

Welcome to the **Multi-Agent Wumpus Environment**, a strategic, sequential, static, and discrete simulation developed in Java. This environment facilitates the development and testing of multiple agents (bots) that collaborate to steal gold from the Wumpus and escape successfully. Agents can be developed in various programming languages and seamlessly integrated into the environment to interact with one another.

![grafik](https://github.com/user-attachments/assets/0c5ee1e0-daca-48f1-a64a-b48f766b28af)

*Figure 1: Overview of the Wumpus Environment*

## World Description

### Environment

The Wumpus environment is a rectangular grid composed of cells. Each cell can contain different elements, and the environment supports multiple agents interacting within it.

- **Dimensions:** The environment's size is configurable, with a minimum size ensuring all entities can coexist without immediate conflicts.
- **Agents:**
  - **Wumpi:** Hostile creatures that protect the gold.
  - **Players:** Cooperative agents aiming to steal gold and escape.
- **Possible Observations:**
  - **Nothing:** No observable elements in the current cell.
  - **Gold:** Presence of gold.
  - **Pit:** A lethal hole that kills any agent that steps into it.
  - **Breeze:** Indicates proximity to a pit.
  - **Stench:** Indicates proximity to a Wumpus.
  - **Exit:** Location to escape the cave.
  - **Location Vector for Scream:** Direction from which a scream is heard.
  - **Wall:** Indicates the presence of a boundary.

### States

Each cell in the environment can be in one of several states:

- **SAFE:** The cell is free of hazards.
- **UNKNOWN:** The cell's status is not yet determined by any agent.
- **IMPOSSIBLE:** The cell is determined to be unsafe.
- **UNREACHABLE:** The cell cannot be accessed based on current knowledge.
- **INIT_STENCH:** Initial detection of stench indicating nearby Wumpus.
- **STENCH:** Continuous detection of stench.
- **WUMPUS:** Presence of a Wumpus.
- **WIND:** Presence of wind indicating a nearby pit.
- **HOLE:** Presence of a pit.

### Requirements

- **Rectangle Size:** 
  - **Minimum Width or Height:** At least the number of players plus two.
  - **Other Dimension:** At least the number of players.
- **Gold Accessibility:** All gold pieces must be reachable by at least one player.

### Minimum Entity Counts

- **Pits:** At least the number of players plus one.
- **Gold Pieces:** Equal to the number of Wumpi.
- **Wumpi:** Equal to the number of gold pieces.

## Actions

### Player Perception

Players have limited perception, restricted to the cell they occupy:

- **Current Cell Observation:** Players can observe the state of the cell they are currently on. They must remember previously visited cells to navigate effectively.

### Player Actions

Players can perform the following actions each turn:

- **Walk Up:** Move one cell upward.
- **Walk Down:** Move one cell downward.
- **Walk Left:** Move one cell to the left.
- **Walk Right:** Move one cell to the right.
- **Pick Up:** Collect gold if present in the current cell.
- **Climb:** Exit the cave via the starting position.
- **Scream:** Emit a scream that other players can detect as a directional vector.
- **Nothing:** Remain idle for one turn.

### Wumpus Actions

Wumpi behave strategically to protect the gold:

- **Movement:** 
  - Move one step per turn within their line of sight.
  - Can traverse over holes.
  - If a Wumpus occupies the same cell as a player, the player is killed.
- **Turn:** 
  - Can turn left or right once per turn to change facing direction.
- **Nothing:** Remain stationary for one turn.

**Wumpus Movement Logic:**

- If the Wumpus is within two cells of its spawn point and detects a player, it will pursue the player.
- Otherwise, the Wumpus moves back towards its spawn location.

### Goals

- **Players:** Collect a piece of gold and exit the cave as quickly as possible.
- **Wumpi:** Protect the gold and eliminate competing agents.

## Bots

### Java Bot

The Java Bot is implemented in the `Bot.java` file within the `java-bot` directory. It follows a strategic approach to navigate the Wumpus environment, detect hazards, collect gold, and communicate with other bots.

**Key Features:**

- **Field Mapping:** Maintains an internal map of the environment based on perceptions.
- **State Management:** Utilizes finite state machines to handle different behaviors (e.g., Explore, Flee, Cooperate).
- **Communication:** Uses file-based communication to coordinate with other bots.

**Usage:**

- Develop your bot logic within the `Bot.java` file.
- Launch multiple bot instances to enable multi-agent cooperation.

## Communication Protocol

The environment and bots communicate through structured messages written to and read from text files. Below are the formats used for different types of messages.

### Message Formats

#### Initialization Message

- **Environment to Bot:**

    ```
    C;INIT;width;height;END
    ```

    - **C:** Indicates a message from the Controller.
    - **INIT:** Initialization command.
    - **width;height:** Dimensions of the environment grid.
    - **END:** Marks the end of the initialization message.

- **Bot to Environment:**

    ```
    B;READY
    ```

    - **B:** Indicates a message from the Bot.
    - **READY:** Bot signals readiness to start.

#### Instruction Message

- **Bot to Environment:**

    ```
    B;movement;scream;pickup;climb
    ```

    - **movement:** One of `UP`, `DOWN`, `LEFT`, `RIGHT`, `NOTHING`.
    - **scream:** `true` or `false`.
    - **pickup:** `true` or `false`.
    - **climb:** `true` or `false`.

    **Note:** Only one action should be active at a time. If multiple actions are set to `true`, the first one in the order is executed.

#### Status Message

- **Environment to Bot:**

    ```
    C;[self];[x,y];hasGold;escaped;alive
    ```

    - **self:** Comma-separated list of statuses (e.g., `[STENCH, WIND]`).
    - **[x,y]:** Vector indicating the direction of a scream. `[0.0,0.0]` if no scream.
    - **hasGold:** `true` or `false`.
    - **escaped:** `true` or `false`.
    - **alive:** `true` or `false`.

## Example Log

Below is an example of a bot's log file demonstrating the interaction with the environment:

```
1. STEP 
--------------------------------------- 
[2024-09-30T17:29:12.222552400] BOT READ: 
[2024-09-30T17:29:12.227553300] C;[WALL_RIGHT,START,PLAYER];[6,3];[];[0.0,0.0];false;false;true

[2024-09-30T17:29:12.232551900] BOT PERFORMED: 
[2024-09-30T17:29:12.238064900] B;NOTHING;false;false;false;[]
--------------------------------------- 
2. STEP 
--------------------------------------- 
[2024-09-30T17:29:14.296716200] BOT READ: 
[2024-09-30T17:29:14.299720300] C;[WALL_RIGHT,START,PLAYER];[6,3];[];[0.0,0.0];false;false;true

[2024-09-30T17:29:14.321478700] BOT PERFORMED: 
[2024-09-30T17:29:14.326493700] B;LEFT;false;false;false;[]
--------------------------------------- 
3. STEP 
--------------------------------------- 
[2024-09-30T17:29:16.374401100] BOT READ: 
[2024-09-30T17:29:16.378402] C;[PLAYER];[5,3];[];[6.0,3.0];false;false;true
```

**Explanation:**

- **Step 1:**
  - **Bot Read:** Received the current state indicating a wall to the right, starting position, player presence, position vector `[6,3]`, and no gold, escape, or death.
  - **Bot Performed:** Decided to do nothing.

- **Step 2:**
  - **Bot Read:** Same as Step 1.
  - **Bot Performed:** Issued a `LEFT` movement command.

- **Step 3:**
  - **Bot Read:** Updated position to `[5,3]`, with a scream vector `[6.0,3.0]`.
