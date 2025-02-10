package services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.CustomerTypeEnum.CustomerType;
import beans.ShoppingCart;
import beans.User;
import beans.UserTypeEnum.UserType;
import dao.UserDAO;

@Path("/user")
public class UserService {
    
    @Context
    ServletContext ctx;
    
    public UserService() {
    }

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("UserDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("UserDAO", new UserDAO(contextPath));
        }
    }

    @GET
    @Path("/id")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserIdByUsername(@QueryParam("username") String username) {
        try {
            UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
            int userId = dao.getUserIdByUsername(username);
            if (userId != -1) {
                return Response.ok(userId).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(@QueryParam("username") String username,
                                 @QueryParam("password") String password,
                                 @QueryParam("name") String name,
                                 @QueryParam("lastname") String lastname,
                                 @QueryParam("birthday") String birthday,
                                 @QueryParam("gender") String gender) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birthDay = LocalDate.parse(birthday, formatter);
            UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
            User newUser = dao.getUserByUsername(username);
            if(newUser != null) {
            	return Response.status(Response.Status.CONFLICT).entity("Username is already taken.").build();
            }
            User user = new User(username, password, name, lastname, birthDay, UserType.CUSTOMER, gender);
            user.setPoints(0);
            user.setFactoryId(0);
            user.setCustomerType(CustomerType.BRONZE);
            user.setBlocked(false);
            user.setSuspicious(false);
            dao.registerUser(ctx.getRealPath(""), user);
            return Response.ok(user).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format. Please use MM/dd/yyyy.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/register/employer")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEmployer(@QueryParam("username") String username,
                                 @QueryParam("password") String password,
                                 @QueryParam("name") String name,
                                 @QueryParam("lastname") String lastname,
                                 @QueryParam("birthday") String birthday,
                                 @QueryParam("gender") String gender) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birthDay = LocalDate.parse(birthday, formatter);
            UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
            User newUser = dao.getUserByUsername(username);
            if(newUser != null) {
            	return Response.status(Response.Status.CONFLICT).entity("Username is already taken.").build();
            }
            User user = new User(username, password, name, lastname, birthDay, UserType.WORKER, gender);
            user.setPoints(0);
            user.setFactoryId(0);
            user.setCustomerType(CustomerType.NOTHING);
            user.setBlocked(false);
            user.setSuspicious(false);
            dao.registerUser(ctx.getRealPath(""), user);
            return Response.ok(user).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format. Please use MM/dd/yyyy.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/register/manager")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerManager(@QueryParam("username") String username,
                                 @QueryParam("password") String password,
                                 @QueryParam("name") String name,
                                 @QueryParam("lastname") String lastname,
                                 @QueryParam("birthday") String birthday,
                                 @QueryParam("gender") String gender) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate birthDay = LocalDate.parse(birthday, formatter);
            UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
            User newUser = dao.getUserByUsername(username);
            if(newUser != null) {
            	return Response.status(Response.Status.CONFLICT).entity("Username is already taken.").build();
            }
            User user = new User(username, password, name, lastname, birthDay, UserType.MANAGER, gender);
            user.setPoints(0);
            user.setFactoryId(0);
            user.setCustomerType(CustomerType.BRONZE);
            user.setBlocked(false);
            user.setSuspicious(false);
            dao.registerUser(ctx.getRealPath(""), user);
            return Response.ok(user).build();
        } catch (DateTimeParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format. Please use MM/dd/yyyy.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("/updateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User updateUser(@QueryParam("id") int id,
                           @QueryParam("username") String username,
                           @QueryParam("password") String password,
                           @QueryParam("name") String name,
                           @QueryParam("lastname") String lastName,
                           @QueryParam("birthday") String birthday,
                           @QueryParam("gender") String gender) {
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate birthDay = LocalDate.parse(birthday, formatter);
        
        return dao.updateUser(id, username, password, name, lastName, birthDay, gender, ctx.getRealPath(""));
    }
    
    @PUT
    @Path("/updateBlock")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User blockUser(@QueryParam("username") String username) {
    	UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
    	return dao.blockUser(username, ctx.getRealPath(""));
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(@QueryParam("username") String username,
                              @QueryParam("password") String password,
                              @Context HttpServletRequest request) {
        try {
            UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
            User user = dao.getUserByUsername(username);
            if (user != null && user.getPassword().equals(password) && !user.isBlocked()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedUser", user);
                session.setAttribute("userType", user.getUserType());
                session.setAttribute("shoppingCart", new ShoppingCart(user));
                System.out.println("Sesija kreirana sa korisnikom: " + user.getUsername());
                JsonObject jsonResponse = Json.createObjectBuilder()
                    .add("user", Json.createObjectBuilder()
                        .add("username", user.getUsername())
                        .add("userType", user.getUserType().toString())
                        .build())
                    .add("sessionId", session.getId())
                    .build();
                return Response.ok(jsonResponse.toString()).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutUser(@Context HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getAllUsers(){
        UserDAO dao = new UserDAO(ctx.getRealPath(""));
        return dao.getAllUsers();
    }
    
    @GET
    @Path("/managers")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getAllManagers(){
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        return dao.getAllManagers();
    }
    
    @GET
    @Path("/free/managers")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getAllFreeManagers(){
        UserDAO dao = new UserDAO(ctx.getRealPath(""));
        return dao.getAllFreeManagers();
    }
    
    @GET
    @Path("/username")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserByUserName(@QueryParam("username") String username){
        UserDAO dao = new UserDAO(ctx.getRealPath(""));
        return dao.getUserByUsername(username);
    }
    
    @GET
    @Path("/customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> getAllCustomers(){
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        return dao.getAllCustomers();
    }
    
    @GET
    @Path("/userById/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("id") int userId){
    	UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
    	return dao.getUserById(userId);
    }
    
    @GET
    @Path("/userType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTypeById(@QueryParam("id") int id) {
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        UserType userType = dao.getUserTypeById(id);
        if (userType != null) {
            return Response.ok(userType).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }
    
    @PUT
    @Path("/promoteToManager")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<User> promoteToManager(@QueryParam("id") int id){
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        return dao.promoteToManager(id, ctx.getRealPath(""));
    }
    
    @PUT
    @Path("/updateUserPoints")
    @Produces(MediaType.APPLICATION_JSON)
    public User updateUserPoints(@QueryParam("username") String username, @QueryParam("price") double price){
        UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
        return dao.updatePoints(username, price, ctx.getRealPath(""));
    }
    
    @PUT
    @Path("/deductUserPoints")
    @Produces(MediaType.APPLICATION_JSON)
    public User deductUserPoints(@QueryParam("username") String username, @QueryParam("price") double price){
        UserDAO dao = new UserDAO(ctx.getRealPath(""));
        return dao.updatePointsDelete(username, price, ctx.getRealPath(""));
    }
    
    @DELETE
    @Path("/{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("username") String username) {
        try {
            UserDAO dao = (UserDAO) ctx.getAttribute("UserDAO");
            User deletedUser = dao.deleteUser(username, ctx.getRealPath(""));
            if (deletedUser == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
            }
            return Response.ok(deletedUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
   
}