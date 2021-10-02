package org.codingspiderfox.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ProjectMember.
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "project_member")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "projectmember")
public class ProjectMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "added_timestamp", nullable = false)
    private ZonedDateTime addedTimestamp;

    @ManyToOne
    private User user;

    @OneToOne(optional = false)
    @NotNull
    @MapsId
    @JoinColumn(name = "id")
    private Project project;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProjectMember id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getAddedTimestamp() {
        return this.addedTimestamp;
    }

    public ProjectMember addedTimestamp(ZonedDateTime addedTimestamp) {
        this.setAddedTimestamp(addedTimestamp);
        return this;
    }

    public void setAddedTimestamp(ZonedDateTime addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProjectMember user(User user) {
        this.setUser(user);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectMember project(Project project) {
        this.setProject(project);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectMember)) {
            return false;
        }
        return id != null && id.equals(((ProjectMember) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMember{" +
            "id=" + getId() +
            ", addedTimestamp='" + getAddedTimestamp() + "'" +
            "}";
    }
}
