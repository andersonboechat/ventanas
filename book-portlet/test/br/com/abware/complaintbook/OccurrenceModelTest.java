package br.com.abware.complaintbook;

import org.junit.Test;

import com.liferay.portal.model.UserFake;

public class OccurrenceModelTest {

	@Test
	public void getOccurrencesTest() throws Exception {
		OccurrenceModel.getOccurrences(1);
	}

	@Test
	public void doOccurrenceTest() throws Exception {
		UserFake uf = new UserFake();
		uf.setUserId(10195);
		
		OccurrenceModel occurrence = new OccurrenceModel(OccurenceType.COMPLAINT, uf);
		occurrence.setText(null);
		
		OccurrenceModel.doOccurrence(occurrence);
	}
	
	@Test
	public void generateCodeTest() {
		UserFake uf = new UserFake();
		uf.setUserId(10195);

		OccurrenceModel occurrence = new OccurrenceModel(OccurenceType.COMPLAINT, uf);
		
		//System.out.println(occurrence.generateCode());
	}
}
