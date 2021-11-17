package de.legoshi.wumpusenv.utils;

public enum Instruction {

    INIT,
    READY,

    UP,
    DOWN,
    LEFT,
    RIGHT,
    NOTHING,
    SCREAM,
    PICKUP;

    public static Instruction toInstruction(String s) {
        return switch (s) {
            case "INIT" -> Instruction.INIT;
            case "READY" -> Instruction.READY;
            case "UP" -> Instruction.UP;
            case "DOWN" -> Instruction.DOWN;
            case "LEFT" -> Instruction.LEFT;
            case "RIGHT" -> Instruction.RIGHT;
            case "NOTHING" -> Instruction.NOTHING;
            case "SCREAM" -> Instruction.SCREAM;
            default -> Instruction.PICKUP;
        };
    }

}
