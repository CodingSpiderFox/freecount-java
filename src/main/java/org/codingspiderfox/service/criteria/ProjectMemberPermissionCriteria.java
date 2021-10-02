package org.codingspiderfox.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.codingspiderfox.domain.enumeration.ProjectMemberPermissionEnum;
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
 * Criteria class for the {@link org.codingspiderfox.domain.ProjectMemberPermission} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProjectMemberPermissionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-member-permissions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectMemberPermissionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProjectMemberPermissionEnum
     */
    public static class ProjectMemberPermissionEnumFilter extends Filter<ProjectMemberPermissionEnum> {

        public ProjectMemberPermissionEnumFilter() {}

        public ProjectMemberPermissionEnumFilter(ProjectMemberPermissionEnumFilter filter) {
            super(filter);
        }

        @Override
        public ProjectMemberPermissionEnumFilter copy() {
            return new ProjectMemberPermissionEnumFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ZonedDateTimeFilter createdTimestamp;

    private ProjectMemberPermissionEnumFilter projectMemberPermission;

    private Boolean distinct;

    public ProjectMemberPermissionCriteria() {}

    public ProjectMemberPermissionCriteria(ProjectMemberPermissionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.createdTimestamp = other.createdTimestamp == null ? null : other.createdTimestamp.copy();
        this.projectMemberPermission = other.projectMemberPermission == null ? null : other.projectMemberPermission.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectMemberPermissionCriteria copy() {
        return new ProjectMemberPermissionCriteria(this);
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

    public ProjectMemberPermissionEnumFilter getProjectMemberPermission() {
        return projectMemberPermission;
    }

    public ProjectMemberPermissionEnumFilter projectMemberPermission() {
        if (projectMemberPermission == null) {
            projectMemberPermission = new ProjectMemberPermissionEnumFilter();
        }
        return projectMemberPermission;
    }

    public void setProjectMemberPermission(ProjectMemberPermissionEnumFilter projectMemberPermission) {
        this.projectMemberPermission = projectMemberPermission;
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
        final ProjectMemberPermissionCriteria that = (ProjectMemberPermissionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(createdTimestamp, that.createdTimestamp) &&
            Objects.equals(projectMemberPermission, that.projectMemberPermission) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdTimestamp, projectMemberPermission, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberPermissionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (createdTimestamp != null ? "createdTimestamp=" + createdTimestamp + ", " : "") +
            (projectMemberPermission != null ? "projectMemberPermission=" + projectMemberPermission + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
