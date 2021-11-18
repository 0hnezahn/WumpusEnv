package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.*;
import javafx.geometry.Point2D;

public class Simulator {

    private GameState gameState;
    private Communicator communicator;

    public Simulator(GameState gameState, Communicator communicator) {
        this.gameState = gameState;
        this.communicator = communicator;
    }

    public void simulateStep() {

        wumpusMove();

        for (Player all : gameState.getPlayers()) {
            if(all.isAlive()) {
                if(all.getInstruction() != Instruction.NOTHING) {
                    all.updatePosition();
                    validatePlayerPos(all);
                    syncPlayer(all);
                }
                else if (all.isScream()) {
                    for (Player receivers : gameState.getPlayers()) {
                        if (!receivers.equals(all)) {
                            receiveScream(receivers, all.getCurrentPosition());
                        }
                    }
                }
                else if(all.isClimb()) {
                    Point2D currentPos = all.getCurrentPosition();
                    if(gameState.getGame()[(int)currentPos.getY()][(int)currentPos.getX()].equals(Status.START)) {
                        if(all.isHasGold()) {
                            all.setHasEscaped(true);
                            System.out.println(all.getId() + " has escaped with gold and won!");
                        }
                    }
                }
                else if(all.isPickup()) {
                    Point2D currentPos = all.getCurrentPosition();
                    if(gameState.getGame()[(int)currentPos.getY()][(int)currentPos.getX()].equals(Status.GOLD)) {
                        all.setHasGold(true);
                        System.out.println(all.getId() + " has collected the gold!");
                        gameState.getGame()[(int)currentPos.getY()][(int)currentPos.getX()].getArrayList().remove(Status.GOLD);
                    }
                }
            }
            all.resetInstructions();
        }

        syncWumpus();

    }

    public void sendPlayerStates() {
        for (Player all : gameState.getPlayers()) {
            communicator.writeToFile(all, all.perceptionToString());
            // System.out.println(all.perceptionToString());
        }
    }

    public void receiveInstructions() {
        for (Player all : gameState.getPlayers()) {
            all.setStringToPlayer(communicator.readFile(all));
        }
    }

    public void validatePlayerPos(Player player) {
        int playerX = (int) player.getCurrentPosition().getX();
        int playerY = (int) player.getCurrentPosition().getY();
        int maxX = gameState.getWidth();
        int maxY = gameState.getHeight();
        Wumpus wumpus = gameState.getWumpus();

        if (playerX >= maxX || playerX < 0 || playerY >= maxY || playerY < 0) {
            player.setCurrentPosition(player.getOldPosition());
        }

        if (player.getCurrentPosition().equals(wumpus.getCurrentPosition())) {
            player.setAlive(false);
        }
        if (player.getOldPosition().equals(wumpus.getCurrentPosition()) && player.getCurrentPosition().equals(wumpus.getOldPosition())) {
            player.setAlive(false);
        }
        if(gameState.getGame()[playerY][playerX].getArrayList().contains(Status.HOLE)) {
            player.setAlive(false);
        }
    }

    public void syncPlayer(Player player) {
        int playerOldX = (int) player.getOldPosition().getX();
        int playerOldY = (int) player.getOldPosition().getY();
        int playerX = (int) player.getCurrentPosition().getX();
        int playerY = (int) player.getCurrentPosition().getY();

        if (!player.isAlive()) {
            gameState.getGame()[playerOldY][playerOldX].getArrayList().remove(Status.PLAYER);
            System.out.println(player.getId() + " just died. F");
            return;
        }

        if(player.isHasGold() && player.isHasEscaped()) {
            gameState.getGame()[playerOldY][playerOldX].getArrayList().remove(Status.PLAYER);
            return;
        }

        gameState.getGame()[playerOldY][playerOldX].getArrayList().remove(Status.PLAYER);
        gameState.getGame()[playerY][playerX].getArrayList().add(Status.PLAYER);

    }

    public void receiveScream(Player receiver, Point2D start) {
        double xDirection = receiver.getCurrentPosition().getX() - start.getX();
        double yDirection = receiver.getCurrentPosition().getY() - start.getY();

        Point2D screamVector = new Point2D(xDirection, yDirection);
        receiver.getPlayerVision().setScream(screamVector);
    }

    public void wumpusMove() {

        Wumpus wumpus = gameState.getWumpus();
        PlayerVision playerVision = getSurroundings(wumpus.getCurrentPosition());

        Point2D wumpusSpawn = wumpus.getWumpusSpawn();
        Point2D wumpusCurr = wumpus.getCurrentPosition();

        if (playerVision.getLeft() != null && playerVision.getLeft().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.LEFT);
            wumpus.updatePosition();
            if (wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }
        if (playerVision.getRight() != null && playerVision.getRight().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.RIGHT);
            wumpus.updatePosition();
            if (wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }
        if (playerVision.getTop() != null && playerVision.getTop().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.UP);
            wumpus.updatePosition();
            if (wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }
        if (playerVision.getBottom() != null && playerVision.getBottom().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.DOWN);
            wumpus.updatePosition();
            if (wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }

        int diffY = (int) (wumpusSpawn.getY() - wumpusCurr.getY());
        if (diffY > 0) {
            wumpus.setInstruction(Instruction.DOWN);
            wumpus.updatePosition();
            return;
        } else if (diffY < 0) {
            wumpus.setInstruction(Instruction.UP);
            wumpus.updatePosition();
            return;
        }

        int diffX = (int) (wumpusSpawn.getX() - wumpusCurr.getX());
        if (diffX > 0) {
            wumpus.setInstruction(Instruction.RIGHT);
            wumpus.updatePosition();
            return;
        } else if (diffX < 0) {
            wumpus.setInstruction(Instruction.LEFT);
            wumpus.updatePosition();
            return;
        }
        wumpus.setInstruction(Instruction.NOTHING);
    }

    public void syncWumpus() {
        Wumpus wumpus = gameState.getWumpus();
        int wOldX = (int) wumpus.getOldPosition().getX();
        int wOldY = (int) wumpus.getOldPosition().getY();
        int wX = (int) wumpus.getCurrentPosition().getX();
        int wY = (int) wumpus.getCurrentPosition().getY();

        gameState.getGame()[wOldY][wOldX].getArrayList().remove(Status.WUMPUS);
        gameState.getGame()[wY][wX].getArrayList().add(Status.WUMPUS);
        gameState.removeSurrounding(wOldY, wOldX, Status.STENCH);
        gameState.addSurrounding(wY, wX, Status.STENCH);
        wumpus.setOldPosition(wumpus.getCurrentPosition());
    }

    public PlayerVision getSurroundings(Point2D point2D) {
        PlayerVision vision = new PlayerVision();

        int maxX = gameState.getWidth();
        int maxY = gameState.getHeight();
        int posX = (int) point2D.getX();
        int posY = (int) point2D.getY();

        if (posX - 1 >= 0) vision.setLeft(gameState.getGame()[posY][posX - 1]);
        else vision.setLeft(null);

        if (posY + 1 < maxY) vision.setBottom(gameState.getGame()[posY + 1][posX]);
        else vision.setBottom(null);

        if (posX + 1 < maxX) vision.setRight(gameState.getGame()[posY][posX + 1]);
        else vision.setRight(null);

        if (posY - 1 >= 0) vision.setTop(gameState.getGame()[posY - 1][posX]);
        else vision.setTop(null);

        vision.setSelf(gameState.getGame()[posY][posX]);

        return vision;
    }

}
