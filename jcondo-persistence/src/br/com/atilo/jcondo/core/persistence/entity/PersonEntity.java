package br.com.atilo.jcondo.core.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="jco_person")
public class PersonEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private long id;

	private long userId;

    private String identity;

	@Column(nullable=false, columnDefinition="INT(1)")
    private Boolean registerComplete;

    @OneToMany(cascade=CascadeType.ALL, mappedBy="person", orphanRemoval=true)
    private List<MembershipEntity> memberships;

    public PersonEntity() {
		this.memberships = new ArrayList<MembershipEntity>();
	}
    
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

	public Boolean getRegisterComplete() {
		return registerComplete;
	}

	public void setRegisterComplete(Boolean registerComplete) {
		this.registerComplete = registerComplete;
	}

	public List<MembershipEntity> getMemberships() {
		return memberships;
	}

	public void setMemberships(List<MembershipEntity> memberships) {
		this.memberships = memberships;
	}

}
