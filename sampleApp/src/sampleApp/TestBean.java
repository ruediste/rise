package sampleApp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Session Bean implementation class TestBean
 */
@Stateless
@LocalBean
public class TestBean {

  
	@PersistenceContext
	EntityManager em;
	
	public void test(){
		try {
			em.unwrap(java.sql.Connection.class).setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Issue issue=new Issue();
		issue.setTitle("Hello "+System.currentTimeMillis());
		em.persist(issue);
	}

	public String load(){
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Issue> query = cb.createQuery(Issue.class);
		Root<Issue> from = query.from(Issue.class);
		query.select(from);
		StringBuilder sb=new StringBuilder();
		for (Issue issue: em.createQuery(query).getResultList()){
			sb.append(issue.getTitle()+"<br/>");
		}
		return sb.toString();
	}
}
