package br.com.atilo.jcondo.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.io.FileUtils;
import org.primefaces.model.CroppedImage;

import br.com.abware.jcondo.core.model.Image;

public class PortraitBean {

	protected static final String TEMP_DIR = "/temp/";

	protected int width;

	protected int height;

	protected Image image;

	protected CroppedImage croppedImage;

	public PortraitBean() {
		image = new Image();
	}

	public PortraitBean(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}
	
	protected File getTempFile() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return File.createTempFile("pic_", ".png", new File(ec.getRealPath("") + TEMP_DIR));
	}
	
	protected String toURL(File file) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.getRequestScheme() + "://" + ec.getRequestServerName() + ec.getRequestContextPath() + TEMP_DIR + file.getName();
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
