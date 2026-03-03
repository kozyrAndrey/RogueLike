package model.domain.service;

import model.domain.Coordinates;
import model.domain.FogState;

import java.util.*;

public class FogOfWarService {
    private final int visionRadius = 8; // Радиус обзора игрока
    private final Map<String, List<Coordinates>> rayCache = new HashMap<>();

    // Определяем, находится ли игрок в комнате
    private boolean isPlayerInRoom(Coordinates playerPos, int[][] map) {
        int cell = map[playerPos.y()][playerPos.x()];
        return cell == 1; // 1 = пол комнаты
    }

    // Получить границы комнаты, если игрок в ней
    private RoomBounds getRoomBounds(Coordinates playerPos, int[][] map) {
        int height = map.length;
        int width = map[0].length;

        // Если игрок не в комнате, возвращаем null
        if (!isPlayerInRoom(playerPos, map)) {
            return null;
        }

        // Ищем границы комнаты с помощью flood fill
        boolean[][] visited = new boolean[height][width];
        Queue<Coordinates> queue = new LinkedList<>();
        queue.add(playerPos);
        visited[playerPos.y()][playerPos.x()] = true;

        int minX = playerPos.x();
        int maxX = playerPos.x();
        int minY = playerPos.y();
        int maxY = playerPos.y();

        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        while (!queue.isEmpty()) {
            Coordinates current = queue.poll();
            int x = current.x();
            int y = current.y();

            // Обновляем границы
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            // Проверяем соседей
            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height
                        && !visited[ny][nx] && map[ny][nx] == 1) {
                    visited[ny][nx] = true;
                    queue.add(new Coordinates(ny, nx));
                }
            }
        }

        return new RoomBounds(minX, maxX, minY, maxY);
    }

    // Класс для хранения границ комнаты
    private static class RoomBounds {
        final int minX, maxX, minY, maxY;

        RoomBounds(int minX, int maxX, int minY, int maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
    }

    // Алгоритм Брезенхэма
    private List<Coordinates> bresenhamLine(int x0, int y0, int x1, int y1) {
        List<Coordinates> line = new ArrayList<>();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;

        int err = dx - dy;

        while (true) {
            line.add(new Coordinates(y0, x0));

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }

        return line;
    }

    private List<Coordinates> getBresenhamLineCached(int x0, int y0, int x1, int y1) {
        String key = x0 + "," + y0 + "," + x1 + "," + y1;
        return rayCache.computeIfAbsent(key, k -> bresenhamLine(x0, y0, x1, y1));
    }

    // Ray Casting с алгоритмом Брезенхэма
    private void bresenhamRayCasting(int centerX, int centerY, int height, int width,
                                     FogState[][] newVisibility, int[][] map) {
        for (int y = Math.max(0, centerY - visionRadius);
             y <= Math.min(height - 1, centerY + visionRadius); y++) {
            for (int x = Math.max(0, centerX - visionRadius);
                 x <= Math.min(width - 1, centerX + visionRadius); x++) {

                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance > visionRadius) continue;

                List<Coordinates> line = getBresenhamLineCached(centerX, centerY, x, y);
                boolean blocked = false;

                for (Coordinates point : line) {
                    int px = point.x();
                    int py = point.y();

                    if (px < 0 || px >= width || py < 0 || py >= height) {
                        blocked = true;
                        break;
                    }

                    if (px == x && py == y) {
                        if (!blocked) newVisibility[py][px] = FogState.VISIBLE;
                        break;
                    }

                    // Показываем текущую клетку
                    newVisibility[py][px] = FogState.VISIBLE;

                    int cell = map[py][px];

                    // Проверяем, не блокирует ли стена, закрытая дверь, пустота
                    if (cell == 3 || (cell >= 4 && cell <= 6) || cell== 0) {
                        blocked = true;
                        break;
                    }
                }
            }
        }
    }

    private void revealCorridor(int centerX, int centerY, int[][] map, FogState[][] fog) {
        int height = map.length;
        int width = map[0].length;

        // Основные направления коридоров
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];
            int x = centerX + dx;
            int y = centerY + dy;

            // Идем вдоль коридора в каждом направлении
            while (x >= 0 && x < width && y >= 0 && y < height) {
                int cell = map[y][x];

                // Прекращаем если это не коридор
                if (cell != 2) {
                    // Но показываем граничную клетку (дверь или стену)
                    if (cell == 3 || (cell >= 4 && cell <= 6)) {
                        fog[y][x] = FogState.VISIBLE;
                    }
                    break;
                }

                fog[y][x] = FogState.VISIBLE;

                x += dx;
                y += dy;
            }
        }
    }

    public FogState[][] calculateVisibility(Coordinates playerPos, int[][] map) {
        int height = map.length;
        int width = map[0].length;
        FogState[][] newFog = new FogState[height][width];

        // Инициализируем все как HIDDEN
        for (int y = 0; y < height; y++) {
            Arrays.fill(newFog[y], FogState.HIDDEN);
        }

        int centerX = playerPos.x();
        int centerY = playerPos.y();

        if (centerX < 0 || centerX >= width || centerY < 0 || centerY >= height) {
            return newFog; // Игрок вне карты
        }
        // Проверяем, находится ли игрок в комнате
        if (map[centerY][centerX] == 1) {
            // Игрок в комнате - показываем ВСЮ комнату
            RoomBounds bounds = getRoomBounds(playerPos, map);
            if (bounds != null) {
                for (int y = Math.max(0, bounds.minY - 1); y <= Math.min(height - 1, bounds.maxY + 1); y++) {
                    for (int x = Math.max(0, bounds.minX - 1); x <= Math.min(width - 1, bounds.maxX + 1); x++) {
                        // Пол комнаты и стены, и начало пола коридора
                        if (map[y][x] == 1 || map[y][x] == 3 || map[y][x] == 2) {
                            newFog[y][x] = FogState.VISIBLE;
                        }
                    }
                }
            }
        } else if (map[centerY][centerX] == 2) {
            // Игрок в коридоре - используем ray casting
            newFog[centerY][centerX] = FogState.VISIBLE;

            // 1. Ray Casting с алгоритмом Брезенхэма
            bresenhamRayCasting(centerX, centerY, height, width, newFog, map);

            // 2. Видимость вдоль коридоров (дополнительно)
            revealCorridor(centerX, centerY, map, newFog);
        }
        return newFog;
    }

    /**
     * Обновляем общий туман войны (сохраняем исследованные области)
     */
    public FogState[][] updateFog(FogState[][] oldFog, FogState[][] newVisibility) {
        int height = oldFog.length;
        int width = oldFog[0].length;
        FogState[][] updatedFog = new FogState[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                FogState oldState = oldFog[y][x];
                FogState newState = newVisibility[y][x];

                if (newState == FogState.VISIBLE) {
                    updatedFog[y][x] = FogState.VISIBLE;
                } else if (oldState == FogState.VISIBLE || oldState == FogState.EXPLORED) {
                    updatedFog[y][x] = FogState.EXPLORED;
                } else {
                    updatedFog[y][x] = FogState.HIDDEN;
                }
            }
        }

        return updatedFog;
    }
}