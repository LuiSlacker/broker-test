package de.sb.broker.model;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.After;
import org.junit.Test;

import junit.framework.Assert;


public class PersonEntityTest extends EntityTest{

	private final EntityManager em = this.getEntityManagerFactory().createEntityManager();
	
	@Test
	public void TestLifeCycle() {
		//persist person entity
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		em.getTransaction().begin();
		//test if Entity exists in DB
		person = em.getReference(Person.class, person.getIdentity());
		assertEquals(person.getName().getFamily(), "White");
		
		// test if Entity has been deleted properly
		em.remove(person);
		em.getTransaction().commit();

		em.getTransaction().begin();
		try {
			person = em.getReference(Person.class, person.getIdentity());
			Assert.fail();
		} catch(EntityNotFoundException e){
			//ok
		} finally {
		}
		em.getTransaction().commit();
	}

	@After
	public void finializeTests(){
		if (em.isOpen()) em.close();
	}
	
	@Test
	public void TestConstraints(){
		Validator validator = this.getEntityValidatorFactory().getValidator();
		Set<ConstraintViolation<Person>> constrainViolations;
		
		//valid values		
		Person person = this.createValidPersonEntity();
		constrainViolations = validator.validate(person);
		assertEquals(constrainViolations.size(), 0);
		
		// partly invalid values
		person.setAlias("WW5");
		person.getName().setFamily("");
		person.getName().setGiven("");
		person.getAddress().setStreet("Simon-Dach-Straße");
		person.getAddress().setCity("");
		person.getAddress().setPostcode("1234567891234567");
		person.getContact().setEmail("wwATweb.ab");
		person.getContact().setPhone("017812345678");
		constrainViolations = validator.validate(person);
		assertEquals(constrainViolations.size(), 5);
		
		// borderline valid values
		person.setAlias("WW5");
		person.getName().setFamily("A");
		person.getName().setGiven("B");
		person.getAddress().setStreet("Simon-Dach-Straße");
		person.getAddress().setCity("C");
		person.getAddress().setPostcode("123456789123456");
		person.getContact().setEmail("ww@web.ab");
		person.getContact().setPhone("017812345678");
		constrainViolations = validator.validate(person);
		assertEquals(constrainViolations.size(), 0);
		
	}

}
