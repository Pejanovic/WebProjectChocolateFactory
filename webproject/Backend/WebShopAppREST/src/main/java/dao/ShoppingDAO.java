package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.Chocolate;
import beans.Shopping;
import beans.ShoppingStatusEnum;
import beans.User;

public class ShoppingDAO {
    private HashMap<String, Shopping> shoppings = new HashMap<String, Shopping>();

    private ShoppingDAO() {
    }

    public ShoppingDAO(String contextPath) throws IOException {
        loadProducts(contextPath);
    }

    public Collection<Shopping> getAllShoppings() {
        return shoppings.values();
    }

    public Shopping updateShoppingStatus(String id, ShoppingStatusEnum.ShopStatusEnum status, String contextPath) {
        Shopping shopp = shoppings.get(id);
        if (shopp == null) {
            System.out.println("Shopping not found for ID: " + id);
            return null;
        }
        shopp.setStatus(status);
        saveAllShoppingsCorrect(contextPath);
        return shopp;
    }

    public Shopping updateShoppingStatusComment(String id, ShoppingStatusEnum.ShopStatusEnum status, String comment,
            String contextPath) {
        Shopping shopp = shoppings.get(id);
        if (shopp == null) {
            System.out.println("Shopping not found for ID: " + id);
            return null;
        }
        shopp.setStatus(status);
        shopp.setComment(comment);
        saveAllShoppingsCorrect(contextPath);
        return shopp;
    }

    public Collection<Shopping> getAllShoppingsByUsername(String username) {
        return shoppings.values().stream().filter(shopp -> shopp.getNameUser().equals(username))
                .collect(Collectors.toList());
    }

    public Collection<Shopping> getAllShoppingsByFactoryId(int id) {
        return shoppings.values().stream().filter(shopp -> shopp.getFactoryId() == id).collect(Collectors.toList());
    }

    public Shopping deleteShopping(String id, String username, String contextPath) {
        Shopping shopp = shoppings.get(id);
        if (shopp != null) {
            shopp.setCanceled(true);
            shopp.setCanceledDate(LocalDate.now());
            if(suspiciousShopping(username, LocalDate.now())) {
            	UserDAO dao = new UserDAO(contextPath);
            	dao.updateSuspiciousStatus(username, contextPath);
            }
            saveAllShoppingsCorrect(contextPath);
        }
        return shopp;
    }
    
    public boolean suspiciousShopping(String username, LocalDate today) {
        Collection<Shopping> shoppingsByUser = getAllShoppingsByUsername(username);
        LocalDate oneMonthAgo = today.minusMonths(1);
        int canceledShopping = 0;

        for (Shopping shopp : shoppingsByUser) {
            if (shopp.getCanceledDate() != null && shopp.getCanceledDate().isAfter(oneMonthAgo)) {
                canceledShopping += 1;
            }
        }

        return canceledShopping >= 5;
    }

