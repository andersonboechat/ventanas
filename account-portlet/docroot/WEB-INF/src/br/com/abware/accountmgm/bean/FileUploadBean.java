package br.com.abware.accountmgm.bean;

import java.io.File;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageOutputStream;
import javax.portlet.PortletContext;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.CroppedImage;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class FileUploadBean {

	private OutputStream imageStream;
	
	private String imagePath;
	
	private CroppedImage croppedImage;

	public void onFileUpload(FileUploadEvent event) {
		try {
			PortletContext portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
			UploadedFile uploadedFile = event.getFile();
			File file = File.createTempFile(FilenameUtils.getBaseName(uploadedFile.getFileName()), 
											"." + FilenameUtils.getExtension(uploadedFile.getFileName()),
											new File(portletContext.getRealPath("") + "/temp"));
			FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), file);
			imagePath = "/temp/" + file.getName();

			Thumbnails.of(file).height(300).toFile(file);
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
			PortletContext portletContext = (PortletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        	imageOutput = new FileImageOutputStream(new File(portletContext.getRealPath("") + imagePath));
            imageOutput.write(croppedImage.getBytes(), 0, croppedImage.getBytes().length);
            imageOutput.close();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Cropping failed."));
        }
         
	}

	public OutputStream getImageStream() {
		return imageStream;
	}

	public void setImageStream(OutputStream imageStream) {
		this.imageStream = imageStream;
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
	
}
