package sampleApp.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import sampleApp.Issue;
import testDs.CustomDataSource;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = -2306626660188818275L;

	// @EJB
	// TestBean testBean;

	@Resource
	UserTransaction ut;

	@PersistenceUnit
	EntityManagerFactory emf;

	private void read() {
	}

	private void readWrite() {
	}

	private void render() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// testBean.test();

		if (true) {
			Map<String, String> props = new HashMap<>();
			props.put("", "");
			EntityManager em = emf.createEntityManager();

			// create an issue
			Issue issue = new Issue();
			issue.setDescription("a");
			try {
				ut.begin();
				em.joinTransaction();
				em.persist(issue);
				ut.commit();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			em.close();

			em = emf.createEntityManager();
			
			//CustomDataSource
			//		.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			try {
				ut.begin();

				EntityManager em2 = emf.createEntityManager();
				em2.joinTransaction();
				Issue issue2 = em2.find(Issue.class, issue.getId());
				issue2.setDescription("b");
				em2.flush();

				em.joinTransaction();

				issue = em.find(Issue.class, issue.getId());
				System.out.println("Description: " + issue.getDescription());
				// the description has to be b
				readWrite();
				render();
				ut.commit();
				em2.close();
			} catch (NotSupportedException | SystemException
					| SecurityException | IllegalStateException
					| RollbackException | HeuristicMixedException
					| HeuristicRollbackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// create result
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("html");
		PrintWriter out = resp.getWriter();
		// out.print("<html><head></head><body>Hello World"+testBean.load()+"</body></html>");
		out.print("<html><head></head><body>Hello World1</body></html>");
		out.close();
	}
}
