package main.java.de.legoshi.javabot;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * @author Benjamin Müller
 */

public class Bot {

    private FileHelper fileHelper;

    // C;[START,PLAYER];[0,0];[INFO];[0.0,0.0];false;false;true
    public String gameState;
    public String command = Command.NOTHING;
    private boolean initialised = false;
    private boolean setSpawn = false;

    // width und height werden beim execute gesetzt
    public int width;
    public int height;

    private FieldState[][] field;

    private Point startPosition;
    private Point currentPosition;
    private Point previousPosition;

    private ArrayList<FieldState.State> currentState;
    private ArrayList<FieldState.State> previousState;

    private String previousCommand = Command.NOTHING;

    private Point followWumpusPosition;
    private Point goldPos = new Point(-1, -1);
    private ArrayList<Point> possibleStarts = new ArrayList<>();

    private String info;
    private Point positionToMove;
    private double[] screamVec = new double[2];

    private boolean hasGold = false;
    private boolean dangerMove = false;
    private boolean firstScream = false;
    private boolean lastScream = false;
    private boolean leader = false;
    private BotState botState = BotState.COMM;

    public Bot(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public void execute() {
        if (!initialised) {
            constructField();
            return;
        }

        if (!setSpawn) setSpawn();
        if (!this.goldPos.equals(new Point(-1, -1))) getAllPossibleStarts();

        loadNewBotState();
        if (this.botState.equals(BotState.EXPLORE)) markPosition();
        if (this.botState.equals(BotState.EXPLORE) && !Arrays.equals(this.screamVec, new double[]{0, 0}))
            this.botState = BotState.SUPPORT;

        command = doNextAction();
        System.out.println("GAME STATE: " + gameState.replace("\n", ""));
        System.out.println("INFO STRING: " + info);
        System.out.println("COMMAND: " + command);
        System.out.println("GOLD: " + this.goldPos.toString());
        System.out.println("POTENTIAL STARTS: " + this.possibleStarts.toString());
        System.out.println("CURR_STATE: " + this.currentState.toString());
        System.out.println("CURR_BOT_STATE: " + this.botState.toString());
        System.out.println("CURR_POS: " + this.currentPosition.toString());
        this.previousCommand = command;
    }

    private void constructField() {
        this.field = new FieldState[width][height];
        initialised = true;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.field[x][y] = new FieldState(new Point(x, y));
            }
        }
    }

    private void setSpawn() {
        String[] gameArray = this.gameState.split(";");
        String posVec = gameArray[2].replaceAll("\\[", "").replaceAll("]", "");
        String[] posVecArr = posVec.split(",");
        this.startPosition = new Point(Integer.parseInt(posVecArr[0]), Integer.parseInt(posVecArr[1]));
        this.currentPosition = this.startPosition;
        this.currentState = new ArrayList<>();
        setSpawn = true;
    }

    private void loadNewBotState() {
        String[] gameArray = this.gameState.split(";");
        String stateString = gameArray[1].replaceAll("\\[", "").replaceAll("]", "");

        this.previousPosition = this.currentPosition;
        this.previousState = this.currentState;

        String posVec = gameArray[2].replaceAll("\\[", "").replaceAll("]", "");
        String[] posVecArr = posVec.split(",");
        this.currentPosition = new Point(Integer.parseInt(posVecArr[0]), Integer.parseInt(posVecArr[1]));

        String[] stateArr = stateString.split(",");
        List<String> currentStates = Arrays.asList(stateArr);
        this.currentState = FieldState.State.parseStates(currentStates);
        if (this.botState == BotState.EXPLORE) addFieldState(currentPosition, currentState);

        String recState = gameArray[3].replaceAll("\\[", "").replaceAll("]", "");
        if (!recState.equals("")) {
            String[] recStateArr = recState.split("-");
            String[] recStateArrX = recState.split("#");

            for (int y = 0; y < width; y++) {
                for (int x = 0; x < height; x++) {
                    for (String s : recStateArrX[y].split(":")) {
                        this.field[x][y].state.add(FieldState.State.parseKnownStates(s));
                    }
                }
            }

            String[] recStateArrPos = recStateArr[1].split("\\+");
            this.positionToMove = new Point(Integer.parseInt(recStateArrPos[0]), Integer.parseInt(recStateArrPos[1]));
        }


        String screamVec = gameArray[4].replaceAll("\\[", "").replaceAll("]", "");
        String[] screamVecArr = screamVec.split(",");
        this.screamVec[0] = Double.parseDouble(screamVecArr[0]);
        this.screamVec[1] = Double.parseDouble(screamVecArr[1]);

        this.hasGold = gameArray[5].equals("true");
    }

    private void markPosition() {
        addFieldState(currentPosition, currentState);
        addWumpusState();
    }

    private String doNextAction() {
        return switch (botState) {
            case EXPLORE -> explore();
            case COOPERATE -> cooperate();
            case FLEE -> flee();
            case DISTRACT -> distract();
            case STEAL -> steal();
            case SUPPORT -> support();
            case ESCAPE -> escape();
            case COMM -> communicate();
            case WAIT -> waiting();
        };
    }

    private String waiting() {
        boolean infoReceived = false;
        if (infoReceived) {
            this.botState = BotState.SUPPORT;
            return Command.NOTHING;
        }
        return Command.NOTHING;
    }

    private String communicate() {
        int rand = (int) Math.round(Math.random());
        boolean screamReceived = !Arrays.equals(screamVec, new double[]{0, 0});

        if (lastScream && !screamReceived) {
            this.leader = true;
            this.botState = BotState.EXPLORE;
            return explore();
        }

        if (!lastScream && screamReceived) {
            this.botState = BotState.WAIT;
            return Command.NOTHING;
        }

        if (rand == 1) {
            this.lastScream = true;
            return Command.SCREAM;
        } else {
            this.lastScream = false;
            return Command.NOTHING;
        }

    }

    private String escape() {
        if (currentPosition.equals(startPosition)) {
            return Command.CLIMB;
        }
        return getNextMove(startPosition);
    }

    private String support() {
        if (!this.positionToMove.equals(currentPosition)) {
            return getNextMove(this.positionToMove);
        } else {
            this.botState = BotState.STEAL;
            return Command.SCREAM;
        }
    }

    private String distract() {
        if (!Arrays.equals(this.screamVec, new double[]{0, 0})) {
            getNextMove(this.goldPos);
            this.dangerMove = true;
        } else if (dangerMove) {
            this.botState = BotState.ESCAPE;
            return escape();
        }
        return Command.NOTHING;
    }

    private String steal() {
        if (!Arrays.equals(this.screamVec, new double[]{0, 0})) {
            getNextMove(this.goldPos);
        } else if (currentPosition.equals(goldPos)) {
            if (!this.hasGold) return Command.PICKUP;
            this.botState = BotState.ESCAPE;
            return escape();
        }
        return Command.NOTHING;
    }

    private String flee() {
        if (!this.botState.equals(BotState.FLEE)) {
            this.followWumpusPosition = previousPosition;
            this.botState = BotState.FLEE;
            ArrayList<FieldState.State> arr = new ArrayList<>();
            arr.add(FieldState.State.INIT_STENCH);
            addFieldState(currentPosition, arr);
            return oppositeMove();
        } else if (this.currentState.contains(FieldState.State.INIT_STENCH)) {
            removeFieldState(followWumpusPosition, FieldState.State.WUMPUS);
            this.followWumpusPosition = previousPosition;
            getFieldState(followWumpusPosition).remove(FieldState.State.WUMPUS);
            return getNextMove(startPosition);
        } else {
            removeStateFromAll(FieldState.State.WUMPUS);
            this.botState = BotState.EXPLORE;
            return Command.NOTHING;
        }
    }

    private String cooperate() {
        if (possibleStarts.size() >= 2) {
            Point waitingPos = this.possibleStarts.get(0);
            if (!currentPosition.equals(waitingPos)) {
                return getNextMove(waitingPos);
            } else {
                this.botState = BotState.DISTRACT;
                return Command.SCREAM;
            }
        } else {
            // go back to start or kys
        }
        return Command.NOTHING;
    }

    private String explore() {
        if (currentState.contains(FieldState.State.INIT_STENCH)) {
            return flee();
        }

        // check if there are reachable, unknown positions
        // delete all impossible fields
        removeStateFromAll(FieldState.State.IMPOSSIBLE);

        // set all impossible fields
        addImpossibleState();

        // if null then no more fields reachable
        // initialise the cooparate function
        Point nextUnknownReachableField = getNextUnknownField();
        if (nextUnknownReachableField == null) {
            this.botState = BotState.COOPERATE;
            return Command.SCREAM;
        }
        System.out.println(nextUnknownReachableField);

        if (currentState.contains(FieldState.State.WIND)) return oppositeMove();
        if (currentState.contains(FieldState.State.SAFE)) return getNextMove(nextUnknownReachableField);

        return Command.NOTHING;
    }

    private String oppositeMove() {
        return switch (previousCommand) {
            case Command.UP -> Command.DOWN;
            case Command.DOWN -> Command.UP;
            case Command.LEFT -> Command.RIGHT;
            case Command.RIGHT -> Command.LEFT;
            default -> throw new IllegalStateException("Unexpected value: " + previousCommand);
        };
    }

    private void addFieldState(Point pos, ArrayList<FieldState.State> states) {
        this.field[pos.x][pos.y].state.remove(FieldState.State.UNKNOWN);
        this.field[pos.x][pos.y].state.remove(FieldState.State.IMPOSSIBLE);
        this.field[pos.x][pos.y] = new FieldState(pos, states);
    }

    private void removeFieldState(Point pos, FieldState.State state) {
        this.field[pos.x][pos.y].state.remove(state);
    }

    private void addWumpusState() {
        ArrayList<Point> stenchPoints = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (this.field[x][y].state.contains(FieldState.State.INIT_STENCH)) {
                    stenchPoints.add(new Point(x, y));
                }
            }
        }
        if (stenchPoints.size() >= 2) {
            Point safePos;
            int xVal, yVal;
            if (stenchPoints.get(0).x < stenchPoints.get(1).x && stenchPoints.get(0).y < stenchPoints.get(1).y
                    || (stenchPoints.get(0).x > stenchPoints.get(1).x && stenchPoints.get(0).y > stenchPoints.get(1).y)) {
                xVal = Math.min(stenchPoints.get(0).x, stenchPoints.get(1).x);
                yVal = Math.min(stenchPoints.get(0).y, stenchPoints.get(1).y);
                if (!getFieldState(new Point(xVal, yVal + 1)).contains(FieldState.State.UNKNOWN)) {
                    safePos = new Point(xVal, yVal + 1);
                } else {
                    safePos = new Point(xVal + 1, yVal);
                }
            } else {
                xVal = Math.min(stenchPoints.get(0).x, stenchPoints.get(1).x);
                yVal = Math.max(stenchPoints.get(0).y, stenchPoints.get(1).y);
                if (!getFieldState(new Point(xVal, yVal - 1)).contains(FieldState.State.UNKNOWN)) {
                    safePos = new Point(xVal, yVal - 1);
                } else {
                    safePos = new Point(xVal + 1, yVal);
                }
            }

            System.out.println("SAFE POS: " + safePos);

            if (stenchPoints.get(0).x < safePos.x || stenchPoints.get(1).x < safePos.x) { // left
                if (stenchPoints.get(1).y < safePos.y || stenchPoints.get(0).y < safePos.y) { // above
                    this.goldPos = new Point(safePos.x - 1, safePos.y - 1);
                } else { // below
                    this.goldPos = new Point(safePos.x - 1, safePos.y + 1);
                }
            } else if (stenchPoints.get(0).x > safePos.x || stenchPoints.get(1).x > safePos.x) { // right
                if (stenchPoints.get(1).y < safePos.y || stenchPoints.get(0).y < safePos.y) { // above
                    this.goldPos = new Point(safePos.x + 1, safePos.y - 1);
                } else { // below
                    this.goldPos = new Point(safePos.x + 1, safePos.y + 1);
                }
            }
        }
    }

    private void getAllPossibleStarts() {
        Point initPos = this.goldPos;
        this.possibleStarts.clear();

        Point p1 = new Point(initPos.x, initPos.y - 2);
        Point p2 = new Point(initPos.x, initPos.y + 2);
        Point p3 = new Point(initPos.x + 2, initPos.y);
        Point p4 = new Point(initPos.x - 2, initPos.y);
        Point p5 = new Point(initPos.x - 1, initPos.y - 1);
        Point p6 = new Point(initPos.x + 1, initPos.y + 1);
        Point p7 = new Point(initPos.x - 1, initPos.y + 1);
        Point p8 = new Point(initPos.x + 1, initPos.y + 1);
        ArrayList<Point> possibleP = new ArrayList<>(List.of(new Point[]{p1, p2, p3, p4, p5, p6, p7, p8}));

        for (Point p : possibleP) {
            if (p.x >= 0 && p.x < width && p.y >= 0 && p.y < height && getFieldState(p).contains(FieldState.State.SAFE)) {
                this.possibleStarts.add(p);
            }
        }

    }

    private ArrayList<FieldState.State> getFieldState(Point pos) {
        return this.field[pos.x][pos.y].state;
    }

    private String getNextMove(Point nextPos) {
        Point position = this.currentPosition;
        int x1 = position.x;
        int y1 = position.y;
        int x2 = nextPos.x;
        int y2 = nextPos.y;

        PathFinding p = new PathFinding();
        PointP start = new PointP(x1, y1, null);
        PointP end = new PointP(x2, y2, null);
        int[][] map = getMap();
        List<PointP> path = p.findPath(map, start, end);

        if (this.botState == BotState.EXPLORE || this.botState == BotState.FLEE) {
            if (path == null) {
                ArrayList<FieldState.State> arr = new ArrayList<>();
                arr.add(FieldState.State.UNREACHABLE);
                addFieldState(nextPos, arr);

                Point nextUnknownReachableField = getNextUnknownField();
                if (nextUnknownReachableField == null) {
                    this.botState = BotState.COOPERATE;
                    System.out.println(mapToString());
                    return Command.INFORM + mapToString();
                }
                return getNextMove(nextUnknownReachableField);
            }
        }

        removeStateFromAll(FieldState.State.UNREACHABLE);

        if (path.get(0).x - x1 > 0) return Command.RIGHT;
        else if (path.get(0).x - x1 < 0) return Command.LEFT;
        else if (path.get(0).y - y1 > 0) return Command.DOWN;
        else if (path.get(0).y - y1 < 0) return Command.UP;
        return Command.NOTHING;
    }

    private boolean stateCheck(int x, int y, FieldState.State state) {
        if (!(x >= 0 && x < width && y >= 0 && y < height)) return true;
        return this.field[x][y].state.contains(state);
    }

    private Point furthestDistance(ArrayList<Point> points, Point p1) {
        Point fp = points.get(0);
        for (Point p : points) {
            if (Math.abs(p.x - p1.x) + Math.abs(p.y - p1.y) >= Math.abs(fp.x - p1.x) + Math.abs(fp.y - p1.y)) {
                fp = p;
            }
        }
        return fp;
    }

    public int[][] getMap() {
        int[][] f = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (stateCheck(x, y, FieldState.State.WIND)
                        || stateCheck(x, y, FieldState.State.INIT_STENCH)
                        || stateCheck(x, y, FieldState.State.WUMPUS)
                        || stateCheck(x, y, FieldState.State.UNREACHABLE)) {
                    f[y][x] = 1;
                } else if (stateCheck(x, y, FieldState.State.UNKNOWN)) {
                    f[y][x] = 0;
                } else {
                    f[y][x] = 0;
                }
            }
        }
        return f;
    }

    private String mapToString() {
        String s = "[";
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int c = 0;
                for (FieldState.State state : this.field[x][y].state) {
                    s = s + state.toString();
                    if ((this.field[x][y].state.size() - c) > 1) {
                        s = s + ",";
                    }
                }
                if (width - x <= 1) {
                    s = s + "#";
                } else {
                    s = s + ":";
                }
            }
        }
        Point posS = furthestDistance(this.possibleStarts, this.possibleStarts.get(this.possibleStarts.size() - 1));
        s = s + "-" + posS.x + "+" + posS.y + "]";
        return s;
    }

    private Point getNextUnknownField() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // System.out.println("X: " + x + " Y: " + y + " CONTAINS: " + this.field[x][y].state.toString());
                if (this.field[x][y].state.contains(FieldState.State.UNKNOWN)
                        && !this.field[x][y].state.contains(FieldState.State.IMPOSSIBLE)) return new Point(x, y);
            }
        }
        return null;
    }

    private boolean hasNeighbour(Point position, FieldState.State state) {
        if (position.x + 1 < width && this.field[position.x + 1][position.y].state.contains(state)) return true;
        if (position.x - 1 >= 0 && this.field[position.x - 1][position.y].state.contains(state)) return true;
        if (position.y + 1 < height && this.field[position.x][position.y + 1].state.contains(state)) return true;
        if (position.y - 1 >= 0 && this.field[position.x][position.y - 1].state.contains(state)) return true;
        return false;
    }

    private void removeStateFromAll(FieldState.State state) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.field[x][y].state.remove(state);
            }
        }
    }

    private void addImpossibleState() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.field[x][y].state.add(FieldState.State.IMPOSSIBLE);
                if (hasNeighbour(new Point(x, y), FieldState.State.SAFE)
                        || hasNeighbour(new Point(x, y), FieldState.State.INIT_STENCH)
                        || hasNeighbour(new Point(x, y), FieldState.State.WIND)) {
                    this.field[x][y].state.remove(FieldState.State.IMPOSSIBLE);
                }
            }
        }
    }

    private enum BotState {
        EXPLORE, FLEE, COOPERATE, SUPPORT, DISTRACT, STEAL, ESCAPE, COMM, WAIT;
    }

    private class FieldState {
        public ArrayList<State> state = new ArrayList<>();
        public Point position;

        public FieldState(Point position) {
            this.position = position;
            state.add(State.UNKNOWN);
        }

        public FieldState(Point position, ArrayList<State> states) {
            state.addAll(states);
            this.position = position;
        }

        private enum State {
            SAFE, UNKNOWN, IMPOSSIBLE, UNREACHABLE, INIT_STENCH, STENCH, WUMPUS, WIND, HOLE;

            public static ArrayList<State> parseStates(List<String> strings) {
                ArrayList<State> arrayList = new ArrayList<>();
                for (String s : strings) {
                    switch (s) {
                        case "WIND" -> arrayList.add(WIND);
                        case "STENCH" -> arrayList.add(INIT_STENCH);
                    }
                }
                if (arrayList.isEmpty()) arrayList.add(SAFE);
                return arrayList;
            }

            public static State parseKnownStates(String s) {
                return switch (s) {
                    case "SAFE" -> SAFE;
                    case "UNKNOWN" -> UNKNOWN;
                    case "IMPOSSIBLE" -> IMPOSSIBLE;
                    case "UNREACHABLE" -> UNREACHABLE;
                    case "INIT_STENCH" -> INIT_STENCH;
                    case "STENCH" -> STENCH;
                    case "WUMPUS" -> WUMPUS;
                    case "WIND" -> WIND;
                    case "HOLE" -> HOLE;
                    default -> throw new IllegalStateException("Unexpected value: " + s);
                };
            }
        }
    }


    public enum Command {
        ;
        public static final String UP = "B;UP;false;false;false;[]";
        public static final String DOWN = "B;DOWN;false;false;false;[]";
        public static final String LEFT = "B;LEFT;false;false;false;[]";
        public static final String RIGHT = "B;RIGHT;false;false;false;[]";
        public static final String NOTHING = "B;NOTHING;false;false;false;[]";
        public static final String SCREAM = "B;NOTHING;true;false;false;[]";
        public static final String PICKUP = "B;NOTHING;false;true;false;[]";
        public static final String CLIMB = "B;NOTHING;false;false;true;[]";
        public static final String INFORM = "B;NOTHING;false;false;false;";
    }

}
