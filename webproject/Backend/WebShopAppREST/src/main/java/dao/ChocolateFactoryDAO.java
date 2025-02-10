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
import java.util.stream.Collectors;

import beans.ChocolateFactory;
import beans.ChocolateFactoryStatusEnum.StatusType;

public class ChocolateFactoryDAO {

    private HashMap<Integer, ChocolateFactory> chocolateFactories = new HashMap<>();

    public ChocolateFactoryDAO() {}

    public ChocolateFactoryDAO(String contextPath) {
        loadFactories(contextPath);
    }

    public Collection<ChocolateFactory> getAll() {
        return chocolateFactories.values();
    }

    public ChocolateFactory getFactoryById(int id) {
        return chocolateFactories.get(id);
    }

    public Collection<ChocolateFactory> getAllByStatus(StatusType status) {
        return chocolateFactories.values().stream()
                .filter(factory -> factory.getStatus() == status)
                .collect(Collectors.toList());
    }

    public ChocolateFactory deleteFactory(int id, String contextPath) {
        ChocolateFactory removedFactory = chocolateFactories.remove(id);
        if (removedFactory != null) {
            saveAllFactories(contextPath);
        }
        return removedFactory;
    }

    public ChocolateFactory updateFactory(int id, ChocolateFactory newFactory, String contextPath) {
        if (chocolateFactories.containsKey(id)) {
            newFactory.setId(id);
            chocolateFactories.put(id, newFactory);
            saveAllFactories(contextPath);
            return newFactory;
        }
        return null;
    }

    private void saveAllFactories(String contextPath) {
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/chocolateFactories.txt");
            out = new BufferedWriter(new FileWriter(file, false));
            for (ChocolateFactory factory : chocolateFactories.values()) {
                out.write(factory.getId() + ";" + factory.getName() + ";" + factory.getWorkingHours() + ";" +
                        factory.getStatus().name() + ";" + factory.getLocationId() + ";" + factory.getRating() + ";" +
                        factory.isDeleted() + ";" + factory.getManagerId() + ";" + 
                        (factory.getLogo() != null ? factory.getLogo() : "null") + "\n");
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

    private void loadFactories(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/chocolateFactories.txt");
            System.out.println(file.getCanonicalPath());
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() != 9) {
                    System.out.println("Invalid line format: " + line);
                    continue;
                }
                int id = Integer.parseInt(st.nextToken().trim());
                String name = st.nextToken().trim();
                String workingHours = st.nextToken().trim();
                StatusType status = StatusType.valueOf(st.nextToken().trim());
                int locationId = Integer.parseInt(st.nextToken().trim());
                double rating = Double.parseDouble(st.nextToken().trim());
                boolean deleted = Boolean.parseBoolean(st.nextToken().trim());
                int managerId = Integer.parseInt(st.nextToken().trim());
                String logoPath = st.nextToken().trim();

                ChocolateFactory factory = new ChocolateFactory(id, name, null, workingHours, status, locationId, logoPath, rating, managerId);
                factory.setDeleted(deleted);
                chocolateFactories.put(id, factory);
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

    public ChocolateFactory saveFactory(String contextPath, ChocolateFactory factory) {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/chocolateFactories.txt");
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

            factory.setId(maxId);
            factory.setStatus(StatusType.OPEN);
            factory.setRating(0.0);
            factory.setDeleted(false);
            UserDAO dao = new UserDAO(contextPath);
            dao.updateManager(factory.getManagerId(), factory.getId(), contextPath);
            dao.saveAllUsers(contextPath);

            out = new BufferedWriter(new FileWriter(file, true));
            out.write(factory.getId() + ";" + factory.getName() + ";" + factory.getWorkingHours() + ";" +
                    factory.getStatus().name() + ";" + factory.getLocationId() + ";" + factory.getRating() + ";" +
                    factory.isDeleted() + ";" + factory.getManagerId() + ";" + 
                    (factory.getLogo() != null ? factory.getLogo() : "null") + "\n");
            chocolateFactories.put(factory.getId(), factory);
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
        return factory;
    }
}
