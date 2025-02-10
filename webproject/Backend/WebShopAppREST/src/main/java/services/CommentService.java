package services;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.Response;

import beans.Comment;
import beans.CommentStatusEnum;
import dao.CommentDAO;

@Path("/comments")
public class CommentService {
    
    @Context
    ServletContext ctx;
    
    public CommentService() {
    }

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("commentDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("commentDAO", new CommentDAO(contextPath));
        }
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getComments() {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.getAll();
    }
    
    @GET
    @Path("/FactorySearch")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getCommentsByFactoryId(@QueryParam("id") int idFactory, @QueryParam("role") String role) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        Collection<Comment> allComments = dao.getAllByFactoryId(idFactory);

        if ("customer".equalsIgnoreCase(role)) {
            return allComments.stream()
                    .filter(comment -> comment.getStatus() == CommentStatusEnum.APPROVED)
                    .collect(Collectors.toList());
        } else if ("manager".equalsIgnoreCase(role) || "administrator".equalsIgnoreCase(role)) {
            return allComments;
        } else {
            return Collections.emptyList();
        }
    }

    
    @GET
    @Path("/CustomerSearch")
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Comment> getCommentsByCustomerId(@QueryParam("id") int customerId) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.getAllByCustomerId(customerId);
    }
    
    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment postComment(Comment comment) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.saveComment(ctx.getRealPath(""), comment);
    }
    

    
    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment deleteComment(@PathParam("id") int id) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.deleteComment(id, ctx.getRealPath(""));
    }
    
    @PUT
    @Path("/approve/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment approveComment(@PathParam("id") int id) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.approveComment(id, ctx.getRealPath(""));
    }

    @PUT
    @Path("/reject/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Comment rejectComment(@PathParam("id") int id) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.rejectComment(id, ctx.getRealPath(""));
    }
    
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Comment updateComment(@PathParam("id") int id, Comment comment) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.updateComment(id, comment, ctx.getRealPath(""));
    }
}
