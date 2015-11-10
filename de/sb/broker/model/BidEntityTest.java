package de.sb.broker.model;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;

public class BidEntityTest extends EntityTest{

	@Test
	public void TestLifeCycle() {
		EntityManager em = this.getEntityManagerFactory().createEntityManager();
		
		//persist Person
		em.getTransaction().begin();
		Person person = this.createValidPersonEntity();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
//		//persist Auction
//		em.getTransaction().begin();
//		Auction auction = this.createValidPersonEntity();
//		em.persist(person);
//		em.getTransaction().commit();
//		this.getWasteBasket().add(person.getIdentity());
		
		em.getTransaction().begin();
		
		
		Bid bid = new Bid(new Auction(), this.createValidPersonEntity());
		
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
