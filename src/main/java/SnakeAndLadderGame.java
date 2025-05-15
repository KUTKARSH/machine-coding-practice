// SnakeAndLadderGame.java
import java.util.*;

class Snake {
    private final int head;
    private final int tail;

    public Snake(int head, int tail) {
        if (head <= tail) throw new IllegalArgumentException("Head must be above tail.");
        this.head = head;
        this.tail = tail;
    }

    public int getHead() { return head; }
    public int getTail() { return tail; }
}

class Ladder {
    private final int start;
    private final int end;

    public Ladder(int start, int end) {
        if (start >= end) throw new IllegalArgumentException("Start must be below end.");
        this.start = start;
        this.end = end;
    }

    public int getStart() { return start; }
    public int getEnd() { return end; }
}

class Player {
    private final String name;
    private int position = 0;

    public Player(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
}

class Dice {
    private final Random random = new Random();
    public int roll() {
        return random.nextInt(6) + 1;
    }
}

class Board {
    private final int size;
    private final Map<Integer, Integer> snakes = new HashMap<>();
    private final Map<Integer, Integer> ladders = new HashMap<>();

    public Board(int size, List<Snake> snakesList, List<Ladder> laddersList) {
        this.size = size;
        for (Snake s : snakesList) {
            snakes.put(s.getHead(), s.getTail());
        }
        for (Ladder l : laddersList) {
            ladders.put(l.getStart(), l.getEnd());
        }
    }

    public int getSize() { return size; }

    public int getNextPosition(int current) {
        if (snakes.containsKey(current)) {
            System.out.println("Bitten by snake at " + current);
            return snakes.get(current);
        }
        if (ladders.containsKey(current)) {
            System.out.println("Climbed ladder at " + current);
            return ladders.get(current);
        }
        return current;
    }
}

class Game {
    private final Board board;
    private final Queue<Player> players;
    private final Dice dice;

    public Game(Board board, List<Player> playerList) {
        this.board = board;
        this.players = new LinkedList<>(playerList);
        this.dice = new Dice();
    }

    public void play() {
        while (true) {
            Player current = players.poll();
            int roll = dice.roll();
            int nextPos = current.getPosition() + roll;
            if (nextPos > board.getSize()) {
                System.out.println(current.getName() + " rolled " + roll + ", but move exceeds board. Skipping.");
            } else {
                nextPos = board.getNextPosition(nextPos);
                current.setPosition(nextPos);
                System.out.println(current.getName() + " rolled " + roll + " and moved to " + nextPos);

                if (nextPos == board.getSize()) {
                    System.out.println(current.getName() + " wins!");
                    break;
                }
            }
            players.offer(current);
        }
    }
}

public class SnakeAndLadderGame {
    public static void main(String[] args) {
        List<Snake> snakes = Arrays.asList(
                new Snake(14, 7),
                new Snake(31, 26),
                new Snake(38, 20)
        );

        List<Ladder> ladders = Arrays.asList(
                new Ladder(3, 22),
                new Ladder(5, 8),
                new Ladder(11, 26)
        );

        Board board = new Board(50, snakes, ladders);
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));

        Game game = new Game(board, players);
        game.play();
    }
}
