package br.com.abware.accountmgm.bean.model;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.abware.jcondo.core.model.Person;

public class PersonDataModel extends LazyDataModel<Person> {

	private static final long serialVersionUID = 1L;

	private List<Person> people;

	public PersonDataModel(List<Person> people) {
		this.people = people;
	}

	@Override
	public List<Person> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		return people.subList(first, first + pageSize);
	}

}
