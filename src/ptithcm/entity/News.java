package ptithcm.entity;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "News")
public class News {
	@Id
//	@GeneratedValue
	private int id;
	private String title;
	private String description;
	private String link;

	@ManyToOne
	@JoinColumn(name = "cate_id")
	private Category cate;

	@OneToMany(mappedBy = "news", fetch = FetchType.EAGER)
	private Collection<Paragraph> paragraphs;

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

	public Category getCate() {
		return cate;
	}

	public void setCate(Category cate) {
		this.cate = cate;
	}

	public Collection<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(Collection<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
