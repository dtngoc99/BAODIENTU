package ptithcm.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ptithcm.entity.Category;
import ptithcm.entity.Image;
import ptithcm.entity.News;
import ptithcm.entity.NewsForAdd;
import ptithcm.entity.Paragraph;
import ptithcm.entity.ParagraphForAdd;

@Controller
@RequestMapping("")
@Transactional
public class AdminController {

	@Autowired
	SessionFactory factory;
	@Autowired
	ServletContext context;

	@RequestMapping("admin/index")
	public String admin_index() {
		return "admin/index";
	}

//	@RequestMapping("admin/insert/paragraph")
//	@ResponseBody
//	public String addParagraph(HttpServletRequest request) {
//		try {
//			request.getSession().setAttribute("count",
//					Integer.parseInt(request.getSession().getAttribute("count").toString()) + 1);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		// System.out.println(request.getSession().getAttribute("count").toString());
//		return "";
//	}

	public boolean validateN(ModelMap model, NewsForAdd news, BindingResult errors) {
		if (news.getTitle().trim().equals("")) {
			errors.rejectValue("title", "news", "Title is empty!");
		}
		if (news.getDescription().trim().equals("")) {
			errors.rejectValue("description", "news", "Description is empty!");
		}

		if (news.getLink() == null) {
			errors.rejectValue("link", "news", "Photo is empty");
		} else {
			String extention = news.getLink().getOriginalFilename()
					.substring(news.getLink().getOriginalFilename().lastIndexOf(".") + 1);
			if (!extention.equalsIgnoreCase("jpg")) {
				errors.rejectValue("link", "news", "Photo is not suitble");
			}
			if (news.getLink().getSize() > 20971520) {
				// System.out.println("File size: "+news.getLink().getSize());
				errors.rejectValue("link", "news", "Photo is big size too!");
			}
		}

		if (errors.hasErrors()) {
			model.addAttribute("message", "Do it!");
			return false;
		}
		return true;
	}

	public boolean validateP(ModelMap model, ParagraphForAdd para, BindingResult errors) {
		if (para.getPara_content().trim().equals("")) {
			errors.rejectValue("para_content", "para", "Content is empty!");
		}
		if (errors.hasErrors()) {
			model.addAttribute("message", "Do it!");
			return false;
		}
		return true;
	}

	public boolean validateC(ModelMap model, Category cate, BindingResult errors) {
		if (cate.getName().trim() == "") {
			errors.rejectValue("name", "cate", "Name is empty");
		}
		if (errors.hasErrors()) {
			model.addAttribute("message", "Do it!");
			return false;
		}
		return true;
	}

	@RequestMapping(value = "admin/insert-news")
	public String insert_news(ModelMap model, HttpServletRequest request,
			@RequestParam(value = "par_quantity") int par_quantity) {
		NewsForAdd news = new NewsForAdd();
		ArrayList<ParagraphForAdd> pars = new ArrayList<>();
		ParagraphForAdd par;
		for (int i = 0; i < par_quantity; i++) {
			par = new ParagraphForAdd();
			pars.add(par);
		}
		news.setPars(pars);
		model.addAttribute("news", news);
		return "admin/insert-news";
	}

