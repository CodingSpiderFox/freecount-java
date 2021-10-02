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
 * Criteria class for the {@link org.codingspiderfox.domain.ProjectMemberRoleAssignment} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProjectMemberRoleAssignmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-member-role-assignments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectMemberRoleAssignmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter assignmentTimestamp;

    private LongFilter projectMemberId;

    private LongFilter projectMemberRoleId;

    private Boolean distinct;

    public ProjectMemberRoleAssignmentCriteria() {}

    public ProjectMemberRoleAssignmentCriteria(ProjectMemberRoleAssignmentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.assignmentTimestamp = other.assignmentTimestamp == null ? null : other.assignmentTimestamp.copy();
        this.projectMemberId = other.projectMemberId == null ? null : other.projectMemberId.copy();
        this.projectMemberRoleId = other.projectMemberRoleId == null ? null : other.projectMemberRoleId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectMemberRoleAssignmentCriteria copy() {
        return new ProjectMemberRoleAssignmentCriteria(this);
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

    public ZonedDateTimeFilter getAssignmentTimestamp() {
        return assignmentTimestamp;
    }

    public ZonedDateTimeFilter assignmentTimestamp() {
        if (assignmentTimestamp == null) {
            assignmentTimestamp = new ZonedDateTimeFilter();
        }
        return assignmentTimestamp;
    }

    public void setAssignmentTimestamp(ZonedDateTimeFilter assignmentTimestamp) {
        this.assignmentTimestamp = assignmentTimestamp;
    }

    public LongFilter getProjectMemberId() {
        return projectMemberId;
    }

    public LongFilter projectMemberId() {
        if (projectMemberId == null) {
            projectMemberId = new LongFilter();
        }
        return projectMemberId;
    }

    public void setProjectMemberId(LongFilter projectMemberId) {
        this.projectMemberId = projectMemberId;
    }

    public LongFilter getProjectMemberRoleId() {
        return projectMemberRoleId;
    }

    public LongFilter projectMemberRoleId() {
        if (projectMemberRoleId == null) {
            projectMemberRoleId = new LongFilter();
        }
        return projectMemberRoleId;
    }

    public void setProjectMemberRoleId(LongFilter projectMemberRoleId) {
        this.projectMemberRoleId = projectMemberRoleId;
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
        final ProjectMemberRoleAssignmentCriteria that = (ProjectMemberRoleAssignmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(assignmentTimestamp, that.assignmentTimestamp) &&
            Objects.equals(projectMemberId, that.projectMemberId) &&
            Objects.equals(projectMemberRoleId, that.projectMemberRoleId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, assignmentTimestamp, projectMemberId, projectMemberRoleId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberRoleAssignmentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (assignmentTimestamp != null ? "assignmentTimestamp=" + assignmentTimestamp + ", " : "") +
            (projectMemberId != null ? "projectMemberId=" + projectMemberId + ", " : "") +
            (projectMemberRoleId != null ? "projectMemberRoleId=" + projectMemberRoleId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
