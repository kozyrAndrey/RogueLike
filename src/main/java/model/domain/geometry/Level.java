package model.domain.geometry;

import model.domain.AccessLevel;
import model.domain.Coordinates;
import model.domain.LevelManager;
import model.domain.creature.enemy.*;
import model.domain.creature.enemy.factory.MasterEnemyFactory;
import model.domain.creature.player.Hero;
import model.domain.items.*;
import model.domain.items.factory.MasterItemFactory;
import model.domain.service.BalanceService;

import java.util.*;

public class Level {
    private final LevelManager levelManager;
    private final int[][] geomField = new int[LevelManager.MAP_HEIGHT][LevelManager.MAP_WIDTH];
    private ArrayList<Room> rooms;
    private final MasterItemFactory itemFactory;
    private final MasterEnemyFactory enemyFactory;

    public Level(int number, Hero player, int difficulty) {
        this.levelManager = new LevelManager(number, player, difficulty);
        this.itemFactory = new MasterItemFactory(new Random(), difficulty, number);
        this.enemyFactory = new MasterEnemyFactory(new Random(), difficulty, number);
        generateLevel(number, difficulty);
        levelManager.setField(geomField);
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void generateLevel(int level, int difficulty) {
        Random rng = new Random();
        int firstRoom = 0, lastRoom = 0;
        do {
            firstRoom = rng.nextInt(9);
            lastRoom = rng.nextInt(9);
        } while (firstRoom == lastRoom);
        // Создание списка комнат
        rooms = generateRooms(firstRoom, lastRoom);
        // Граф переходов между комнатами
        GridGraph paths = new GridGraph(3, 3);
        int[][] adjMatrix = paths.getAdjMatrix();
        // Создание списка коридоров на основе графа переходов
        List<Corridor> corridors = generateCorridors(adjMatrix, rooms, rng);
        // Генерация запертых дверей и ключей от них
        int[][] gatesAndKeys = generateClosedGates(firstRoom, rooms, corridors, rng);
        // Создание точки появления и точки выхода
        generateSpawnPoint(rng, rooms.get(firstRoom));
        generateExitPoint(rng, rooms.get(lastRoom));
        // Создание списка объектов на уровне
        generateKeys(gatesAndKeys, rng);
        generateItems(rng, firstRoom, level, difficulty);
        generateEnemies(rng, firstRoom, level, difficulty);

        // Заполнение матрицы поля созданными комнатами и коридорами
        fillMatrixRooms(rooms);
        for (Corridor corridor : corridors) {
            fillMatrixCoor(corridor.generatePathPoint(),
                    rooms.get(corridor.getFirst()).getClosed(),
                    rooms.get(corridor.getSecond()).getClosed());
        }
        // Отладочная информация
        System.out.printf("Start room = %d, Last room = %d\n", firstRoom, lastRoom);
        for (int i = 0; i < 3; i++) {
            System.out.println("GatesAndKeys[" + i + "] = " + gatesAndKeys[i][0] + ", " + gatesAndKeys[i][1]);
        }
    }

    public ArrayList<Room> generateRooms(int firstRoom, int lastRoom) {
        Random rng = new Random();
        ArrayList<Room> rooms = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Room tmpRoom;
            int width = rng.nextInt(18) + 5;
            int height = rng.nextInt(1) + 5;
            boolean first = (i == firstRoom);
            boolean last = (i == lastRoom);

            tmpRoom = new Room(rng.nextInt((LevelManager.MAP_WIDTH / 3) - width) + (((LevelManager.MAP_WIDTH / 3) + 1) * (i % 3)),
                    rng.nextInt((LevelManager.MAP_HEIGHT / 3) - height) + (((LevelManager.MAP_HEIGHT / 3) + 1) * (i / 3)),
                    width,
                    height, first, last);
            rooms.add(tmpRoom);
        }
        return rooms;
    }

