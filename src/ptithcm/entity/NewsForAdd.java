package ptithcm.entity;

import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

public class NewsForAdd {
	private int id;
	private String title;
	private String description;
	private MultipartFile link;
	private String imageLink;
	private int cateID;
	private ArrayList<ParagraphForAdd> pars;
	
	public String getImageLink() {
		return imageLink;
	}
	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public MultipartFile getLink() {
		return link;
	}
	public void setLink(MultipartFile link) {
		this.link = link;
	}
	public ArrayList<ParagraphForAdd> getPars() {
		return pars;
	}
	public void setPars(ArrayList<ParagraphForAdd> pars) {
		this.pars = pars;
	}
	public int getCateID() {
		return cateID;
	}
	public void setCateID(int cateID) {
		this.cateID = cateID;
	} 
	
}
