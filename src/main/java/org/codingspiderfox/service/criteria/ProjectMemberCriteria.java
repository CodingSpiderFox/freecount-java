package org.codingspiderfox.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.service.filter.ZonedDateTimeFilter;

/**
 * Criteria class for the {@link org.codingspiderfox.domain.ProjectMember} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProjectMemberResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-members?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectMemberCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter addedTimestamp;

    private StringFilter userId;

    private LongFilter projectId;

    private Boolean distinct;

    public ProjectMemberCriteria() {}

    public ProjectMemberCriteria(ProjectMemberCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.addedTimestamp = other.addedTimestamp == null ? null : other.addedTimestamp.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectMemberCriteria copy() {
        return new ProjectMemberCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ZonedDateTimeFilter getAddedTimestamp() {
        return addedTimestamp;
    }

    public ZonedDateTimeFilter addedTimestamp() {
        if (addedTimestamp == null) {
            addedTimestamp = new ZonedDateTimeFilter();
        }
        return addedTimestamp;
    }

    public void setAddedTimestamp(ZonedDateTimeFilter addedTimestamp) {
        this.addedTimestamp = addedTimestamp;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public LongFilter projectId() {
        if (projectId == null) {
            projectId = new LongFilter();
        }
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProjectMemberCriteria that = (ProjectMemberCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(addedTimestamp, that.addedTimestamp) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addedTimestamp, userId, projectId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (addedTimestamp != null ? "addedTimestamp=" + addedTimestamp + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (projectId != null ? "projectId=" + projectId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