    public List<Corridor> generateCorridors(int[][] adjMatrix, ArrayList<Room> rooms, Random rng) {
        ArrayList<Corridor> corridors = new ArrayList<>();
        boolean[] visited = new boolean[9];
        Stack<Integer> stack = new Stack<>();
        stack.push(rng.nextInt(9));
        visited[stack.peek()] = true;

        while (!stack.isEmpty()) {
            int current = stack.pop();
            ArrayList<Integer> neighbors = new ArrayList<>();
            for (int neighbor = 0; neighbor < 9; neighbor++) {
                if (adjMatrix[current][neighbor] == 1 && !visited[neighbor])
                    neighbors.add(neighbor);
            }
            Collections.shuffle(neighbors, rng);
            for (int neighbor : neighbors) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    stack.push(neighbor);
                    createCorridor(rooms, neighbor, current, rng, corridors);
                }
            }
        }
        int extraCorr = rng.nextInt(4);
        for (int i = 0; i < extraCorr; i++) {
            int room1 = rng.nextInt(9);
            int room2 = rng.nextInt(9);

            if (room1 != room2 && adjMatrix[room1][room2] == 1) {
                boolean corrExists = false;
                for (Corridor existing : corridors) {
                    if ((existing.getFirst() == room1 && existing.getSecond() == room2) ||
                            (existing.getFirst() == room2 && existing.getSecond() == room1)) {
                        corrExists = true;
                        break;
                    }
                }
                if (!corrExists) {
                    createCorridor(rooms, room1, room2, rng, corridors);
                }
            }
        }

        return corridors;
    }

    private void createCorridor(ArrayList<Room> rooms, int neighbor, int current, Random rng,
            ArrayList<Corridor> corridors) {
        if ((current / 3 == neighbor / 3) || (current % 3 == neighbor % 3)) {
            int x1 = rooms.get(current).getxStart();
            int y1 = rooms.get(current).getyStart();
            int x2 = rooms.get(neighbor).getxStart();
            int y2 = rooms.get(neighbor).getyStart();
            switch (current - neighbor) {
                case 3 -> {
                    x1 += 1 + rng.nextInt(rooms.get(current).getWidth() - 2);
                    x2 += 1 + rng.nextInt(rooms.get(neighbor).getWidth() - 2);
                    y2 += rooms.get(neighbor).getHeight() - 1;
                }
                case -1 -> {
                    x1 += rooms.get(current).getWidth() - 1;
                    y1 += 1 + rng.nextInt(rooms.get(current).getHeight() - 2);
                    y2 += 1 + rng.nextInt(rooms.get(neighbor).getHeight() - 2);
                }
                case -3 -> {
                    x1 += 1 + rng.nextInt(rooms.get(current).getWidth() - 2);
                    y1 += rooms.get(current).getHeight() - 1;
                    x2 += 1 + rng.nextInt(rooms.get(neighbor).getWidth() - 2);
                }
                case 1 -> {
                    y1 += 1 + rng.nextInt(rooms.get(current).getHeight() - 2);
                    x2 += rooms.get(neighbor).getWidth() - 1;
                    y2 += 1 + rng.nextInt(rooms.get(neighbor).getHeight() - 2);
                }
            }
            corridors.add(new Corridor(x1, y1, x2, y2, current, neighbor));
        }
    }

    public int[][] generateClosedGates(int first, List<Room> rooms, List<Corridor> corridors, Random rng) {
        int[][] gatesAndKeys = new int[3][2];
        ArrayList<ArrayList<Integer>> graph = buildGraph(rooms, corridors);
        // Узнаю расстояние до каждой комнаты
        int[] distances = calcDistances(graph, first);
        // Узнаю длиннейший возможный путь из стартовой комнаты
        int longest = 0;
        for (int distance : distances)
            if (distance > longest)
                longest = distance;
        // Узнаю количество комнат на каждой из длин путей
        int[] countRoomsSameDist = new int[longest + 1];
        for (int distance : distances) {
            countRoomsSameDist[distance]++;
        }
        int[] maxDoorRange = generateDoorsRanges(longest);
        for (int i = 0; i < 3; i++) {
            AccessLevel currentAccess;
            switch (i) {
                case 0 -> currentAccess = AccessLevel.BLUE;
                case 1 -> currentAccess = AccessLevel.YELLOW;
                case 2 -> currentAccess = AccessLevel.RED;
                default -> currentAccess = AccessLevel.NONE;
            }
            int currentMaxDoorRange = maxDoorRange[i];
            int countRoomsForKey = 0;
            for (int j = 0; j < currentMaxDoorRange; j++)
                countRoomsForKey += countRoomsSameDist[j];
            int countRoomsForDoors = countRoomsSameDist[currentMaxDoorRange];
            int rngNumber = rng.nextInt(countRoomsForKey);
            for (int j = 0; j < distances.length; j++) {
                if (distances[j] <= (currentMaxDoorRange - 1)) {
                    if (rngNumber > 0) {
                        rngNumber--;
                    } else {
                        gatesAndKeys[i][1] = j;
                        break;
                    }
                }
            }
            if (longest == 2 && i == 2)
                countRoomsForDoors--;
            rngNumber = rng.nextInt(countRoomsForDoors);
            for (int j = 0; j < distances.length; j++) {
                if (distances[j] == currentMaxDoorRange) {
                    if (rngNumber > 0 && rooms.get(j).getClosed() == AccessLevel.NONE) {
                        rngNumber--;
                    } else if (rngNumber == 0) {
                        rooms.get(j).setClosed(currentAccess);
                        gatesAndKeys[i][0] = j;
                        break;
                    }
                }
            }
        }
        return gatesAndKeys;
    }

    private ArrayList<ArrayList<Integer>> buildGraph(List<Room> rooms2, List<Corridor> corridors) {
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < rooms2.size(); i++) {
            graph.add(new ArrayList<>());
        }
        for (Corridor corridor : corridors) {
            int room1 = corridor.getFirst();
            int room2 = corridor.getSecond();
            graph.get(room1).add(room2);
            graph.get(room2).add(room1);
        }
        return graph;
    }

    private int[] calcDistances(ArrayList<ArrayList<Integer>> graph, int startRoom) {
        int[] distances = new int[graph.size()];
        Arrays.fill(distances, -1);
        distances[startRoom] = 0;

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(startRoom);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int neighbor : graph.get(current)) {
                if (distances[neighbor] == -1) {
                    distances[neighbor] = distances[current] + 1;
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }

    private int[] generateDoorsRanges(int longest) {
        int[] maxDoorRange = new int[3]; // 0 - Blue, 1 - Yellow, 2 - Red
        switch (longest) {
            case 2 -> {
                maxDoorRange[0] = 1;
                maxDoorRange[1] = 2;
                maxDoorRange[2] = 2;
            }
            case 3, 4 -> {
                maxDoorRange[0] = longest - 2;
                maxDoorRange[1] = longest - 1;
                maxDoorRange[2] = longest;
            }
            case 5 -> {
                maxDoorRange[0] = longest - 3;
                maxDoorRange[1] = longest - 1;
                maxDoorRange[2] = longest;
            }
            case 6, 7, 8 -> {
                maxDoorRange[0] = 2;
                maxDoorRange[1] = 4;
                maxDoorRange[2] = 6;
            }
        }
        return maxDoorRange;
    }

    private void generateSpawnPoint(Random rng, Room spawnRoom) {
        int yStart = spawnRoom.getyStart() + 1;
        int xStart = spawnRoom.getxStart() + 1;
        int height = spawnRoom.getHeight() - 2;
        int width = spawnRoom.getWidth() - 2;
        int y, x;
        do {
            y = rng.nextInt(height);
            x = rng.nextInt(width);
        } while (levelManager.isCellOccupied(new Coordinates((y + yStart), (x + xStart))));
        levelManager.setPlayerPosition(new Coordinates(y + yStart, x + xStart));
    }

    private void generateExitPoint(Random rng, Room exitRoom) {
        int yStart = exitRoom.getyStart() + 2;
        int xStart = exitRoom.getxStart() + 2;
        int height = exitRoom.getHeight() - 4;
        int width = exitRoom.getWidth() - 4;
        int y = rng.nextInt(height);
        int x = rng.nextInt(width);
        levelManager.addEntity(new Exit(new Coordinates(y + yStart, x + xStart)));
    }

    private void generateKeys(int[][] gatesAndKeys, Random rng) {
        for (int i = 0; i < 3; i++) {
            AccessLevel access = AccessLevel.NONE;
            String name = "";
            switch (i) {
                case 0 -> {
                    access = AccessLevel.BLUE;
                    name = "Blue Key";
                }
                case 1 -> {
                    access = AccessLevel.YELLOW;
                    name = "Yellow Key";
                }
                case 2 -> {
                    access = AccessLevel.RED;
                    name = "Red Key";
                }
            }
            Room currentRoom = rooms.get(gatesAndKeys[i][1]);
            int yStart = currentRoom.getyStart() + 1;
            int xStart = currentRoom.getxStart() + 1;
            int height = currentRoom.getHeight() - 2;
            int width = currentRoom.getWidth() - 2;
            int y, x;
            do {
                y = rng.nextInt(height);
                x = rng.nextInt(width);
            } while (levelManager.isCellOccupied(new Coordinates((y + yStart), (x + xStart))));
            Key key = new Key(new Coordinates(y + yStart, x + xStart), access, name);
            System.out.println("Add key");
            levelManager.addEntity(key);
        }
    }

    private void generateItems(Random rng, int first, int level, int difficulty) {
        for (int index = 0; index < rooms.size(); index++) {
            if (index == first)
                continue;
            Room room = rooms.get(index);
            int yStart = room.getyStart() + 1;
            int xStart = room.getxStart() + 1;
            int height = room.getHeight() - 2;
            int width = room.getWidth() - 2;
            int freeSpace = 0;
            int itemsInRoom = BalanceService.calculateNumberOfItems(level, difficulty);
            for (int i = yStart; i < yStart + height; i++) {
                for (int j = xStart; j < xStart + width; j++) {
                    if (!levelManager.isCellOccupied(new Coordinates(i, j)))
                        freeSpace++;
                }
            }
            for (int i = 0; i < itemsInRoom && freeSpace > 0; i++, freeSpace--) {
                int y, x;
                do {
                    y = rng.nextInt(height);
                    x = rng.nextInt(width);
                } while (levelManager.isCellOccupied(new Coordinates((y + yStart), (x + xStart))));
                Coordinates itemCoor = new Coordinates(y + yStart, x + xStart);
                Item item = itemFactory.createRandomItem(itemCoor);
                levelManager.addEntity(item);
            }
        }
    }

    private void generateEnemies(Random rng, int first, int level, int difficulty) {
        for (int index = 0; index < rooms.size(); index++) {
            if (index == first)
                continue;
            Room room = rooms.get(index);
            int yStart = room.getyStart() + 1;
            int xStart = room.getxStart() + 1;
            int height = room.getHeight() - 2;
            int width = room.getWidth() - 2;
            int freeSpace = 0;
            int enemiesInRoom = BalanceService.calculateNumberOfEnemies(level, difficulty);
            for (int i = yStart; i < yStart + height; i++) {
                for (int j = xStart; j < xStart + width; j++) {
                    if (!levelManager.isCellOccupied(new Coordinates(i, j)))
                        freeSpace++;
                }
            }
            for (int i = 0; i < enemiesInRoom && freeSpace > 3; i++, freeSpace--) {
                int y, x;
                do {
                    y = rng.nextInt(height);
                    x = rng.nextInt(width);
                } while (levelManager.isCellOccupied(new Coordinates((y + yStart), (x + xStart))));
                Coordinates enemyCoor = new Coordinates(y + yStart, x + xStart);
                Enemy enemy = enemyFactory.createRandomEnemy(enemyCoor);
                levelManager.addEntity(enemy);
            }
        }
    }

    private void fillMatrixCoor(int[][] pathPoints, AccessLevel first, AccessLevel second) {
        for (int i = 0; i < pathPoints.length - 1; i++) {
            int x1 = pathPoints[i][0];
            int y1 = pathPoints[i][1];
            int x2 = pathPoints[i + 1][0];
            int y2 = pathPoints[i + 1][1];
            if (i == 0) {
                geomField[y1][x1] = 2;
                if (first != AccessLevel.NONE)
                    levelManager.addEntity(new Door(new Coordinates(y1, x1), first));
            }
            if (Math.abs(x1 - x2) != 0) {
                for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                    for (int k = -1; k <= 1; k++) {
                        if ((y1 + k >= 0) && (y1 + k < LevelManager.MAP_HEIGHT) && k != 0 && geomField[y1 + k][x] == 0)
                            geomField[y1 + k][x] = 3;
                    }
                    if (geomField[y1][x] == 0 || geomField[y1][x] == 3)
                        geomField[y1][x] = 2;
                }
            } else if (Math.abs(y1 - y2) != 0) {
                for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                    for (int k = -1; k <= 1; k++) {
                        if ((x1 + k >= 0) && (x1 + k < LevelManager.MAP_WIDTH) && k != 0 && geomField[y][x1 + k] == 0)
                            geomField[y][x1 + k] = 3;
                    }
                    if (geomField[y][x1] == 0 || geomField[y][x1] == 3)
                        geomField[y][x1] = 2;
                }
            }
        }
        int y = pathPoints[pathPoints.length - 1][1];
        int x = pathPoints[pathPoints.length - 1][0];
        geomField[y][x] = 2;
        if (second != AccessLevel.NONE)
            levelManager.addEntity(new Door(new Coordinates(y, x), second));
    }

    public void fillMatrixRooms(List<Room> rooms) {
        for (int i = 0; i < 9; i++) {
            int x = rooms.get(i).getxStart();
            int y = rooms.get(i).getyStart();
            int width = rooms.get(i).getWidth();
            int height = rooms.get(i).getHeight();
            for (int z = y; z < y + height; z++) {
                for (int w = x; w < x + width; w++) {
                    if (z == y || z == y + height - 1 || w == x || w == x + width - 1) {
                        geomField[z][w] = 3;
                    } else if (geomField[z][w] == 0) {
                        geomField[z][w] = 1;
                    }
                }
            }
        }
    }
}
