package de.sb.broker.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.After;
import org.junit.Test;

public class BidEntityTest extends EntityTest{
	EntityManager em = this.getEntityManagerFactory().createEntityManager();

	@Test(expected = EntityNotFoundException.class)
	public void TestLifeCycle() {
		
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
		
		bid = em.getReference(Bid.class, bid.getIdentity());
		assertEquals(bid.getBidder(), person);
		
		em.getTransaction().begin();
		em.remove(bid);
		bid = em.getReference(Bid.class, bid.getIdentity());
		em.getTransaction().commit();
		
	}
	
	@After
	public void finializeTests(){
		if (em.isOpen()) em.close();
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
