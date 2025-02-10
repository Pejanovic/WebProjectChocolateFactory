package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import beans.Location;

public class LocationDAO {

    private HashMap<Integer, Location> locations = new HashMap<Integer, Location>();

    public LocationDAO() {
    }

    public LocationDAO(String contextPath) {
        loadLocations(contextPath);
    }

    public Collection<Location> getAll() {
        return locations.values();
    }
    
    public Location getById(int id) {
        return locations.get(id);
    }

    public Location deleteLocation(int id, String contextPath) {
        Location removedLocation = locations.remove(id);
        if (removedLocation != null) {
            saveAllLocations(contextPath);
        }
        return removedLocation;
    }

    public Location updateLocation(int id, Location newLocation, String contextPath) {
        if (locations.containsKey(id)) {
            newLocation.setId(id); 
            locations.put(id, newLocation);
            saveAllLocations(contextPath);
            return newLocation;
        }
        return null;
    }
    
    private void saveAllLocations(String contextPath) {
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/locations.txt");
            out = new BufferedWriter(new FileWriter(file, false));
            for (Location location : locations.values()) {
                out.write(location.getId() + ";" + location.getLongitude() + ";" + location.getLatitude() + ";" +
                          location.getStreet() + ";" + location.getCity() + ";" + location.getPostalCode() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLocations(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/locations.txt");
            System.out.println(file.getCanonicalPath());
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() != 6) {
                    System.out.println("Invalid line format: " + line);
                    continue;
                }
                int id = Integer.parseInt(st.nextToken().trim());
                double longitude = Double.parseDouble(st.nextToken().trim());
                double latitude = Double.parseDouble(st.nextToken().trim());
                String street = st.nextToken().trim();
                String city = st.nextToken().trim();
                String postalCode = st.nextToken().trim();

                Location location = new Location(id, longitude, latitude, street, city, postalCode);
                locations.put(id, location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public Location saveLocation(String contextPath, Location location) {
        BufferedReader in = null;
        BufferedWriter out = null;
        
        if (!isValidLocation(location)) {
            throw new IllegalArgumentException("Svi elementi moraju biti popunjeni i ispravnog tipa.");
        }

        try {
            File file = new File(contextPath + "/locations.txt");
            in = new BufferedReader(new FileReader(file));
            
            int maxId = -1;
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                StringTokenizer st = new StringTokenizer(line, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                if (id > maxId) {
                    maxId = id;
                }
            }
            maxId++;

            location.setId(maxId);

            out = new BufferedWriter(new FileWriter(file, true));
            out.write(location.getId() + ";" + location.getLongitude() + ";" + location.getLatitude() + ";" +
                      location.getStreet() + ";" + location.getCity() + ";" + location.getPostalCode() + "\n");
            locations.put(location.getId(), location);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return location;
    }

    private boolean isValidLocation(Location location) {
        if (location.getStreet() == null || location.getStreet().isEmpty()) {
            return false;
        }
        if (location.getCity() == null || location.getCity().isEmpty()) {
            return false;
        }
        if (location.getPostalCode() == null || location.getPostalCode().isEmpty()) {
            return false;
        }
        if (location.getLongitude() == 0) {
            return false;
        }
        if (location.getLatitude() == 0) {
            return false;
        }
        return true;
    }
}
