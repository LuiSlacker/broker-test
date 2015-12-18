package de.sb.broker.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;
import org.junit.Test;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Person;

public class PersonServiceTest extends ServiceTest{

	
	@Test
	public void testCriteriaQueries() {
		
		
		//persist person entity with alias='TTTT' & pwd='secret'
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		// test status without criteria
		WebTarget webTarget = newWebTarget("TTTT", "secret").path("people/");
		assertEquals(200, webTarget.request().get().getStatus());
		
		
		//test QueryParam "alias"
		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("alias", "TTTT");
		Response response = webTarget.request().get();
		List<Person> all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "firstName"
		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("firstName", "Test");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "familyName"
		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("familyName", "Tester");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "email"
		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("email", "test@tester.org");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "phone"
		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("phone", "015212345678");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
//		
//		//test QueryParam "street"
//		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("street", "Testweg 1");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Person>>() {});
//		assertEquals("Tester", all.get(0).getName().getFamily());
//		
//		//test QueryParam "postcode"
//		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("postcode", "10245");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Person>>() {});
//		assertEquals("Tester", all.get(0).getName().getFamily());
//		
//		//test QueryParam "city"
//		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("city", "Testhausen");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Person>>() {});
//		assertEquals("Tester", all.get(0).getName().getFamily());
//		
//		//test QueryParam "creationtimeLower"
//		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("creationtimeLower", System.currentTimeMillis()-1000*60);
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Person>>() {});
//		assertEquals("Tester", all.get(0).getName().getFamily());
//		
//		//test QueryParam "creationtimeUpper"
//		webTarget = newWebTarget("TTTT", "secret").path("people/").queryParam("creationtimeUpper", System.currentTimeMillis()+1000);
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Person>>() {});
//		assertEquals("Tester", all.get(0).getName().getFamily());
	}
	
	@Test
	public void testIdentityQueries() {
		//persist person entity
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		// test for invalid authentication
		WebTarget webTarget = newWebTarget("TTTT", "BAM").path("people/1");
		assertEquals(401, webTarget.request().get().getStatus());
		
		// test valid entity
		webTarget = newWebTarget("TTTT", "secret").path("people/" + person.getIdentity());
		final Response response = webTarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Test", p.getName().getGiven());
		
		// test invalid entity
		webTarget = newWebTarget("TTTT", "secret").path("people/1234567");
		final int status = webTarget.request().get().getStatus();
		assertEquals(404, status);
	}
	
	
	@Test
	public void testAuctionRelationQueries() {
		
		//persist person entity with alias='TTTT' & pwd='secret'
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		//persist auction entity
		Auction auction = new Auction(person);
		auction.setTitle("Testauction");
		auction.setDescription("Test description");
		em.getTransaction().begin();
		em.persist(auction);
		em.getTransaction().commit();
		this.getWasteBasket().add(auction.getIdentity());
		
		WebTarget webTarget = newWebTarget("TTTT", "secret").path("people/" + person.getIdentity());
		Response response = webTarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Test", p.getName().getGiven());
		
		webTarget = newWebTarget("TTTT", "secret").path("people/" + person.getIdentity() + "/auctions");
		response = webTarget.request().get();
		List<Auction> all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("Testauction", all.get(0).getTitle());
	}
	
	@Test
	public void testBidRelationQueries() {
//		///persist person entity with alias='TTTT' & pwd='secret'
//		Person person = this.createValidPersonEntity();
//		em.getTransaction().begin();
//		em.persist(person);
//		em.getTransaction().commit();
//		this.getWasteBasket().add(person.getIdentity());
		
	}
	
	@Test
	public void testRequester() {
		//persist person entity with alias='TTTT' & pwd='secret'
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
				
		WebTarget webTarget = newWebTarget("TTTT", "secret").path("people/requester/");
		final Response response = webTarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Test", p.getName().getGiven());
	}
	
	@Test
	public void testLifeCycle() {
//		WebTarget webTarget = newWebTarget("ines", "ines").path("people/");
//		Person p = new Person();
//		p.setAlias("Blubbbb");
//		p.getName().setFamily("sdfs");
//		p.getName().setGiven("sssdf");
//		p.getContact().setEmail("a@b.desss");
//		p.getAddress().setCity("Hometown");
//		final ClientResponse response = webTarget.request().post(ClientResponse.class, Person.class);
//		Person p = response.readEntity(Person.class);
//		assertEquals("Test", p.getName().getGiven());
	}
	
}
