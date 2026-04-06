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

    public void changeLongitude(int amount) {
        if (amount > 0 && this.longitude > Integer.MAX_VALUE - amount) {
            this.longitude = Integer.MAX_VALUE;
            return;
        }
        if (amount < 0 && this.longitude < -amount) {
            this.longitude = 0;
            return;
        }
        this.longitude += amount;
        if (this.longitude < 0) {
            this.longitude = 0;
        }
    }

    public void changeLatitude(int amount) {
        if (amount > 0 && this.latitude > Integer.MAX_VALUE - amount) {
            this.latitude = Integer.MAX_VALUE;
            return;
        }
        if (amount < 0 && this.latitude < -amount) {
            this.latitude = 0;
            return;
        }
        this.latitude += amount;
        if (this.latitude < 0) {
            this.latitude = 0;
        }
    }

    public void changeHeight(int amount) {
        if (amount > 0 && this.height > 100 - amount) {
            this.height = 100;
            return;
        }
        if (amount < 0 && this.height < -amount) {
            this.height = 0;
            return;
        }
        this.height += amount;
        if (this.height > 100) {
            this.height = 100;
        } else if (this.height < 0) {
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