# Wumpus Enviroment
Code for the Wumpus Enviroment can be found in the src folder. I suggest a communication pattern as follows: \
The Enviroment starts its message with "E;...". The bot starts its message with "B;...". Each argument or instruction is sperated with a ";".\

Message format for Instructions: \
"B;MOVE;SCREAM(b);PICKUP(b);CLIMB(b)" where (b) stands for boolean datatype. So for example \
"B;UP;false,false,false". Notice that you can only have one interaction at once. If you put in more then the first filled in in shown order is taken.

Message format for Status of field/player: \
"C;[TOP,TOP];[BOTTOM];[RIGHT];[LEFT];[X,Y];HASGOLD(b);ESCAPED(b)" \
The vector of scream is initialized with [0,0]

# Java Bot
The Java Bot implementation is done. It can be found in the JavaBot folder.
All the algorithmic code goes into the Bot.java file. You can use:
- fileHelper.log(String message) to log files
- gameState contains the current game state in string format
- command has to be assigned the string of interaction that the agent is supposed to do
