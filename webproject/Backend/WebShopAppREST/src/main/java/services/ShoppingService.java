package services;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Chocolate;
import beans.Shopping;
import dao.ChocolateDAO;
import dao.ShoppingDAO;

@Path("/shoppings")
public class ShoppingService {

	@Context
	ServletContext ctx;
	
	public ShoppingService() {
		
	}
	
	@PostConstruct
	public void init() throws IOException {
		if(ctx.getAttribute("ShoppingDAO") == null) {
			String contextPath = ctx.getRealPath("");
			ctx.setAttribute("ShoppingDAO", new ShoppingDAO(contextPath));
		}
	}
	
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Shopping> getAllShoppings() {
        ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
        return dao.getAllShoppings();
    }
    
    @GET
    @Path("/username")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Shopping> getAllShoppingsByUsername(@QueryParam("username") String username) {
    	ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
    	return dao.getAllShoppingsByUsername(username);
    }
    
    @GET
    @Path("/factory")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Shopping> getAllShoppingsByFactoryId(@QueryParam("factoryId") int factoryId) {
    	ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
    	return dao.getAllShoppingsByFactoryId(factoryId);
    }
    
    @DELETE
    @Path("/shopp")
    @Produces(MediaType.APPLICATION_JSON)
    public Shopping deleteById(@QueryParam("id") String id, @QueryParam("username") String username) {
    	ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
    	return dao.deleteShopping(id, username, ctx.getRealPath(""));
    }
    
    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Shopping createNewShopping(Shopping shopping) {
        ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
        return dao.saveAllShoppings(ctx.getRealPath(""), shopping);
    }
    
    @PUT
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Shopping updateShoppingStatus(Shopping shopping) {
        ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
        String contextPath = ctx.getRealPath("");
        Shopping updatedShopping = dao.updateShoppingStatus(shopping.getIdShopping(), shopping.getStatus(), contextPath);
        
        if (updatedShopping == null) {
            return null;
        }
        
        return updatedShopping;
    }
    
    @PUT
    @Path("/status-comment")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Shopping updateShoppingStatusComment(Shopping shopping) {
        ShoppingDAO dao = (ShoppingDAO) ctx.getAttribute("ShoppingDAO");
        String contextPath = ctx.getRealPath("");
        Shopping updatedShopping = dao.updateShoppingStatusComment(shopping.getIdShopping(), shopping.getStatus(), shopping.getComment(), contextPath);
        
        if (updatedShopping == null) {
            return null;
        }
        
        return updatedShopping;
    }
}
