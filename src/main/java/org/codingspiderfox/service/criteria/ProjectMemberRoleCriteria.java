package org.codingspiderfox.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.codingspiderfox.domain.enumeration.ProjectMemberRoleEnum;
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
 * Criteria class for the {@link org.codingspiderfox.domain.ProjectMemberRole} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProjectMemberRoleResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-member-roles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectMemberRoleCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProjectMemberRoleEnum
     */
    public static class ProjectMemberRoleEnumFilter extends Filter<ProjectMemberRoleEnum> {

        public ProjectMemberRoleEnumFilter() {}

        public ProjectMemberRoleEnumFilter(ProjectMemberRoleEnumFilter filter) {
            super(filter);
        }

        @Override
        public ProjectMemberRoleEnumFilter copy() {
            return new ProjectMemberRoleEnumFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter createdTimestamp;

    private ProjectMemberRoleEnumFilter projectMemberRole;

    private Boolean distinct;

    public ProjectMemberRoleCriteria() {}

    public ProjectMemberRoleCriteria(ProjectMemberRoleCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.createdTimestamp = other.createdTimestamp == null ? null : other.createdTimestamp.copy();
        this.projectMemberRole = other.projectMemberRole == null ? null : other.projectMemberRole.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectMemberRoleCriteria copy() {
        return new ProjectMemberRoleCriteria(this);
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

    public ZonedDateTimeFilter getCreatedTimestamp() {
        return createdTimestamp;
    }

    public ZonedDateTimeFilter createdTimestamp() {
        if (createdTimestamp == null) {
            createdTimestamp = new ZonedDateTimeFilter();
        }
        return createdTimestamp;
    }

    public void setCreatedTimestamp(ZonedDateTimeFilter createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public ProjectMemberRoleEnumFilter getProjectMemberRole() {
        return projectMemberRole;
    }

    public ProjectMemberRoleEnumFilter projectMemberRole() {
        if (projectMemberRole == null) {
            projectMemberRole = new ProjectMemberRoleEnumFilter();
        }
        return projectMemberRole;
    }

    public void setProjectMemberRole(ProjectMemberRoleEnumFilter projectMemberRole) {
        this.projectMemberRole = projectMemberRole;
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
        final ProjectMemberRoleCriteria that = (ProjectMemberRoleCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(createdTimestamp, that.createdTimestamp) &&
            Objects.equals(projectMemberRole, that.projectMemberRole) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdTimestamp, projectMemberRole, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberRoleCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (createdTimestamp != null ? "createdTimestamp=" + createdTimestamp + ", " : "") +
            (projectMemberRole != null ? "projectMemberRole=" + projectMemberRole + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
