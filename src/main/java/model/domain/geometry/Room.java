package model.domain.geometry;

import model.domain.AccessLevel;

public class Room {
    private final int xStart, yStart, width, height;
    private final boolean first, last;
    private AccessLevel closed = AccessLevel.NONE;

    public Room(int x, int y, int width, int height, boolean first, boolean last) {
        this.xStart = x;
        this.yStart = y;
        this.width = width;
        this.height = height;
        this.first = first;
        this.last = last;
    }

    public int getxStart() { return xStart; }

    public int getyStart() { return yStart; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public AccessLevel getClosed() { return closed; }

    public void setClosed(AccessLevel access) { this.closed = access; }
}