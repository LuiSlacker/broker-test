package de.sb.broker.rest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.sb.broker.model.Person;

public class PersonServiceTest extends ServiceTest{

	
	@Test
	public void testCriteriaQueries() {
		
		// test status without criteria
		WebTarget webTarget = newWebTarget("ines", "ines").path("people/");
		assertEquals(200, webTarget.request().get().getStatus());
		
		//persist person entity
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		//test QueryParam "alias"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("alias", "TT");
		Response response = webTarget.request().get();
		List<Person> all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "firstName"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("firstName", "Test");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "familyName"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("familyName", "Tester");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "email"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("email", "test@tester.de");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "phone"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("phone", "015212345678");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "street"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("street", "Testweg 1");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "postcode"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("postcode", "10245");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "city"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("city", "Testhausen");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "creationtimeLower"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("creationtimeLower", System.currentTimeMillis()-1000);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
		
		//test QueryParam "creationtimeUpper"
		webTarget = newWebTarget("ines", "ines").path("people/").queryParam("creationtimeUpper", System.currentTimeMillis()+1000);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Person>>() {});
		assertEquals("Tester", all.get(0).getName().getFamily());
	}
	
	@Test
	public void testIdentityQueries() {
		
		// test for invalid authentication
		WebTarget webTarget = newWebTarget("ines", "BAM").path("people/1");
		assertEquals(401, webTarget.request().get().getStatus());
		
		//persist person entity
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		
		// test valid entity
		webTarget = newWebTarget("ines", "ines").path("people/" + person.getIdentity());
		final Response response = webTarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Test", p.getName().getGiven());
		
		// test invalid entity
		webTarget = newWebTarget("ines", "ines").path("people/1234567");
		final int status = webTarget.request().get().getStatus();
		assertEquals(404, status);
	}
	
	@Test
	public void testBidRelations() {
		//TODO
	}
	
	@Test
	public void testRequester() {
		WebTarget webTarget = newWebTarget("ines", "ines").path("people/requester/");
		final Response response = webTarget.request().get();
		Person p = response.readEntity(Person.class);
		assertEquals("Ines", p.getName().getGiven());
	}
}
