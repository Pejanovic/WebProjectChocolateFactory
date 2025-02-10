package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Chocolate;
import dao.ChocolateDAO;

@Path("/chocolates")
public class ChocolateService {
    
    @Context
    ServletContext ctx;
    
    public ChocolateService() {
    }

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("chocolateDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("chocolateDAO", new ChocolateDAO(contextPath));
        }
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Chocolate> getChocolates() {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.getAll();
    }
    
    @GET
    @Path("/FactorySearch")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Chocolate> getChocolatesByFactoryId(@QueryParam("id") int idFactory) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.getAllByFactoryId(idFactory);
    }
    
    @GET
    @Path("/id")
    @Produces(MediaType.APPLICATION_JSON)
    public Chocolate getChocolatesById(@QueryParam("id") int id) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.getById(id);
    }
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Chocolate postChocolates(Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.saveChocolate(ctx.getRealPath(""), chocolate);
    }
    
    
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Chocolate deleteChocolate(@PathParam("id") int id) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.deleteChocolate(id, ctx.getRealPath(""));
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Chocolate updateChocolate(@PathParam("id") int id, Chocolate chocolate) {
    	ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
    	return dao.updateChocolate(id, chocolate, ctx.getRealPath(""));
    }
    
}
