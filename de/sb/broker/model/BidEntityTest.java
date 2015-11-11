package de.sb.broker.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;

public class BidEntityTest extends EntityTest{

	@Test
	public void TestLifeCycle() {
		EntityManager em = this.getEntityManagerFactory().createEntityManager();
		
		em.getTransaction().begin();
		//grab a instance of Person (Ines)
		Person person = em.find(Person.class, 1l);
		
		//grab a instance of Auction with seller Sascha
		TypedQuery<Auction> query = em.createQuery("SELECT a FROM Auction a", Auction.class);
		List<Auction> allAuctions = query.getResultList();
		
		// construct new Bid
		Bid bid = new Bid(allAuctions.get(4), person);
		
		em.persist(bid);
		em.getTransaction().commit();
		this.getWasteBasket().add(bid.getIdentity());
		em.close();
	}
	
	@Test
	public void TestConstraints(){
		Validator validator = this.getEntityValidatorFactory().getValidator();
		Set<ConstraintViolation<Bid>> constrainViolations;
		
		Bid bid = new Bid();
		constrainViolations = validator.validate(bid);
		assertEquals(constrainViolations.size(), 0);
	}

}
