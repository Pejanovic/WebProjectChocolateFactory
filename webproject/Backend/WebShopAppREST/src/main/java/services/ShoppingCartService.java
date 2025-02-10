package services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import beans.CartItemResponse;
import beans.Chocolate;
import beans.ShoppingCart;
import beans.User;
import dao.ChocolateDAO;

@Path("/cart")
public class ShoppingCartService {
    
    @Context
    ServletContext ctx;

    @Context
    HttpServletRequest request;
    
    public ShoppingCartService() {
    }
    
    @PostConstruct
    public void init() {
        HttpSession session = request.getSession(false);
        if (session.getAttribute("shoppingCart") == null) {
            User user = (User) session.getAttribute("loggedUser");
            if (user != null) {
                System.out.println("Initializing shopping cart for user: " + user.getUsername());
                session.setAttribute("shoppingCart", new ShoppingCart(user));
            } else {
                System.out.println("No user found in session during initialization");
            }
        } else {
            System.out.println("Shopping cart already initialized in session");
        }
    }

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllItems() {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No session found").build();
        }
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (cart == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No shopping cart found in session").build();
        }
        List<CartItemResponse> cartItems = new ArrayList<>();
        List<Chocolate> chocolates = cart.getChocolates();
        List<Integer> amounts = cart.getChocolateAmounts();
        if (chocolates.size() != amounts.size()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Mismatch in sizes of chocolates and amounts").build();
        }
        for (int i = 0; i < chocolates.size(); i++) {
            cartItems.add(new CartItemResponse(i + 1, chocolates.get(i), amounts.get(i)));
        }
        return Response.ok(cartItems).build();
    }
    
    @POST
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItem(@QueryParam("chocolateId") int chocolateId, @QueryParam("amount") int amount) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No session found").build();
        }
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (cart == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No shopping cart found in session").build();
        }
        ChocolateDAO dao = new ChocolateDAO(ctx.getRealPath(""));
        Chocolate chocolate = dao.getById(chocolateId);
        
        boolean found = false;
        for (int i = 0; i < cart.getChocolates().size(); i++) {
            Chocolate choco = cart.getChocolates().get(i);
            if(chocolate.getFactoryId() != choco.getFactoryId()) {
            	return Response.status(Response.Status.BAD_REQUEST).entity("You have started shopping at a different factory.").build();
            }
            
            if (choco.getId() == chocolateId) {
                cart.getChocolateAmounts().set(i, cart.getChocolateAmounts().get(i) + amount);
                found = true;
                break;
            }
        }
        
        if (!found) {
            cart.getChocolates().add(chocolate);
            cart.getChocolateAmounts().add(amount);
        }

        cart.setCijena(cart.getCijena() + (chocolate.getPrice() * amount));
        session.setAttribute("shoppingCart", cart);
        return getAllItems();
    }

    
    @DELETE
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeItem(@QueryParam("chocolateId") int chocolateId) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No session found").build();
        }
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (cart == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No shopping cart found in session").build();
        }
        List<Chocolate> chocolates = cart.getChocolates();
        List<Integer> amounts = cart.getChocolateAmounts();
        boolean found = false;
        for (int i = 0; i < chocolates.size(); i++) {
            if (chocolates.get(i).getId() == chocolateId) {
                double price = chocolates.get(i).getPrice();
                int amount = amounts.get(i);
                cart.setCijena(cart.getCijena() - (price * amount));
                chocolates.remove(i);
                cart.setChocolates(chocolates);
                amounts.remove(i);
                cart.setChocolateAmounts(amounts);
                session.setAttribute("shoppingCart", cart);
                found = true;
                break;
            }
        }
        if (!found) {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found in cart").build();
        }
        return getAllItems();
    }
    
    @PUT
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItem(@QueryParam("chocolateId") int chocolateId, @QueryParam("amount") int amount) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No session found").build();
        }
        ShoppingCart cart = (ShoppingCart) session.getAttribute("shoppingCart");
        if (cart == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No shopping cart found in session").build();
        }
        List<Chocolate> chocolates = cart.getChocolates();
        List<Integer> amounts = cart.getChocolateAmounts();
        boolean found = false;
        for (int i = 0; i < chocolates.size(); i++) {
            if (chocolates.get(i).getId() == chocolateId) {
                int currentAmount = amounts.get(i);
                double price = chocolates.get(i).getPrice();
                int difference = amount - currentAmount;
                cart.setCijena(cart.getCijena() + (price * difference));
                amounts.set(i, amount);
                cart.setChocolateAmounts(amounts);
                found = true;
                break;
            }
        }
        if (!found) {
            return Response.status(Response.Status.NOT_FOUND).entity("Chocolate not found in cart").build();
        }
        session.setAttribute("shoppingCart", cart);
        return getAllItems();
    }
}
