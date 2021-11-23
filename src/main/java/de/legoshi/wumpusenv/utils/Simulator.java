package de.legoshi.wumpusenv.utils;

import de.legoshi.wumpusenv.game.*;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import lombok.Setter;

import java.util.ArrayList;

public class Simulator {

    private GameState gameState;
    private Communicator communicator;

    @Setter private Label messageLabel;

    public Simulator(GameState gameState, Communicator communicator) {
        this.gameState = gameState;
        this.communicator = communicator;
    }

    /**
     * 1. Update position of Wumpus object
     * 2. Do for all alive and not escaped players either:
     * 2.a Update position of Player object
     * 2.b Update scream var of Player object
     * 2.c Update climb var of Player object
     * 2.d Update climb var of Player object
     */
    public void simulateStep() {
        wumpusMove();
        for (Player all : gameState.getPlayers()) {

            if (all.isAlive()) {

                Point2D playerPos = all.getCurrentPosition();
                int playerPosX = (int) playerPos.getX();
                int playerPosY = (int) playerPos.getY();
                ArrayList<Status> fieldStatus = gameState.getGame()[playerPosY][playerPosX].getArrayList();

                if (all.isAlive() && !all.isHasEscaped()) {
                    if (all.getInstruction() != Instruction.NOTHING) {
                        all.updatePosition();
                    } else if (all.isScream()) {
                        for (Player receivers : gameState.getPlayers()) {
                            if (!receivers.equals(all)) {
                                receiveScream(receivers, playerPos);
                            }
                        }
                    } else if (all.isClimb()) {
                        if (fieldStatus.contains(Status.START)) {
                            if (all.isHasGold()) {
                                all.setHasEscaped(true);
                                messageLabel.setText(all.getId() + " has escaped with gold and won");
                                FileHelper.writeToLog(all.getId() + " has escaped with gold and won");
                            } else if (all.isHasEscaped()) {
                                messageLabel.setText(all.getId() + " has escaped");
                                FileHelper.writeToLog(all.getId() + " has escaped");
                            }
                        }
                    } else if (all.isPickup()) {
                        if (fieldStatus.contains(Status.GOLD)) {
                            all.setHasGold(true);
                            fieldStatus.remove(Status.GOLD);
                            messageLabel.setText(all.getId() + " has collected the gold");
                            FileHelper.writeToLog(all.getId() + " has collected the gold");
                        }
                    }
                }
                validatePlayerPos(all);
                syncPlayer(all);
                all.resetInstructions();
            }
        }
        syncWumpus();
    }

    public void setVisible(PlayerVision playerVision, boolean val) {
        if(playerVision.getSelf() != null) playerVision.getSelf().setVisible(val);
    }

    public void sendPlayerStates() {
        for (Player all : gameState.getPlayers()) {
            communicator.writeToFile(all, all.perceptionToString());
        }
    }

    public void receiveInstructions() {
        for (Player all : gameState.getPlayers()) {
            all.setStringToPlayer(communicator.readFile(all));
        }
    }

    public void validatePlayerPos(Player player) {
        Point2D playerPos = player.getCurrentPosition();
        int playerX = (int) playerPos.getX();
        int playerY = (int) playerPos.getY();
        int maxX = gameState.getWidth();
        int maxY = gameState.getHeight();

        if (playerX >= maxX || playerX < 0 || playerY >= maxY || playerY < 0) {
            player.setCurrentPosition(player.getOldPosition());
            player.setOldPosition(player.getOldPosition());
        }
        for(Wumpus wumpus : gameState.getWumpuses()) {
            if (player.getCurrentPosition().equals(wumpus.getCurrentPosition())) {
                player.setAlive(false);
            }
            if (player.getOldPosition().equals(wumpus.getCurrentPosition()) && player.getCurrentPosition().equals(wumpus.getOldPosition())) {
                player.setAlive(false);
            }
        }
        if(gameState.getGame()[(int)player.getCurrentPosition().getY()][(int)player.getCurrentPosition().getX()].getArrayList().contains(Status.HOLE)) {
            player.setAlive(false);
        }
    }

