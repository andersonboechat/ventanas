package br.com.abware.accountmgm.bean;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.CroppedImage;

import br.com.abware.jcondo.core.model.Image;

@ManagedBean
@ViewScoped
public class CameraBean {

	private static final String TEMP_DIR = "/temp/";

	private int width;

	private int height;

	private Image image;

	private CroppedImage croppedImage;	

	public CameraBean(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	public CameraBean() {
		image = new Image();
	}
	
	private File getTempFile() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return File.createTempFile("cam_", ".png", new File(ec.getRealPath("") + TEMP_DIR));
	}
	
	private String toURL(File file) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.getRequestScheme() + "://" + ec.getRequestServerName() + ec.getRequestContextPath() + TEMP_DIR + file.getName();
	}

	public void onCapture(CaptureEvent event) {
		try {
			byte[] data = event.getData();
			File file = getTempFile(); 

            FileImageOutputStream imageOutput;
            imageOutput = new FileImageOutputStream(file);
            imageOutput.write(data, 0, data.length);
            imageOutput.close();

			Thumbnails.of(file).height(height).toFile(file);
			image.setPath(toURL(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onCropp() {
	    if(croppedImage == null) {
            return;
        }
        FileImageOutputStream imageOutput;
        try {
        	String url = new URL(image.getPath()).getPath();
        	File file = getTempFile(); 
        	imageOutput = new FileImageOutputStream(file);
        	imageOutput.reset();
            imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
            imageOutput.close();
            image.setPath(toURL(file));
            
            FileUtils.deleteQuietly(new File(url));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cropping failed."));
        }
         
	}	

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public CroppedImage getCroppedImage() {
		return croppedImage;
	}

	public void setCroppedImage(CroppedImage croppedImage) {
		this.croppedImage = croppedImage;
	}

}
