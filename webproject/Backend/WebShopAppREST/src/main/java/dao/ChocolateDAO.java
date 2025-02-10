package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.ChocolateCategoryEnum.Category;
import beans.StatusChocolateEnum.Status;
import beans.TypeOfChocolateEnum.Type;

public class ChocolateDAO {

    private HashMap<Integer, Chocolate> chocolates = new HashMap<Integer, Chocolate>();

    public ChocolateDAO() {
    }

    public ChocolateDAO(String contextPath) {
        loadProducts(contextPath);
    }

    public Collection<Chocolate> getAll() {
        return chocolates.values();
    }
    
    public Collection<Chocolate> getAllByFactoryId(int idFactory) {
        List<Chocolate> filteredChocolates = chocolates.values().stream()
            .filter(chocolate -> chocolate.getFactoryId() == idFactory)
            .collect(Collectors.toList());

        if (filteredChocolates.isEmpty()) {
            System.out.println("No chocolates found for factory ID: " + idFactory);
        } else {
            filteredChocolates.forEach(chocolate -> System.out.println("Matching chocolate: " + chocolate));
        }

        return filteredChocolates;
    }

    public Chocolate deleteChocolate(int id, String contextPath) {
        Chocolate removedChocolate = chocolates.remove(id);
        if (removedChocolate != null) {
            saveAllProducts(contextPath);
        }
        return removedChocolate;
    }
    
    public Chocolate getById(int id) {
        if (chocolates.containsKey(id)) {
            return chocolates.get(id);
        } else {
            return null;
        }
    }

    public Chocolate updateChocolate(int id, Chocolate newChocolate, String contextPath) {
        if (chocolates.containsKey(id)) {
            newChocolate.setId(id); 
            chocolates.put(id, newChocolate);
            saveAllProducts(contextPath);
            return newChocolate;
        }
        return null;
    }
    
    private void saveAllProducts(String contextPath) {
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/chocolates.txt");
            out = new BufferedWriter(new FileWriter(file, false));
            for (Chocolate chocolate : chocolates.values()) {
                out.write(chocolate.getId() + ";" + chocolate.getName() + ";" + chocolate.getPrice() + ";" +
                          chocolate.getFactoryId() + ";" + chocolate.getGrammage() + ";" + chocolate.getDescription() + ";"
                          + chocolate.getCategory().name() + ";" +
                          chocolate.getType().name() + ";" + chocolate.getStatus().name() + ";" + chocolate.getQuantity() + ";" + chocolate.getImage() + "\n");
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

    private void loadProducts(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/chocolates.txt");
            System.out.println(file.getCanonicalPath());
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() != 11) {
                    System.out.println("Invalid line format: " + line);
                    continue;
                }
                int id = Integer.parseInt(st.nextToken().trim());
                String name = st.nextToken().trim();
                double price = Double.parseDouble(st.nextToken().trim());
                int factoryId = Integer.parseInt(st.nextToken().trim());
                int grammage = Integer.parseInt(st.nextToken().trim());
                String description = st.nextToken().trim();
                Category category = Category.valueOf(st.nextToken().trim());
                Type type = Type.valueOf(st.nextToken().trim());
                Status status = Status.valueOf(st.nextToken().trim());
                int quantity = Integer.parseInt(st.nextToken().trim());
                String image = st.nextToken().trim();

                Chocolate chocolate = new Chocolate(id, name, price, factoryId, grammage, description,image, category, type, status, quantity);
                chocolates.put(id, chocolate);
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
    
    public Chocolate saveChocolate(String contextPath, Chocolate chocolate) {
        BufferedReader in = null;
        BufferedWriter out = null;
        
        if (!isValidChocolate(chocolate)) {
            throw new IllegalArgumentException("Svi elementi moraju biti popunjeni i ispravnog tipa.");
        }

        try {
            File file = new File(contextPath + "/chocolates.txt");
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

            chocolate.setId(maxId);

            out = new BufferedWriter(new FileWriter(file, true));
            out.write(chocolate.getId() + ";" + chocolate.getName() + ";" + chocolate.getPrice() + ";" +
                      chocolate.getFactoryId() + ";" + chocolate.getGrammage() + ";" + chocolate.getDescription() + ";"
                      + chocolate.getCategory().name() + ";" +
                      chocolate.getType().name() + ";" + chocolate.getStatus().name() + ";" + chocolate.getQuantity() + ";" + chocolate.getImage() + "\n");
            chocolates.put(chocolate.getId(), chocolate);
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
        return chocolate;
    }

    private boolean isValidChocolate(Chocolate chocolate) {
        if (chocolate.getName() == null || chocolate.getName().isEmpty()) {
            return false;
        }
        if (chocolate.getPrice() <= 0) {
            return false;
        }
        if (chocolate.getFactoryId() <= 0) {
            return false;
        }
        if (chocolate.getGrammage() <= 0) {
            return false;
        }
        if (chocolate.getDescription() == null || chocolate.getDescription().isEmpty()) {
            return false;
        }
        if (chocolate.getCategory() == null) {
            return false;
        }
        if (chocolate.getType() == null) {
            return false;
        }
        if (chocolate.getStatus() == null) {
            return false;
        }
        if (chocolate.getQuantity() < 0) {
            return false;
        }
        if (chocolate.getImage() == null) {
            return false;
        }
        return true;
    }
}
