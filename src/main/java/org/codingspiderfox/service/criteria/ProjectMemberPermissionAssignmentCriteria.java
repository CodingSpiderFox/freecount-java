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
 * Criteria class for the {@link org.codingspiderfox.domain.ProjectMemberPermissionAssignment} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProjectMemberPermissionAssignmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-member-permission-assignments?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectMemberPermissionAssignmentCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter assignmentTimestamp;

    private LongFilter projectMemberId;

    private LongFilter projectMemberPermissionId;

    private Boolean distinct;

    public ProjectMemberPermissionAssignmentCriteria() {}

    public ProjectMemberPermissionAssignmentCriteria(ProjectMemberPermissionAssignmentCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.assignmentTimestamp = other.assignmentTimestamp == null ? null : other.assignmentTimestamp.copy();
        this.projectMemberId = other.projectMemberId == null ? null : other.projectMemberId.copy();
        this.projectMemberPermissionId = other.projectMemberPermissionId == null ? null : other.projectMemberPermissionId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectMemberPermissionAssignmentCriteria copy() {
        return new ProjectMemberPermissionAssignmentCriteria(this);
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

    public LongFilter getProjectMemberPermissionId() {
        return projectMemberPermissionId;
    }

    public LongFilter projectMemberPermissionId() {
        if (projectMemberPermissionId == null) {
            projectMemberPermissionId = new LongFilter();
        }
        return projectMemberPermissionId;
    }

    public void setProjectMemberPermissionId(LongFilter projectMemberPermissionId) {
        this.projectMemberPermissionId = projectMemberPermissionId;
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
        final ProjectMemberPermissionAssignmentCriteria that = (ProjectMemberPermissionAssignmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(assignmentTimestamp, that.assignmentTimestamp) &&
            Objects.equals(projectMemberId, that.projectMemberId) &&
            Objects.equals(projectMemberPermissionId, that.projectMemberPermissionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, assignmentTimestamp, projectMemberId, projectMemberPermissionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberPermissionAssignmentCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (assignmentTimestamp != null ? "assignmentTimestamp=" + assignmentTimestamp + ", " : "") +
            (projectMemberId != null ? "projectMemberId=" + projectMemberId + ", " : "") +
            (projectMemberPermissionId != null ? "projectMemberPermissionId=" + projectMemberPermissionId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