	@RequestMapping(value = "admin/insert-news", params = "btnSave")
	public String insert_news(ModelMap model, @ModelAttribute("news") NewsForAdd news, BindingResult errors) {
		Session session = null;
		Transaction t = null;
		if (validateN(model, news, errors)) {
			try {
				session = factory.openSession();
				t = session.beginTransaction();
				String hql = "";
				Query query;

				hql = "SELECT MAX(id) FROM News";
				query = session.createQuery(hql);
				int NewsId = (int) query.uniqueResult();
				News neww = new News();
				neww.setId(NewsId + 1);
				neww.setTitle(news.getTitle());
				neww.setDescription(news.getDescription());
				Category cate = new Category();
				cate.setId(news.getCateID());
				neww.setCate(cate);
				String fname;
				String newFileName;
				String path;
				if (!news.getLink().isEmpty()) {
					fname = news.getLink().getOriginalFilename();
					newFileName = neww.getId() + "-" + 1 + fname.substring(fname.lastIndexOf("."));
					path = context.getRealPath("./images/web/" + newFileName);
					news.getLink().transferTo(new File(path));
					neww.setLink(newFileName);
				} else {
					neww.setLink("");
				}
				session.save(neww);
				// paragraph and image
				int i = 2;
				for (ParagraphForAdd p : news.getPars()) {
					hql = "SELECT MAX(id) FROM Paragraph";
					query = session.createQuery(hql);
					int ParaId = (int) query.uniqueResult();
					Paragraph para = new Paragraph();
					para.setId(ParaId + 1);
					if (p.getTitle().trim().length() == 0) {
						para.setTitle("");
					} else {
						para.setTitle(p.getTitle());
					}
					if (p.getPara_content().trim().length() == 0) {
						para.setPara_content("");
					} else {
						para.setPara_content(p.getPara_content());
					}
					if (p.getQuote().trim().length() == 0) {
						para.setQuote("");
					} else {
						para.setQuote(p.getQuote());
					}
					para.setNews(neww);

					session.save(para);
					if (!p.getImg().isEmpty()) {
						hql = "SELECT MAX(id) FROM Image";
						query = session.createQuery(hql);
						int ImageId = (int) query.uniqueResult();
						fname = p.getImg().getOriginalFilename();
						newFileName = neww.getId() + "-" + (i) + fname.substring(fname.lastIndexOf("."));
						path = context.getRealPath("./images/web/" + newFileName);
						p.getImg().transferTo(new File(path));
						Image im = new Image();
						im.setId(++ImageId);
						im.setLink(newFileName);
						im.setPara(para);
						session.save(im);
					}
					i++;
				}
				t.commit();
				model.addAttribute("message", "Success!");
				return "redirect:/admin/index.htm";
			} catch (Exception e) {
				t.rollback();
				model.addAttribute("message", "Failure!");
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		return "admin/insert-news";

	}

	@RequestMapping(value = "admin/insert-news", params = "btnReset")
	public String resetIN(ModelMap model, @ModelAttribute("news") News news) {
		news.setTitle(null);
		news.setDescription(null);
		news.setCate(null);
		return "admin/insert-news";
	}

	@RequestMapping(value = "admin/insert-news", params = "btnExit")
	public String exitIN() {
		return "redirect:/admin/index.htm";
	}

	@RequestMapping(value = "admin/editN/{id}")
	public String edit(ModelMap model, @PathVariable("id") int id) {
		Session session = null;
		try {
			session = factory.getCurrentSession();
			String hql = "FROM News WHERE id= " + id;
			Query query = session.createQuery(hql);
			List<News> list = query.list();
			NewsForAdd news = new NewsForAdd();
			news.setId(list.get(0).getId());
			news.setTitle(list.get(0).getTitle());
			news.setDescription(list.get(0).getDescription());
			news.setCateID(list.get(0).getCate().getId());
			if (list.get(0).getLink().trim().length() > 0) {
				String path = context.getRealPath("./images/web/" + list.get(0).getLink());
				File file = new File(path);
				FileInputStream input = new FileInputStream(file);
				MultipartFile image = new MockMultipartFile("file", file.getName(), "text/plain",
						org.apache.commons.io.IOUtils.toByteArray(input));
				news.setLink(image);
				news.setImageLink(list.get(0).getLink());
			} else {
				news.setImageLink("");
			}
			ArrayList<ParagraphForAdd> paras = new ArrayList<>();
			for (Paragraph p : list.get(0).getParagraphs()) {
				ParagraphForAdd para = new ParagraphForAdd();
				para.setId(p.getId());
				para.setTitle(p.getTitle());
				para.setQuote(p.getQuote());
				para.setPara_content(p.getPara_content());
				para.setImgLink("");
				for (Image i : p.getImages()) {
					String path = context.getRealPath("./images/web/" + i.getLink());
					File file = new File(path);
					FileInputStream input = new FileInputStream(file);
					MultipartFile image = new MockMultipartFile("file", file.getName(), "text/plain",
							org.apache.commons.io.IOUtils.toByteArray(input));
					para.setImg(image);
					para.setImgLink(i.getLink());
				}
				paras.add(para);
			}
			news.setPars(paras);
			model.addAttribute("news", news);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "admin/editN";
	}

	@RequestMapping(value = "admin/editN", params = "btnSave")
	public String edit(ModelMap model, @ModelAttribute("news") NewsForAdd news, BindingResult errors) {
		Session session = null;
		Transaction t = null;
		try {
			session = factory.openSession();
			t = session.beginTransaction();
			News neww = new News();
			neww.setId(news.getId());
			neww.setTitle(news.getTitle());
			neww.setDescription(news.getDescription());
			Category cate = new Category();
			cate.setId(news.getCateID());
			neww.setCate(cate);
			String fname;
			String newFileName;
			String path = "";
			if (!news.getLink().isEmpty()) {
				fname = news.getLink().getOriginalFilename();
				newFileName = neww.getId() + "-" + 1 + fname.substring(fname.lastIndexOf("."));
				path = context.getRealPath("./images/web/" + newFileName);
				news.getLink().transferTo(new File(path));
				neww.setLink(newFileName);
			} else {
				if (news.getImageLink().trim().length() > 0) {
					neww.setLink(news.getImageLink());
				} else {
					neww.setLink("");
				}
			}
			session.update(neww);
			// paragraph and image
			int i = 2;
			for (ParagraphForAdd p : news.getPars()) {
				Paragraph para = new Paragraph();
				para.setId(p.getId());
				para.setNews(neww);
				if (p.getTitle().trim().length() == 0) {
					para.setTitle("");
				} else {
					para.setTitle(p.getTitle());
				}
				if (p.getPara_content().trim().length() == 0) {
					para.setPara_content("");
				} else {
					para.setPara_content(p.getPara_content());
				}
				if (p.getQuote().trim().length() == 0) {
					para.setQuote("");
				} else {
					para.setQuote(p.getQuote());
				}
				session.update(para);
				if (!p.getImg().isEmpty()) {
					if (p.getImgLink().trim().length() > 0) {
						path = context.getRealPath("./images/web/" + p.getImgLink());

					} else if (p.getImgLink().trim().length() <= 0) {
						String hql = "SELECT MAX(id) FROM Image";
						Query query = session.createQuery(hql);
						int ImageId = (int) query.uniqueResult();
						Image im = new Image();
						im.setId(ImageId + 1);
						fname = p.getImg().getOriginalFilename();
						newFileName = neww.getId() + "-" + (i) + fname.substring(fname.lastIndexOf("."));
						im.setPara(para);
						im.setLink(newFileName);
						path = context.getRealPath("./images/web/" + im.getLink());

						session.save(im);
					}
					File file = new File(path);
					p.getImg().transferTo(new File(path));

				}
				i++;
			}

			t.commit();
			model.addAttribute("message", "Success!");
		} catch (Exception e) {
			model.addAttribute("message", "Failure!");
			t.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return "redirect:/admin/index.htm";
	}

	@RequestMapping(value = "admin/editN", params = "btnReset")
	public String edit(@ModelAttribute("n") News news) {
		return "redirect:/admin/editN/" + news.getId() + ".htm";
	}

	@RequestMapping(value = "admin/editN", params = "btnExit")
	public String edit(ModelMap model) {
		return "admin/index";
	}

	public Boolean deleteI(int id) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Query query = session.createQuery("FROM Image WHERE news_id = " + id);
			List<Image> list = query.list();
			for (Image image : list) {
				session.delete(image);
			}
			t.commit();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			t.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return false;
	}

	@RequestMapping(value = "admin/deleteN/{id}")
	public String deleteN(ModelMap model, @PathVariable("id") int id) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			News n = (News) session.get(News.class, id);
			for (Paragraph p : n.getParagraphs()) {
				try {
					for (Image i : p.getImages()) {
						session.delete(i);
					}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
				session.delete(p);
			}
			session.delete(n);
			t.commit();
			model.addAttribute("message", "Success!");
		} catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("message", "Failure!");
			t.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return "redirect:/admin/index.htm";
	}

	public void deleteN(int id) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {
			Query query = session.createQuery("FROM News WHERE cate_id = " + id);
			List<News> list = query.list();
			for (News news : list) {
				session.delete(news);
			}
			t.commit();
		} catch (Exception e) {
			// TODO: handle exception
			t.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@RequestMapping(value = "admin/insert-cate")
	public String insert(ModelMap model) {
		Category cate = new Category();
		model.addAttribute("cate", cate);
		return "admin/insert-cate";
	}

	@RequestMapping(value = "admin/insert-cate", params = "btnSave")
	public String insert(ModelMap model, @ModelAttribute("cate") Category cate, BindingResult errors) {
		Session session = null;
		Transaction t = null;
		if (validateC(model, cate, errors)) {
			try {
				session = factory.openSession();
				t = session.beginTransaction();
				session.save(cate);
				t.commit();
				model.addAttribute("message", "Success!");
				return "redirect:/admin/index.htm";
			} catch (Exception e) {
				model.addAttribute("message", "Failure!");
				t.rollback();
				e.printStackTrace();
			} finally {
				session.close();
			}
		}
		return "admin/insert-cate";

	}

	@RequestMapping(value = "admin/insert-cate", params = "btnReset")
	public String reset(ModelMap model, @ModelAttribute("cate") Category cate) {
		cate.setName(null);
		cate.setDesign_type(0);
		return "admin/insert-cate";
	}

	@RequestMapping(value = "admin/insert-cate", params = "btnExit")
	public String exit(ModelMap model) {
		return "admin/index";
	}

	@RequestMapping(value = "admin/editC/{id}")
	public String exitC(ModelMap model, @PathVariable("id") int id) {
		Session session = null;
		try {
			session = factory.openSession();
			Category cate = (Category) session.get(Category.class, id);
			model.addAttribute("cate", cate);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			session.close();
		}
		return "admin/editC";
	}

	@RequestMapping(value = "admin/editC", params = "btnSave")
	public String edit(ModelMap model, @ModelAttribute("cate") Category cate, BindingResult errors) {
		Session session = null;
		Transaction t = null;
		if (validateC(model, cate, errors)) {
			try {
				session = factory.openSession();
				t = session.beginTransaction();
				session.update(cate);
				t.commit();
				model.addAttribute("message", "Success!");
			} catch (Exception e) {
				model.addAttribute("message", "Failure!");
				e.printStackTrace();
				t.rollback();
			} finally {
				session.close();
			}
		}
		return "redirect:/admin/index.htm";
	}

	@RequestMapping(value = "admin/editC", params = "btnReset")
	public String resetC(ModelMap model, @ModelAttribute("cate") Category cate) {
		return "redirect:/admin/editC/" + cate.getId() + ".htm";
	}

	@RequestMapping(value = "admin/editC", params = "btnExit")
	public String exitC() {
		return "admin/index";
	}

	@RequestMapping(value = "admin/deleteC/{id}")
	@ResponseBody // nhận giá trị trả về
	public String deleteC(ModelMap model, @PathVariable("id") int id) {
		Session session = factory.openSession();
		Transaction t = session.beginTransaction();
		try {

			deleteN(id);
			Category c = new Category();
			c.setId(id);
			session.delete(c);
			t.commit();
			model.addAttribute("message", "Success!");
		} catch (Exception e) {
			// TODO: handle exception
			model.addAttribute("message", "Failure!");
			t.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return "redirect:/admin/index.htm";
	}

	@ModelAttribute("admin_news")
	public List<News> getAdNews() {
		Session session = factory.getCurrentSession();
		String hql = "FROM News";
		Query query = session.createQuery(hql);
		List<News> list = query.list();
		return list;
	}

	@ModelAttribute("cates")
	public List<News> getCase() {
		Session session = factory.getCurrentSession();
		String hql = "FROM Category";
		Query query = session.createQuery(hql);
		List<News> list = query.list();
		return list;
	}
}
