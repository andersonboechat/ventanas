package br.com.abware.complaintbook.persistence.manager;

import org.junit.Test;

public class OccurrenceManagerTest {

	@Test
	public void findOccurrencesByUserIdTest() {
		OccurrenceManager manager = new OccurrenceManager();
		manager.findOccurrencesByUserId(10195);
	}
}
