package br.com.abware.accountmgm.bean;

import java.io.File;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CaptureEvent;
import org.primefaces.model.CroppedImage;

import br.com.abware.jcondo.core.model.Image;

@ManagedBean
@ViewScoped
public class CameraBean {

	private int width;

	private int height;

	private Image image;
	
	private CroppedImage croppedImage;	

	private File file;

	public CameraBean(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	public CameraBean() {
		image = new Image();
	}	

	public void onCapture(CaptureEvent event) {
		try {
			byte[] data = event.getData();
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			String imagePath = "/temp/";
			File file = File.createTempFile("cam_", ".png", new File(ec.getRealPath("") + imagePath));
			
            FileImageOutputStream imageOutput;
            imageOutput = new FileImageOutputStream(file);
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
			
			FileUtils.writeByteArrayToFile(file, event.getData());
			Thumbnails.of(file).height(height).toFile(file);
			imagePath = ec.getRequestScheme() + "://" + ec.getRequestServerName() + ec.getRequestContextPath() + imagePath + file.getName();
			image.setPath(imagePath);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onImageCropp() {
	    if(croppedImage == null) {
            return;
        }
        FileImageOutputStream imageOutput;
        try {
        	imageOutput = new FileImageOutputStream(file);
        	imageOutput.reset();
            imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
            imageOutput.close();
            //FileUtils.touch(file);
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

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
