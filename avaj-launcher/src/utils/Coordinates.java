package src.utils;

import java.util.Objects;

public class Coordinates {
    private int longitude;
    private int latitude;
    private int height;

    Coordinates(int p_longitude, int p_latitude, int p_height) {
        this.longitude = p_longitude;
        this.latitude = p_latitude;
        this.height = p_height;
    }

    public int getLongitude() {
        return this.longitude;
    }
    public int getLatitude() {
        return this.latitude;
    }
    public int getHeight() {
        return this.height;
    }

    public void increaseLongitude(int amount) {
        this.longitude += amount;
    }
    public void decreaseLongitude(int amount) {
        this.longitude -= amount;
    }
    public void increaseLatitude(int amount) {
        this.latitude += amount;
    }
    public void decreaseLatitude(int amount) {
        this.latitude -= amount;
    }
    public void increaseHeight(int amount) {
        this.height += amount;
        if (this.height > 100) {
            this.height = 100;
        }
    }
    public void decreaseHeight(int amount) {
        this.height -= amount;
        if (this.height < 0) {
            this.height = 0;
        }
    }

    @Override
    public String toString() {
        return ("Longitude: " + this.longitude +
                ",\tLatitude: " + this.latitude +
                ",\tHeight: " + this.height);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude, height);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Coordinates objectCoordinate = (Coordinates) object;
        return longitude == objectCoordinate.longitude &&
            latitude == objectCoordinate.latitude
            && height == objectCoordinate.height;
    }
}