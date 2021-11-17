package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.GameState;
import de.legoshi.wumpusenv.game.Player;
import de.legoshi.wumpusenv.game.PlayerVision;
import de.legoshi.wumpusenv.game.Wumpus;
import javafx.geometry.Point2D;

public class Simulator {

    private GameState gameState;

    public Simulator(GameState gameState) {
        this.gameState = gameState;
    }

    public void simulateStep() {

        wumpusMove();

        for (Player all : gameState.getPlayers()) {
            all.updatePosition();
            validatePlayerPos(all);
            syncPlayer(all);
            if (all.isScream()) {
                for (Player receivers : gameState.getPlayers()) {
                    if (!receivers.equals(all)) {
                        receiveScream(receivers, all.getCurrentPosition());
                    }
                }
            }
        }

        syncWumpus();

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

        if(player.getCurrentPosition().equals(wumpus.getCurrentPosition())) {
            player.setAlive(false);
        }
        if(player.getOldPosition().equals(wumpus.getCurrentPosition())) {
            player.setAlive(false);
        }
    }

    public void syncPlayer(Player player) {
        int playerOldX = (int) player.getOldPosition().getX();
        int playerOldY = (int) player.getOldPosition().getY();
        int playerX = (int) player.getCurrentPosition().getX();
        int playerY = (int) player.getCurrentPosition().getY();

        if(!player.isAlive()) {
            gameState.getGame()[playerOldY][playerOldX].getArrayList().remove(Status.PLAYER);
            gameState.getPlayers().remove(player);
            System.out.println(player.getId() + " jus died. F");
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

        if (playerVision.getLeft().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.LEFT);
            wumpus.updatePosition();
            if(wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }
        if (playerVision.getRight().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.RIGHT);
            wumpus.updatePosition();
            if(wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }
        if (playerVision.getTop().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.UP);
            wumpus.updatePosition();
            if(wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }
        if (playerVision.getBottom().getArrayList().contains(Status.PLAYER)) {
            wumpus.setInstruction(Instruction.DOWN);
            wumpus.updatePosition();
            if(wumpus.spawnDistance() > 2) {
                wumpus.setCurrentPosition(wumpus.getOldPosition());
            } else return;
        }

        int diffY = (int)(wumpusSpawn.getY() - wumpusCurr.getY());
        if(diffY > 0) {
            wumpus.setInstruction(Instruction.DOWN);
            wumpus.updatePosition();
            return;
        }
        else if(diffY < 0) {
            wumpus.setInstruction(Instruction.UP);
            wumpus.updatePosition();
            return;
        }

        int diffX = (int)(wumpusSpawn.getX() - wumpusCurr.getX());
        if(diffX > 0) {
            wumpus.setInstruction(Instruction.RIGHT);
            wumpus.updatePosition();
            return;
        }
        else if(diffX < 0) {
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
    }

    private PlayerVision getSurroundings(Point2D point2D) {
        PlayerVision vision = new PlayerVision();

        int maxX = gameState.getWidth();
        int maxY = gameState.getHeight();
        int posX = (int) point2D.getX();
        int posY = (int) point2D.getY();

        if (posX - 1 >= 0) vision.setLeft(gameState.getGame()[posY][posX - 1]);
        else vision.setLeft(null);

        if (posY - 1 >= 0) vision.setBottom(gameState.getGame()[posY - 1][posX]);
        else vision.setBottom(null);

        if (posX + 1 < maxX) vision.setRight(gameState.getGame()[posY][posX + 1]);
        else vision.setRight(null);

        if (posY + 1 < maxY) vision.setTop(gameState.getGame()[posY + 1][posX]);
        else vision.setTop(null);

        return vision;
    }

}
