package br.com.abware.complaintbook.persistence.manager;

import java.util.List;

import javax.persistence.Query;

import br.com.abware.complaintbook.persistence.entity.Occurrence;

public class OccurrenceManager extends BaseManager<Occurrence> {
	
	@SuppressWarnings("unchecked")
	public List<Occurrence> findOccurrencesByUserId(long userId) {
		String queryString = "FROM Occurrence WHERE userId = :userId ORDER BY date DESC";
		Query query = em.createQuery(queryString);
		query.setParameter("userId", userId);
		return query.getResultList();
	}
	
	@Override
	protected Class<Occurrence> getEntityClass() {
		return Occurrence.class;
	}

}
