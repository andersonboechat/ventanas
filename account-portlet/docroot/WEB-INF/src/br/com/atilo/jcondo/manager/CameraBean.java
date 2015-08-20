package br.com.atilo.jcondo.manager;

import java.io.File;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.stream.FileImageOutputStream;

import net.coobird.thumbnailator.Thumbnails;

import org.primefaces.event.CaptureEvent;

@ManagedBean
@ViewScoped
public class CameraBean extends PortraitBean {

	private boolean render;

	public CameraBean() {
		super();
	}

	public CameraBean(int width, int height) {
		super(width, height);
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

	public boolean isRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

}
