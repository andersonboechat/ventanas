package br.com.atilo.jcondo.manager.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;

import br.com.abware.jcondo.core.PersonType;
import br.com.abware.jcondo.exception.BusinessException;
import br.com.atilo.jcondo.core.service.FlatServiceImpl;
import br.com.atilo.jcondo.core.service.MembershipServiceImpl;
import br.com.atilo.jcondo.core.service.PersonServiceImpl;

public class MembershipAddRequestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private PersonServiceImpl personService = new PersonServiceImpl();
	
	private FlatServiceImpl flatService = new FlatServiceImpl();
	
	private MembershipServiceImpl membershipService = new MembershipServiceImpl();
	
	@Override
	public void init(ServletConfig config) throws ServletException {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		PrintWriter pw = resp.getWriter(); 

		try {
			String datas[] = req.getParameter("data").split("p");
			Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			Date deadline = DateUtils.truncate(new Date(Long.parseLong(datas[0])), Calendar.DAY_OF_MONTH);

			if (today.after(deadline)) {
				throw new BusinessException("");
			}

			long personId = Long.parseLong(datas[1]);
			long flatId = Long.parseLong(datas[2]);

			membershipService.add(flatService.getFlat(flatId), personService.getPerson(personId), PersonType.VISITOR);
			pw.write("sucesso"); 

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			pw.write("falha"); 
		}
		pw.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		doGet(req, resp);
	}
}
