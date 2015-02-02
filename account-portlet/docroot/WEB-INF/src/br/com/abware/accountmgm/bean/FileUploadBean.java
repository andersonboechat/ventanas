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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class FileUploadBean {

	private String imagePath;
	
	private CroppedImage croppedImage;
	
	private File image;

	public void onFileUpload(FileUploadEvent event) {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			imagePath = "/temp/";
			UploadedFile uploadedFile = event.getFile();
			image = File.createTempFile(FilenameUtils.getBaseName(uploadedFile.getFileName()), 
											"." + FilenameUtils.getExtension(uploadedFile.getFileName()),
											new File(ec.getRealPath("") + imagePath));
			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), image);
			Thumbnails.of(image).height(480).toFile(image);
			imagePath = ec.getRequestScheme() + "://" + ec.getRequestServerName() + ec.getRequestContextPath() + imagePath + image.getName();
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
        	imageOutput = new FileImageOutputStream(image);
            imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
            imageOutput.close();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cropping failed."));
        }
         
	}
	
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public CroppedImage getCroppedImage() {
		return croppedImage;
	}

	public void setCroppedImage(CroppedImage croppedImage) {
		this.croppedImage = croppedImage;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}
	
}
