package de.sb.broker.rest;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Person;

public class AuctionServiceTest extends ServiceTest{


	@Test
	public void testCriteriaQueries() {
		// test status without criteria
		WebTarget webTarget = newWebTarget("ines", "ines").path("auctions/");
		assertEquals(200, webTarget.request().get().getStatus());
		
		//persist person and auction entity
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();

		Auction auction = new Auction(person);
		auction.setTitle("Testauction");
		auction.setDescription("Test description");
		auction.setUnitCount((short)10000);
		auction.setAskingPrice(11111);
				
		em.getTransaction().begin();
		em.persist(auction);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		this.getWasteBasket().add(auction.getIdentity());

		//test QueryParam "title"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("title", "Testauction");
		Response response = webTarget.request().get();
		List<Auction> all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("Test description", all.get(0).getDescription());
		
		//test QueryParam "UCLower"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("UCLower", 9999);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertTrue(9999 <= all.get(0).getUnitCount());
		
//		//test QueryParam "UCUpper"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("UCUpper", 40000);
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertTrue(40000 >= all.get(0).getUnitCount());

		
		//test QueryParam "priceLower"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("priceLower", 11110);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertTrue(11110 <= all.get(0).getAskingPrice());

		
		//test QueryParam "priceUpper"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("priceUpper", 11112);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertTrue(11112 >= all.get(0).getAskingPrice());
		
		//test QueryParam "resultOffset"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("resultOffset", 0);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("resultOffset", 1);
		response = webTarget.request().get();
		List<Auction> allWithResultOffset1 = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals(all.size(), allWithResultOffset1.size()+1);
		
		//test QueryParam "resultLength"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("resultLength", 2);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals(2, all.size());
		
		//test QueryParam "creationtimeLower"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("creationtimeLower", System.currentTimeMillis()-1000*60);
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertEquals("Test description", all.get(0).getDescription());
		
//		//test QueryParam "creationtimeUpper"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("creationtimeUpper", System.currentTimeMillis()+1000);
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertEquals("Test description", all.get(0).getDescription());
//		
////		//test QueryParam "closuretimeLower"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("closuretimeLower", System.currentTimeMillis());
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertTrue(System.currentTimeMillis() <= all.get(0).getClosureTimestamp());
		
		//test QueryParam "closuretimeUpper"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("closuretimeUpper", System.currentTimeMillis() + 48*60*60*1000);
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertTrue((System.currentTimeMillis()+ 48*60*60*1000) >= all.get(0).getClosureTimestamp());
		
		//test QueryParam "descriptionFrag"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("descriptionFrag", "description");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("Test description", all.get(0).getDescription());		
	}
	
	@Test
	public void testIdentityQueries() {
		// test for invalid authentication
		WebTarget webTarget = newWebTarget("ines", "BAM").path("auctions/1");
		assertEquals(401, webTarget.request().get().getStatus());
		
		//persist person and auction entity
		Person person = this.createValidPersonEntity();
		em.getTransaction().begin();
		em.persist(person);
		em.getTransaction().commit();

		Auction auction = new Auction(person);
		auction.setTitle("Testauction");
		auction.setDescription("Test description");
		em.getTransaction().begin();
		em.persist(auction);
		em.getTransaction().commit();
		this.getWasteBasket().add(person.getIdentity());
		this.getWasteBasket().add(auction.getIdentity());

		// test valid entity
		webTarget = newWebTarget("ines", "ines").path("auctions/" + auction.getIdentity());
		final Response response = webTarget.request().get();
		Auction a = response.readEntity(Auction.class);
		assertEquals("Testauction", a.getTitle());
		
		// test invalid entity
		webTarget = newWebTarget("ines", "ines").path("auctions/1234567");
		final int status = webTarget.request().get().getStatus();
		assertEquals(404, status);
	}
		
	@Test
	public void testBidRelations() {
		fail("Not yet implemented");
	}
}
