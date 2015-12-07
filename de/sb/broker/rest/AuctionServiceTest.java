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
		Auction auction = new Auction(person);
		auction.setTitle("Testauction");
		auction.setDescription("Test description");
		auction.setAskingPrice(1);
		auction.setClosureTimestamp(1);
		auction.setUnitCount((short) 1);
		
		em.getTransaction().begin();
		em.persist(auction);
		em.getTransaction().commit();
		this.getWasteBasket().add(auction.getIdentity());
				
		//test QueryParam "title"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("title", "Testauction");
		Response response = webTarget.request().get();
		List<Auction> all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("Test description", all.get(0).getDescription());
		
		//test QueryParam "UCLower"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("UCLower", "0");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("1", all.get(0).getUnitCount());
		
		//test QueryParam "UCUpper"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("UCUpper", "2");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("1", all.get(0).getUnitCount());
		
		//test QueryParam "priceLower"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("priceLower", "0");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("1", all.get(0).getAskingPrice());
		
		//test QueryParam "priceUpper"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("priceUpper", "2");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("1", all.get(0).getAskingPrice());
		
//		TODO: check if correct 
//		//test QueryParam "resultOffset"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("resultOffset", "1");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertEquals("1", all.get(0).getAskingPrice());
//		
//		//test QueryParam "resultLength"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("resultLength", "2");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertEquals("1", all.get(0).getAskingPrice());
//		
//		//test QueryParam "creationtimeLower"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("creationtimeLower", "");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertEquals("1", all.get(0).getAskingPrice());
//		
//		//test QueryParam "creationtimeUpper"
//		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("creationtimeUpper", "");
//		response = webTarget.request().get();
//		all = response.readEntity(new GenericType<List<Auction>>() {});
//		assertEquals("1", all.get(0).getAskingPrice());
		
		//test QueryParam "closuretimeLower"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("closuretimeLower", "0");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("1", all.get(0).getClosureTimestamp());
		
		//test QueryParam "closuretimeUpper"
		webTarget = newWebTarget("ines", "ines").path("auctions/").queryParam("closuretimeUpper", "2");
		response = webTarget.request().get();
		all = response.readEntity(new GenericType<List<Auction>>() {});
		assertEquals("1", all.get(0).getClosureTimestamp());
		
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
		Auction auction = new Auction(person);
		auction.setTitle("Testauction");
		auction.setDescription("Test description");
		auction.setAskingPrice(1);
		auction.setClosureTimestamp(1);
		auction.setUnitCount((short) 1);

		em.getTransaction().begin();
		em.persist(auction);
		em.getTransaction().commit();
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
