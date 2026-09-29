package com.viper.client.util;

import com.viper.client.config.Config;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Waypoints {

    public static class Waypoint {
        public String name;
        public int x, y, z;
        public String dimension;

        public Waypoint(String name, int x, int y, int z, String dimension) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
    }

    public static boolean add(String name, int x, int y, int z, String dimension) {
        if (name == null || name.isEmpty()) return false;
        if (Config.waypoints.size() >= 10) return false;
        Config.waypoints.put(name.toLowerCase(), new Config.WaypointData(name, x, y, z, dimension));
        Config.save();
        return true;
    }

    public static boolean remove(String name) {
        if (name == null) return false;
        boolean removed = Config.waypoints.remove(name.toLowerCase()) != null;
        if (removed) Config.save();
        return removed;
    }

    public static Config.WaypointData get(String name) {
        if (name == null) return null;
        return Config.waypoints.get(name.toLowerCase());
    }

    public static List<Config.WaypointData> getAll() {
        return new ArrayList<>(Config.waypoints.values());
    }

    public static Config.WaypointData findClosest(int px, int py, int pz, String currentDim) {
        Config.WaypointData closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Config.WaypointData wp : Config.waypoints.values()) {
            if (wp.dimension != null && !wp.dimension.equals(currentDim)) continue;
            double dx = wp.x - px;
            double dy = wp.y - py;
            double dz = wp.z - pz;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < closestDist) {
                closestDist = dist;
                closest = wp;
            }
        }
        return closest;
    }

    public static String getDirection(int fromX, int fromZ, int toX, int toZ) {
        double dx = toX - fromX;
        double dz = toZ - fromZ;
        double angle = Math.toDegrees(Math.atan2(-dx, dz));
        if (angle < 0) angle += 360;
        if (angle >= 337.5 || angle < 22.5) return "N";
        if (angle >= 22.5 && angle < 67.5) return "NO";
        if (angle >= 67.5 && angle < 112.5) return "O";
        if (angle >= 112.5 && angle < 157.5) return "SO";
        if (angle >= 157.5 && angle < 202.5) return "S";
        if (angle >= 202.5 && angle < 247.5) return "SW";
        if (angle >= 247.5 && angle < 292.5) return "W";
        return "NW";
    }
}