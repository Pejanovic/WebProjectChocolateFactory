package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import beans.CustomerTypeEnum.CustomerType;
import beans.User;
import beans.UserTypeEnum.UserType;

public class UserDAO {

    private HashMap<Integer, User> users = new HashMap<Integer, User>();

    public UserDAO() {

    }

    public UserDAO(String contextPath) {
        loadUsers(contextPath);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public int getUserIdByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user.getId();
            }
        }
        return -1;
    }
    
    public Collection<User> getAllManagers() {
        return users.values().stream().filter(user -> user.getUserType() == UserType.MANAGER).collect(Collectors.toList());
    }

    public Collection<User> getAllFreeManagers() {
        return users.values().stream().filter(user -> user.getUserType() == UserType.MANAGER && user.getFactoryId() == 0).collect(Collectors.toList());
    }

    public User updateManager(int userId, int factoryId, String contextPath) {
        User user = users.get(userId);
        if (user != null) {
            user.setFactoryId(factoryId);
            saveAllUsers(contextPath);
            return user;
        } else {
            System.out.println("Manager with ID: " + userId + " not found.");
        }
        return null;
    }

    public User blockUser(String username, String contextPath) {
        User user = getUserByUsername(username);
        user.setBlocked(true);
        saveAllUsers(contextPath);
        return user;
    }

    public User updatePoints(String username, double priceShopp, String contextPath) {
        User user = getUserByUsername(username);
        user.setPoints(user.getPoints() + (priceShopp / 1000 * 133));
        if(user.getPoints() >= 3000) {
        	user.setCustomerType(CustomerType.SILVER);
        }else if(user.getPoints() >= 5000) {
        	user.setCustomerType(CustomerType.GOLD);
        }
        saveAllUsers(contextPath);
        return user;
    }

    public User updateUser(int id, String username, String password, String name, String lastName, LocalDate birthDay, String gender, String contextPath) {
        User user = users.get(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);
        user.setLastName(lastName);
        user.setBirthday(birthDay);
        user.setGender(gender);
        saveAllUsers(contextPath);
        return user;
    }

    public User updatePointsDelete(String username, double priceShopp, String contextPath) {
        User user = getUserByUsername(username);

        if (user != null) {
            double pointsToDeduct = priceShopp / 1000 * 133 * 4;

            if (user.getPoints() >= pointsToDeduct) {
                user.setPoints(user.getPoints() - pointsToDeduct);
            } else {
                user.setPoints(0.0);
            }
            
            if(user.getPoints() >= 3000 && user.getPoints() < 5000) {
            	user.setCustomerType(CustomerType.SILVER);
            }else if(user.getPoints() >= 5000) {
            	user.setCustomerType(CustomerType.GOLD);
            }

            saveAllUsers(contextPath);
        }

        return user;
    }
    
    public User deleteUser(String username, String contextPath) {
    	User user = getUserByUsername(username);
    	users.remove(user.getId());
    	return user;
    }
    
    public User updateSuspiciousStatus(String username, String contextPath) {
        User user = getUserByUsername(username);

        if (user != null) {
            user.setSuspicious(true);
            saveAllUsers(contextPath);
        }

        return user;
    }

    public User registerUser(String contextPath, User user) {

        if (users.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }

        int maxId = users.values().stream().mapToInt(User::getId).max().orElse(0);
        user.setId(maxId + 1);

        users.put(maxId, user);
        saveAllUsers(contextPath);
        return user;
    }

    public User getUserByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public Collection<User> getAllCustomers() {
        return users.values().stream().filter(user -> user.getUserType() == UserType.CUSTOMER).collect(Collectors.toList());
    }

    public Collection<User> promoteToManager(int id, String contextPath) {
        for (User user : users.values()) {
            if (user.getId() == id) {
                user.setUserType(UserType.MANAGER);
                saveAllUsers(contextPath);
                return users.values();
            }
        }
        return null;
    }

    public User getUserById(int id) {
        for (User user : users.values()) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
    
    public String getUsernameById(int userId) {
        User user = users.get(userId);
        return user != null ? user.getUsername() : null;
    }
     
    private void loadUsers(String contextPath) {
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/users.txt");
            in = new BufferedReader(new FileReader(file));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                StringTokenizer st = new StringTokenizer(line, ";");
                if (st.countTokens() != 13) {
                    System.out.println("Invalid line format: " + line);
                    continue;
                }
                int id = Integer.parseInt(st.nextToken().trim());
                String username = st.nextToken().trim();
                String password = st.nextToken().trim();
                String name = st.nextToken().trim();
                String lastName = st.nextToken().trim();
                LocalDate birthday = LocalDate.parse(st.nextToken().trim());
                UserType userType = UserType.valueOf(st.nextToken().trim());
                double points = Double.parseDouble(st.nextToken().trim());
                int factoryId = Integer.parseInt(st.nextToken().trim());
                CustomerType customerType = CustomerType.valueOf(st.nextToken().trim());
                String gender = st.nextToken().trim();
                boolean suspicious = Boolean.parseBoolean(st.nextToken().trim());
                boolean blocked = Boolean.parseBoolean(st.nextToken().trim());

                User user = new User(id, username, password, name, lastName, birthday, userType, points, factoryId, customerType, gender, suspicious, blocked);
                users.put(id, user);
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

    public UserType getUserTypeById(int id) {
        User user = users.get(id);
        return (user != null) ? user.getUserType() : null;
    }
    
    public void saveAllUsers(String contextPath) {
        BufferedWriter out = null;
        try {
            File file = new File(contextPath + "/users.txt");
            out = new BufferedWriter(new FileWriter(file, false));
            for (User user : users.values()) {
                out.write(user.getId() + ";" + user.getUsername() + ";" + user.getPassword() + ";" + user.getName() + ";" +
                          user.getLastName() + ";" + user.getBirthday() + ";" + user.getUserType() + ";" +
                          user.getPoints() + ";" + user.getFactoryId() + ";" + user.getCustomerType() + ";" +
                          user.getGender() + ";" + user.isSuspicious() + ";" + user.isBlocked() + "\n");
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
}
