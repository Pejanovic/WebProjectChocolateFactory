package services;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

import beans.ChocolateFactory;
import dao.ChocolateFactoryDAO;

@Path("/chocolateFactories")
public class ChocolateFactoryService {

    @Context
    ServletContext ctx;
    
    @Context
    HttpServletRequest request;

    public ChocolateFactoryService() {
    }

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("chocolateFactoryDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("chocolateFactoryDAO", new ChocolateFactoryDAO(contextPath));
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ChocolateFactory> getChocolateFactories() {
        HttpSession session = request.getSession(false);
        if(session == null) {
            System.out.println("Sesija je null");
        } else {
            System.out.println("Sesija nije null");
        }
        ChocolateFactoryDAO dao = (ChocolateFactoryDAO) ctx.getAttribute("chocolateFactoryDAO");
        return dao.getAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public ChocolateFactory getChocolateFactoryById(@PathParam("id") int id) {
        ChocolateFactoryDAO dao = (ChocolateFactoryDAO) ctx.getAttribute("chocolateFactoryDAO");
        return dao.getFactoryById(id);
    }
/*
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<ChocolateFactory> getChocolateFactoriesByStatus(@QueryParam("status") String status) {
        ChocolateFactoryDAO dao = (ChocolateFactoryDAO) ctx.getAttribute("chocolateFactoryDAO");
        return dao.getAllByStatus(status.valueOf(status));
    }
*/
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChocolateFactory postChocolateFactory(ChocolateFactory factory) {
        ChocolateFactoryDAO dao = (ChocolateFactoryDAO) ctx.getAttribute("chocolateFactoryDAO");
        return dao.saveFactory(ctx.getRealPath(""), factory);
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChocolateFactory deleteChocolateFactory(@PathParam("id") int id) {
        ChocolateFactoryDAO dao = (ChocolateFactoryDAO) ctx.getAttribute("chocolateFactoryDAO");
        return dao.deleteFactory(id, ctx.getRealPath(""));
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ChocolateFactory updateChocolateFactory(@PathParam("id") int id, ChocolateFactory factory) {
        ChocolateFactoryDAO dao = (ChocolateFactoryDAO) ctx.getAttribute("chocolateFactoryDAO");
        return dao.updateFactory(id, factory, ctx.getRealPath(""));
    }
}
