# Wumpus Enviroment
Code for the Wumpus Enviroment can be found in the src folder. I suggest a communication pattern as follows:
The Enviroment starts its message with "E;...". The bot starts its message with "B;...". Each argument or instruction is sperated with a ";".

# Java Bot
The Java Bot implementation is done. It can be found in the JavaBot folder.
All the algorithmic code goes into the Bot.java file. You can use:
- fileHelper.log(String message) to log files
- gameState contains the current game state in string format
- command has to be assigned the string of interaction that the agent is supposed to do
