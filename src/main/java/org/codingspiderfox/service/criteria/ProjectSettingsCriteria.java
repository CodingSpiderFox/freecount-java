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

/**
 * Criteria class for the {@link org.codingspiderfox.domain.ProjectSettings} entity. This class is used
 * in {@link org.codingspiderfox.web.rest.ProjectSettingsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-settings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ProjectSettingsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BooleanFilter mustProvideBillCopyByDefault;

    private LongFilter projectId;

    private Boolean distinct;

    public ProjectSettingsCriteria() {}

    public ProjectSettingsCriteria(ProjectSettingsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.mustProvideBillCopyByDefault = other.mustProvideBillCopyByDefault == null ? null : other.mustProvideBillCopyByDefault.copy();
        this.projectId = other.projectId == null ? null : other.projectId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProjectSettingsCriteria copy() {
        return new ProjectSettingsCriteria(this);
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

    public BooleanFilter getMustProvideBillCopyByDefault() {
        return mustProvideBillCopyByDefault;
    }

    public BooleanFilter mustProvideBillCopyByDefault() {
        if (mustProvideBillCopyByDefault == null) {
            mustProvideBillCopyByDefault = new BooleanFilter();
        }
        return mustProvideBillCopyByDefault;
    }

    public void setMustProvideBillCopyByDefault(BooleanFilter mustProvideBillCopyByDefault) {
        this.mustProvideBillCopyByDefault = mustProvideBillCopyByDefault;
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
        final ProjectSettingsCriteria that = (ProjectSettingsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(mustProvideBillCopyByDefault, that.mustProvideBillCopyByDefault) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mustProvideBillCopyByDefault, projectId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSettingsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (mustProvideBillCopyByDefault != null ? "mustProvideBillCopyByDefault=" + mustProvideBillCopyByDefault + ", " : "") +
            (projectId != null ? "projectId=" + projectId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
