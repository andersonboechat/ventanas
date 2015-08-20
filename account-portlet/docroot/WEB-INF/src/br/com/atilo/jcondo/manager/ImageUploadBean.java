package br.com.atilo.jcondo.manager;

import java.io.File;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@ViewScoped
public class ImageUploadBean extends PortraitBean {

	public ImageUploadBean() {
		super();
	}

	public ImageUploadBean(int width, int height) {
		super(width, height);
	}

	public void onUpload(FileUploadEvent event) {
		try {
			File file = getTempFile();
			FileUtils.copyInputStreamToFile(event.getFile().getInputstream(), file);
			Thumbnails.of(file).height(height).toFile(file);
			image.setPath(toURL(file));
			image.setWidth(ImageIO.read(file).getWidth());
			image.setHeight(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
