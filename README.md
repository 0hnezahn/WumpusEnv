# 1. Introduction
Our new approach aims to allow multiple agent approaches to work together in a wumpus environment. Other parties are able to develope bots in their perferred language and are then able to load them into the enviroment and let them interact with eachother.
The environment developed in java will simulate one walkthrough in our partially observable, strategic, sequential, static, discrete multiagent wumpus world.

# 2. World description
### 2.1 Evniroment
+ wumpus environment is rectangular
+ Agents: one or more wumpi, two Player
+ possible observations: nothing, gold, pit, breeze, stench, exit (mby light), locationvector for scream, wall

### 2.2 States
+ gold: contains one gold
+ pit: kills agent
+ breeze: in von Neumann neighborhood to pit
+ stench: in von Neumann neighborhood to wumpus
+ vector: pointing from one agents position to the scream location  

Following further states are required to ensure that the bot knows in what direction a wall is. Otherwise it would be 
undetectable if a bot is actually moving or if he is just running against a wall:
+ wall up
+ wall down
+ wall left
+ wall right

### 2.3 Requirements
+ Rectangle: minimal size: |players|+2 for hight or width, the other side being at least |players| 
+ all gold has to be reachable

###### 2.4 Minimum number of ...
+ ...pits: |players| + 1
+ ...gold: |wumpi|
+ ...wumpi: |gold|


# 3. Actions
### 3.1 Perception of the player
Perception of a player is limited to the cell she is standing on (partially-observable)
This means the state of only this one cell is fixed but she is meant to remember the states of cells she has been to since they dont change a lot.

### 3.2 Actions of the player
- walk_up: the player can walk one cell every move
- walk_down: the player can walk one cell every move
- walk_left: the player can walk one cell every move
- walk_right: the player can walk one cell every move
- pick up: the player can pick up gold if she is on a gold cell
- climb: the wumpus cave has to be left climbing out the way the player entered
- scream: a scream can be heard by other players and will be represented as a vector
- nothing: do nothing for one turn

### 3.3 Goal
Pick up a piece of gold and leave as fast as possible


### 3.4 Wumpus agent actions
- walking: The Wumpus can only move one step per turn in his line of sight. He can walk over holes. If the Wumpus stands on an agent, the agent dies.
- turn: The agent can turn only once per turn either to the left or to the right, changing its facing direction.
- nothing: Stand one turn without taking an action

NOTE: The Wumpus movement is defined as follows:  
`if abs(w_spawnX - w_nextX) < 2 AND abs(w_spawnY - w_nextY) < 2:
    follow player
else:
    move back home`

### 3.5 Target of the Wumpus agent
The goal of the Wumpus agent is to protect the gold from the competing agents and keep it himself.


# 4. How to Start
So far the environment only worked under Windows.  
Make sure to have Java 17 installed. https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe

Open the folder "complete" which has the .jar aswell as an .exe for the environment. Also make sure to have the standard
java bot in the same folder if you want to spawn in a custom player. Then the only thing left to do is to double click
either the .jar or the .exe for the environment and a window should open up

# 5. Wumpus Enviroment
Code for the Wumpus Environment can be found in the src folder. The environment is developed in java. It consists of 4 main modules. 

### 5.1 Controller
The controller arranges the functionality of the GUI; for example buttons, labels, sliders and displaying the current gamestate. It also starts and executes the simulation.

### 5.2 Gamestate
The gamestate holds all of the important informations regarding the current game setup. It holds the player and wumpus objects aswell as the fieldstatus.
Each simulated step, these values are updated by the simulator, synchronized and visually displayed onto the GUI with the help of the controller.

### 5.3 Simulator
The simulator takes the current game state and applies its implemented logic onto the objects of the gamestate changeing them as defined below.

### 5.4 Communicator
The communicator allows the indirect conversation between the environment and agent bots with the help of files. Each bot creates itself a text file aswell as a log file. Inside the text file which has the name of the bot, it writes his interactions for the simulator and recieves percerptions from the simulator.

#### 5.4.1 Message formats
The environment starts its message with "C;" followed by further arguments later explained. The bot starts its message with "B;" with further arguments later explained.
Each argument or instruction is sperated with a ";". HINT: the first message received is of the format: 
C;INIT;width;height;END

##### 5.4.2 Message format for instructions:  
"B;movement;scream;pickup;climb" with:

- movement instruction: "UP", "DOWN", "LEFT", "RIGHT", "NOTHING" 
- scream instruction: "true", "false" 
- pickup instruction: "true", "false" 
- climb instruction: "true", "false"

ALWAYS assign a value for all 'variables'. For example: "B;UP;false,false,false"  
Notice that you can only set one interaction at once. If you put in or enable more than one interaction, the first in order is used.

##### 5.4.3 Message format for status of field:  
"C;[self];[x,y];hasgold;escaped;alive" with:  
- self: "STENCH", "WIND", "HOLE", "WUMPUS", "START", "PLAYER", "GOLD", "WALL_TOP", "WALL_BOTTOM", "WALL_LEFT", "WALL_RIGHT"
- x, y: Double
- hasgold: "true", "false"
- escaped: "true", "false"
- alive: "true", "false"

The value self can be repeated such as "[STENCH, WIND]".  
The [x,y] represents a vector of the scream. If no scream was received, the vector will be [0.0, 0.0]

# 6. Java Bot
The Java Bot implementation can be found in the JavaBot folder. All the algorithmic code goes into the Bot.java file. You can use:    
- fileHelper.log(String message) to log files
- gameState contains the current game state in string format
- command has to be assigned the string of interaction that the agent is supposed to do

# 7. Python Bot
