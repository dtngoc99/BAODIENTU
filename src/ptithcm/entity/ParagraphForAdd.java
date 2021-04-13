package ptithcm.entity;

import org.springframework.web.multipart.MultipartFile;

public class ParagraphForAdd {
	private int id;
	private String para_content;
	private String title;
	private String quote;
	private MultipartFile img;
	private String imgLink;
	

	public String getImgLink() {
		return imgLink;
	}
	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPara_content() {
		return para_content;
	}
	public void setPara_content(String para_content) {
		this.para_content = para_content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	public MultipartFile getImg() {
		return img;
	}
	public void setImg(MultipartFile img) {
		this.img = img;
	}
	
}
