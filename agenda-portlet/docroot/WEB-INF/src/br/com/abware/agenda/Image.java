package br.com.abware.agenda;

import java.net.URL;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class Image {

	private StreamedContent src;
	
	private String path;
	
	private String title;
	
	private String description;

	public Image(StreamedContent src, String title, String description, String path) {
		this.src = src;
		this.title = title;
		this.description = description;
		this.path = path;
	}
	
	public Image(String title, String description, String path) {
		this.title = title;
		this.description = description;
		this.path = path;
		try {
			this.src = new DefaultStreamedContent(new URL(path).openStream(), "image/jpeg");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent getSrc() {
		return src;
	}

	public void setSrc(StreamedContent src) {
		this.src = src;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