    public void syncPlayer(Player player) {
        int playerOldX = (int) player.getOldPosition().getX();
        int playerOldY = (int) player.getOldPosition().getY();
        int playerX = (int) player.getCurrentPosition().getX();
        int playerY = (int) player.getCurrentPosition().getY();
        ArrayList<Status> fieldStatus = gameState.getGame()[playerY][playerX].getArrayList();
        ArrayList<Status> fieldStatusOld = gameState.getGame()[playerOldY][playerOldX].getArrayList();

        if (!player.isAlive()) {
            fieldStatus.remove(Status.PLAYER);
            fieldStatusOld.remove(Status.PLAYER); // which is important????
            Platform.runLater(() -> messageLabel.setText(player.getId() + " just died. F"));
            FileHelper.writeToLog(player.getId() + " just died. F");
            return;
        }

        if(player.isHasGold() && player.isHasEscaped()) {
            fieldStatusOld.remove(Status.PLAYER);
            return;
        }

        fieldStatusOld.remove(Status.PLAYER);
        fieldStatus.add(Status.PLAYER);
    }

    public void receiveScream(Player receiver, Point2D start) {
        double xDirection = receiver.getCurrentPosition().getX() - start.getX();
        double yDirection = receiver.getCurrentPosition().getY() - start.getY();

        Point2D screamVector = new Point2D(xDirection, yDirection);
        receiver.getPlayerVision().setScream(screamVector);
    }

    public void wumpusMove() {

        for(Wumpus wumpus : gameState.getWumpuses()) {
            WumpusVision wumpusVision = getSurroundings(wumpus.getCurrentPosition());

            Point2D wumpusSpawn = wumpus.getWumpusSpawn();
            Point2D wumpusCurr = wumpus.getCurrentPosition();
            Point2D wumpusOld = wumpus.getOldPosition();

            if (wumpusVision.getLeft() != null && wumpusVision.getLeft().getArrayList().contains(Status.PLAYER)) {
                wumpus.setInstruction(Instruction.LEFT);
                wumpus.updatePosition();
                if (wumpus.spawnDistance() > 2) {
                    wumpus.setCurrentPosition(wumpusOld);
                } else return;
            }
            if (wumpusVision.getRight() != null && wumpusVision.getRight().getArrayList().contains(Status.PLAYER)) {
                wumpus.setInstruction(Instruction.RIGHT);
                wumpus.updatePosition();
                if (wumpus.spawnDistance() > 2) {
                    wumpus.setCurrentPosition(wumpusOld);
                } else return;
            }
            if (wumpusVision.getTop() != null && wumpusVision.getTop().getArrayList().contains(Status.PLAYER)) {
                wumpus.setInstruction(Instruction.UP);
                wumpus.updatePosition();
                if (wumpus.spawnDistance() > 2) {
                    wumpus.setCurrentPosition(wumpusOld);
                } else return;
            }
            if (wumpusVision.getBottom() != null && wumpusVision.getBottom().getArrayList().contains(Status.PLAYER)) {
                wumpus.setInstruction(Instruction.DOWN);
                wumpus.updatePosition();
                if (wumpus.spawnDistance() > 2) {
                    wumpus.setCurrentPosition(wumpusOld);
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
    }

    public void syncWumpus() {
        for(Wumpus wumpus : gameState.getWumpuses()) {
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
    }

    public WumpusVision getSurroundings(Point2D point2D) {
        WumpusVision vision = new WumpusVision();

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

    public PlayerVision getSelf(Point2D point2D) {
        PlayerVision vision = new PlayerVision();

        int posX = (int) point2D.getX();
        int posY = (int) point2D.getY();

        vision.setSelf(gameState.getGame()[posY][posX]);

        return vision;
    }

}
