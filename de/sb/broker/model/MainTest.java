package de.sb.broker.model;

import static java.util.logging.Level.WARNING;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class MainTest extends EntityTest{
	
	public static EntityManager em;
	final static Set<Long> wasteBasket = new HashSet<>();

	public static void main(String[] args) {
		final Set<Long> wasteBasket = new HashSet<>();
		em = Persistence.createEntityManagerFactory("broker").createEntityManager();
		em.getTransaction().begin();
		
//		Person person = generatePerson();
//		createPerson(person);
		
		Auction auction = generateAuction();
		createAuction(auction);
		
		
		em.getTransaction().commit();
//		wasteBasket.add(person.getIdentity());
		wasteBasket.add(auction.getIdentity());
		em.close();
	}
	
	public static Person generatePerson(){
		Person person = new Person();
		person.setAlias("WW4");
		person.getName().setFamily("White");
		person.getName().setGiven("Walter");
		person.getAddress().setStreet("Simon-Dach.Straße");
		person.getAddress().setCity("Berlin");
		person.getAddress().setPostcode("10245");
		person.getContact().setEmail("ww@web.eu");
		person.getContact().setPhone("017812345678");
		return person;
	}
	
	public static Auction generateAuction(){
		Auction auction = new Auction(generatePerson());
		auction.setTitle("title");
		auction.setDescription("desc");
		return auction;
	}
	
	public static void createPerson(Person person) {

	    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    Validator validator = factory.getValidator();
	    Set<ConstraintViolation<Person>> constraintViolations = validator.validate(person);
	    if(constraintViolations.size() > 0){
	        Iterator<ConstraintViolation<Person>> iterator = constraintViolations.iterator();
	        while(iterator.hasNext()){
	            ConstraintViolation<Person> cv = iterator.next();
	            System.err.println(cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage());
	        }
	    } else{
	        em.persist(person);
	    }
	}
	
	public static void createAuction(Auction auction) {

	    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    Validator validator = factory.getValidator();
	    Set<ConstraintViolation<Auction>> constraintViolations = validator.validate(auction);
	    if(constraintViolations.size() > 0){
	        Iterator<ConstraintViolation<Auction>> iterator = constraintViolations.iterator();
	        while(iterator.hasNext()){
	            ConstraintViolation<Auction> cv = iterator.next();
	            System.err.println(cv.getRootBeanClass().getName()+"."+cv.getPropertyPath() + " " +cv.getMessage());
	        }
	    } else{
	        em.persist(auction);
	    }
	}
	
	public void emptyWasteBasket () {
		try {
			em.getTransaction().begin();
			for (final Long identity : wasteBasket) {
				try {
					final Object entity = em.find(BaseEntity.class, identity);
					if (entity != null) em.remove(entity);
				} catch (final Exception exception) {
					Logger.getGlobal().log(WARNING, exception.getMessage(), exception);
				}
			}
			em.getTransaction().commit();
			wasteBasket.clear();
		} finally {
			em.close();
		}
	}
}
