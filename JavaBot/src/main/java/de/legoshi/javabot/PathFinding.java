package main.java.de.legoshi.javabot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PathFinding {

    public boolean isWalkable(int[][] map, PointP point) {
        if (point.y < 0 || point.y > map.length - 1) return false;
        if (point.x < 0 || point.x > map[0].length - 1) return false;
        return map[point.y][point.x] == 0;
    }

    public List<PointP> findNeighbors(int[][] map, PointP point) {
        List<PointP> neighbors = new ArrayList<>();
        PointP up = point.offset(0,  1);
        PointP down = point.offset(0,  -1);
        PointP left = point.offset(-1, 0);
        PointP right = point.offset(1, 0);
        if (isWalkable(map, up)) neighbors.add(up);
        if (isWalkable(map, down)) neighbors.add(down);
        if (isWalkable(map, left)) neighbors.add(left);
        if (isWalkable(map, right)) neighbors.add(right);
        return neighbors;
    }

    public List<PointP> findPath(int[][] map, PointP start, PointP end) {
        boolean finished = false;
        List<PointP> used = new ArrayList<>();
        used.add(start);
        while (!finished) {
            List<PointP> newOpen = new ArrayList<>();
            for(int i = 0; i < used.size(); ++i){
                PointP point = used.get(i);
                for (PointP neighbor : findNeighbors(map, point)) {
                    if (!used.contains(neighbor) && !newOpen.contains(neighbor)) {
                        newOpen.add(neighbor);
                    }
                }
            }

            for(PointP point : newOpen) {
                used.add(point);
                if (end.equals(point)) {
                    finished = true;
                    break;
                }
            }

            if (!finished && newOpen.isEmpty())
                return null;
        }

        List<PointP> path = new ArrayList<>();
        PointP point = used.get(used.size() - 1);
        while(point.previous != null) {
            path.add(0, point);
            point = point.previous;
        }

        System.out.println("--------------NEXT STEP: " + path.get(0).toString());
        return path;
    }

    public static void main(String[] args) {
        int[][] map = {
                {0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0},
                {0,0,1,0,0,0,0},
                {0,1,0,0,0,0,0}
        };

        PointP start = new PointP(2, 4, null);
        PointP end = new PointP(2, 6, null);
        PathFinding finding = new PathFinding();
        List<PointP> path = finding.findPath(map, start, end);
        if (path != null) {
            for (PointP point : path) {
                System.out.println(point);
            }
        }
        else
            System.out.println("No path found");
    }

}

class PointP {
    public int x;
    public int y;
    public PointP previous;

    public PointP(int x, int y, PointP previous) {
        this.x = x;
        this.y = y;
        this.previous = previous;
    }

    @Override
    public String toString() { return String.format("(%d, %d)", x, y); }

    @Override
    public boolean equals(Object o) {
        PointP point = (PointP) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y); }

    public PointP offset(int ox, int oy) { return new PointP(x + ox, y + oy, this);  }
}
