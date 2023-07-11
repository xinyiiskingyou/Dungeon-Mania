package dungeonmania;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.util.Position;

public class DungeonGenerator {
    
    private boolean[][] mazeArray;
    private int width;
    private int height;

    public DungeonGenerator(boolean[][] mazeArray, int width, int height) {
        this.mazeArray = mazeArray;
        this.width = width;
        this.height = height;
    }

    public boolean[][] generateMaze() {
        
        // start is empty
        mazeArray[1][1] = true;
        int[][] distanceOf2 = {{0, 2}, {2, 0}, {0, -2}, {-2, 0}};
        Position start = new Position(1, 1);

        // add to options all neighbours of 'start' not on boundary that are of distance 2 away and are walls
        List<Position> options = getNeighbours(start, distanceOf2, false);
        Random random = new Random();  
        while (! options.isEmpty()) {
            Position next = options.remove(random.nextInt(options.size()));
            // let neighbours = each neighbour of distance 2 from next not on boundary that are empty
            List<Position> neighbours = getNeighbours(next, distanceOf2, true);
            if (! neighbours.isEmpty()) {
                Position neighbourPos = neighbours.get(random.nextInt(neighbours.size()));
                setPositionNextAndNeighbour(next, neighbourPos);
            }
            // add to options all neighbours of 'next' not on boundary that are of distance 2 away and are walls
            options.addAll(getNeighbours(next, distanceOf2, false));
        }

        // if maze[end] is a wall:
        if (! mazeArray[this.width - 1][this.height - 1]) {
            mazeArray[this.width - 1][this.height - 1] = true;
            Position end = new Position(this.width - 1, this.height - 1);
            int[][] distanceOf1 = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            // let neighbours = neighbours not on boundary of distance 1 from maze[end]
            List<Position> neighbours = getNeighbours(end, distanceOf1, false);
            // if there are no cells in neighbours that are empty
            if (! neighbours.isEmpty()) {
                Position neighbourPos = neighbours.get(random.nextInt(neighbours.size()));
                mazeArray[neighbourPos.getX()][neighbourPos.getY()] = true;
            }
        }
        return mazeArray;
    }

    public void setPositionNextAndNeighbour(Position next, Position neighbourPos) {
        // maze[ next ] = empty (i.e. true)
        mazeArray[next.getX()][next.getY()] = true;
        // maze[ position inbetween next and neighbour ] = empty (i.e. true)
        int inBetweenX= (neighbourPos.getX() + next.getX()) / 2;
        int inBetweenY = (neighbourPos.getY() + next.getY()) / 2;
        mazeArray[inBetweenX][inBetweenY] = true;
        // maze[ neighbour ] = empty (i.e. true)
        mazeArray[neighbourPos.getX()][neighbourPos.getY()] = true;        
    }

    public List<Position> getNeighbours(Position position, int[][] directions, boolean isEmpty) {
        List<Position> options = new ArrayList<>();
        for (int[] direction: directions) {
            int newX = position.getX() + direction[0];
            int newY = position.getY() + direction[1];
            // check not on boundary
            if (checkNotOnBoundary(newX, newY) && mazeArray[newX][newY] == isEmpty) {
                options.add(new Position(newX, newY));
            }
        }
        return options;
    }
    
    public boolean checkNotOnBoundary(int newX, int newY) {
        return newX >= 0 && newX < this.width && newY >= 0 && newY < this.height;
    }
}
