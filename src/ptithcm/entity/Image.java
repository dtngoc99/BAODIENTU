package ptithcm.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Images")
public class Image {
	@Id
//	@GeneratedValue
	private int id;
	private String name;
	private String link;
	
	@ManyToOne
	@JoinColumn(name="para_id")
	private Paragraph para;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Paragraph getPara() {
		return para;
	}

	public void setPara(Paragraph para) {
		this.para = para;
	}

}
