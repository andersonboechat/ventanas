package br.com.atilo.jcondo.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;

import br.com.abware.jcondo.exception.BusinessException;


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

			BufferedImage bi = ImageIO.read(new FileInputStream(file));
			float originalWidth = bi.getWidth();
			float originalHeight = bi.getHeight();

			int newHeight = Math.round((originalHeight / originalWidth) * width);

			if (newHeight < height) {
				int newWidth = Math.round((originalWidth / originalHeight) * height);
				if (newWidth < width) {
					throw new BusinessException("img.too.small", originalWidth, originalHeight);
				} else {
					Thumbnails.of(file).height(height).toFile(file);
					image.setPath(toURL(file));
					image.setWidth(ImageIO.read(file).getWidth());
					image.setHeight(height);
				}
			} else {
				Thumbnails.of(file).width(width).toFile(file);
				image.setPath(toURL(file));
				image.setWidth(width);
				image.setHeight(ImageIO.read(file).getHeight());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
