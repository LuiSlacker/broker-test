package de.sb.broker.model;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;

public class AuctionEntityTest extends EntityTest{

	@Test
	public void TestLifeCycle() {
		EntityManager em = this.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
		
		// grab a instance of Person
		Person person = em.getReference(Person.class, 1l);
		
		// construct new Auction
		Auction auction = new Auction(person);
		auction.setTitle("Meth");
		auction.setDescription("Good Shit");
		em.persist(auction);
		em.getTransaction().commit();
		
		this.getWasteBasket().add(auction.getIdentity());
		em.close();
	}
	
	@Test
	public void TestConstraints(){
		Validator validator = this.getEntityValidatorFactory().getValidator();
		Set<ConstraintViolation<Auction>> constrainViolations;
		
		//valid values
		Auction auction = new Auction(this.createValidPersonEntity());
		auction.setTitle("Title");
		auction.setDescription("BLA BLA");
		constrainViolations = validator.validate(auction);
		assertEquals(constrainViolations.size(), 0);
		
		auction.setTitle("");
		auction.setDescription("");
		constrainViolations = validator.validate(auction);
		assertEquals(constrainViolations.size(), 2);
		
	}

}
