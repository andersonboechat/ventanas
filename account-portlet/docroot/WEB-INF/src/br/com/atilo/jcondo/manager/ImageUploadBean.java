package br.com.atilo.jcondo.manager;

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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.UploadedFile;

import br.com.abware.jcondo.core.model.Image;

@ManagedBean
@ViewScoped
public class ImageUploadBean {

	private int width;
	
	private int height;
	
	private Image image;

	private CroppedImage croppedImage;

	private File file;

	public ImageUploadBean(int width, int height) {
		this();
		this.width = width;
		this.height = height;
	}

	public ImageUploadBean() {
		image = new Image();
	}
	
	public void onImageUpload(FileUploadEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			String imagePath = "/temp/";
			UploadedFile uploadedFile = event.getFile();
			file = File.createTempFile(FilenameUtils.getBaseName(uploadedFile.getFileName()), 
									   "." + FilenameUtils.getExtension(uploadedFile.getFileName()),
									   new File(ec.getRealPath("") + imagePath));
			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), file);
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
            imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
            imageOutput.close();
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

	public void setFile(File image) {
		this.file = image;
	}
	
}
