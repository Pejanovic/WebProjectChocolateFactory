package services;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.ChocolateFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Pattern;

@Path("/imageupload")
public class ImageUploadService {

    @Context
    ServletContext ctx;

    @Context
    HttpServletRequest request;

    public ImageUploadService() {
    }

    @PostConstruct
    public void init() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response uploadImage(ChocolateFactory factory) {
        String base64Image = factory.getLogo();

        System.out.println("Received base64Image: " + base64Image.substring(0, Math.min(base64Image.length(), 30)));

        String imageFormat = "jpg";
        if (factory.getName().contains(".jpeg")) {
            imageFormat = "jpeg";
        } else if (factory.getName().contains(".png")) {
            imageFormat = "png";
        } else if (factory.getName().contains(".gif")) {
            imageFormat = "gif";
        } else if (factory.getName().contains(".bmp")) {
            imageFormat = "bmp";
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String directoryPath = "C:\\Users\\Korisnik\\OneDrive\\Radna površina\\Web project Factory\\webproject\\Frontend\\front-app\\public";
        File file = new File(directoryPath, factory.getName() + "." + imageFormat);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving image").build();
        }

        String filePath = file.getAbsolutePath();
        return Response.ok(filePath).build();
    }
    
    @POST
    @Path("/chocolate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadChocolateImage(Chocolate chocolate) {
        String base64Image = chocolate.getImage();

        System.out.println("Received base64Image: " + base64Image.substring(0, Math.min(base64Image.length(), 30)));

        String imageFormat = "jpg";
        if (chocolate.getName().contains(".jpeg")) {
            imageFormat = "jpeg";
        } else if (chocolate.getName().contains(".png")) {
            imageFormat = "png";
        } else if (chocolate.getName().contains(".gif")) {
            imageFormat = "gif";
        } else if (chocolate.getName().contains(".bmp")) {
            imageFormat = "bmp";
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);

        String directoryPath = "C:\\Users\\Korisnik\\OneDrive\\Radna površina\\Web project Factory\\webproject\\Frontend\\front-app\\public";
        File file = new File(directoryPath, chocolate.getName() + "." + imageFormat);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error saving image").build();
        }

        String filePath = file.getAbsolutePath();
        return Response.ok(filePath).build();
    }
}