    private void loadProducts(String contextPath) throws IOException {
        BufferedReader in = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            File file = new File(contextPath + "/shoppings.txt");
            System.out.println(file.getCanonicalPath());
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;

                String[] parts = line.split(";");
                if (parts.length < 10) {
                    System.out.println("Invalid line format: " + line);
                    continue;
                }

                String shoppingId = parts[0];
                int factoryId = Integer.parseInt(parts[1].trim());
                Date date = null;
                try {
                    date = dateFormat.parse(parts[2].trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                double price = Double.parseDouble(parts[3].trim());
                String nameUser = parts[4].trim();
                ShoppingStatusEnum.ShopStatusEnum status = ShoppingStatusEnum.ShopStatusEnum.valueOf(parts[5].trim());
                boolean canceled = Boolean.parseBoolean(parts[6].trim());
                LocalDate canceledDate = parts[7].trim().equals("null") ? null : LocalDate.parse(parts[7].trim());
                String comment = parts[8].trim().equals("null") ? " " : parts[8].trim();

                List<Chocolate> chocolates = new ArrayList<>();
                List<Integer> amounts = new ArrayList<>();

                boolean isAmountSection = false;
                for (int i = 9; i < parts.length; i++) {
                    String token = parts[i].trim();
                    if (token.equals("|")) {
                        isAmountSection = true;
                        continue;
                    }

                    if (isAmountSection) {
                        try {
                            amounts.add(Integer.parseInt(token));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid amount: " + token);
                        }
                    } else {
                        try {
                            int chocolateId = Integer.parseInt(token);
                            ChocolateDAO dao = new ChocolateDAO(contextPath);
                            Chocolate chocolate = dao.getById(chocolateId);
                            chocolates.add(chocolate);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid chocolate ID: " + token);
                        }
                    }
                }

                Shopping shopping = new Shopping(shoppingId, factoryId, date, price, nameUser, status, canceled, canceledDate, chocolates, amounts, comment);
                shoppings.put(shoppingId, shopping);
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

    public Shopping saveAllShoppings(String contextPath, Shopping shopping) {
        String newId = generateNewId();
        shopping.setIdShopping(newId);
        shoppings.put(newId, shopping);
        BufferedWriter out = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            File file = new File(contextPath + "/shoppings.txt");
            out = new BufferedWriter(new FileWriter(file, false));
            for (Shopping s : shoppings.values()) {
                out.write(s.getIdShopping() + ";" + s.getFactoryId() + ";" +
                          dateFormat.format(s.getDate()) + ";" + s.getPrice() + ";" +
                          s.getNameUser() + ";" + s.getStatus().name() + ";" +
                          s.isCanceled() + ";" + (s.getCanceledDate() == null ? "null" : s.getCanceledDate().toString()) + ";" +
                          (s.getComment() == null ? "null" : s.getComment()));
                
                StringBuilder chocolatesBuilder = new StringBuilder();
                for (Chocolate chocolate : s.getChocolates()) {
                    chocolatesBuilder.append(";" + chocolate.getId());
                }

                StringBuilder amountsBuilder = new StringBuilder();
                for (Integer amount : s.getAmounts()) {
                    amountsBuilder.append(";" + amount);
                }

                out.write(chocolatesBuilder.toString());
                out.write(";|");
                out.write(amountsBuilder.toString());
                out.write("\n");
            }
            System.out.println("Shoppings saved successfully.");
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
        return shopping;
    }

    public Collection<Shopping> saveAllShoppingsCorrect(String contextPath) {
        BufferedWriter out = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            File file = new File(contextPath + "/shoppings.txt");
            out = new BufferedWriter(new FileWriter(file, false));
            for (Shopping s : shoppings.values()) {
                out.write(s.getIdShopping() + ";" + s.getFactoryId() + ";" +
                          dateFormat.format(s.getDate()) + ";" + s.getPrice() + ";" +
                          s.getNameUser() + ";" + s.getStatus().name() + ";" +
                          s.isCanceled() + ";" + (s.getCanceledDate() == null ? "null" : s.getCanceledDate().toString()) + ";" +
                          (s.getComment() == null ? "null" : s.getComment()));
                
                StringBuilder chocolatesBuilder = new StringBuilder();
                for (Chocolate chocolate : s.getChocolates()) {
                    chocolatesBuilder.append(";" + chocolate.getId());
                }

                StringBuilder amountsBuilder = new StringBuilder();
                for (Integer amount : s.getAmounts()) {
                    amountsBuilder.append(";" + amount);
                }

                out.write(chocolatesBuilder.toString());
                out.write(";|");
                out.write(amountsBuilder.toString());
                out.write("\n");
            }
            System.out.println("Shoppings saved successfully.");
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
        return shoppings.values();
    }

    private String generateNewId() {
        int maxId = 0;
        for (String id : shoppings.keySet()) {
            try {
                int numericId = Integer.parseInt(id);
                if (numericId > maxId) {
                    maxId = numericId;
                }
            } catch (NumberFormatException e) {
            }
        }
        return String.valueOf(maxId + 1);
    }
}
