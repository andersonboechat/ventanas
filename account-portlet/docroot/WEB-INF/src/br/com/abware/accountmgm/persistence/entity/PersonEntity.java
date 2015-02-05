package br.com.abware.accountmgm.persistence.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="jco_person")
public class PersonEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	private long id;

	private long userId;

    private String identity;

    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="jco_person_flat", joinColumns={@JoinColumn(name="personId")}, inverseJoinColumns={@JoinColumn(name="flatId")})
    private List<FlatEntity> flats;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public List<FlatEntity> getFlats() {
		return flats;
	}

	public void setFlats(List<FlatEntity> flats) {
		this.flats = flats;
	}

}
