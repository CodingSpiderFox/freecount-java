import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('ProjectMemberPermissionAssignment e2e test', () => {
  const projectMemberPermissionAssignmentPageUrl = '/project-member-permission-assignment';
  const projectMemberPermissionAssignmentPageUrlPattern = new RegExp('/project-member-permission-assignment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/project-member-permission-assignments').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-member-permission-assignments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-member-permission-assignments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-member-permission-assignments/*').as('deleteEntityRequest');
  });

  it('should load ProjectMemberPermissionAssignments', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-member-permission-assignment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectMemberPermissionAssignment').should('exist');
    cy.url().should('match', projectMemberPermissionAssignmentPageUrlPattern);
  });

  it('should load details ProjectMemberPermissionAssignment page', function () {
    cy.visit(projectMemberPermissionAssignmentPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('projectMemberPermissionAssignment');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionAssignmentPageUrlPattern);
  });

  it('should load create ProjectMemberPermissionAssignment page', () => {
    cy.visit(projectMemberPermissionAssignmentPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberPermissionAssignment');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionAssignmentPageUrlPattern);
  });

  it('should load edit ProjectMemberPermissionAssignment page', function () {
    cy.visit(projectMemberPermissionAssignmentPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberPermissionAssignment');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionAssignmentPageUrlPattern);
  });

  it.skip('should create an instance of ProjectMemberPermissionAssignment', () => {
    cy.visit(projectMemberPermissionAssignmentPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberPermissionAssignment');

    cy.get(`[data-cy="assignmentTimestamp"]`).type('2021-10-02T00:25').should('have.value', '2021-10-02T00:25');

    cy.setFieldSelectToLastOfEntity('projectMember');

    cy.setFieldSelectToLastOfEntity('projectMemberPermission');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionAssignmentPageUrlPattern);
  });

  it.skip('should delete last instance of ProjectMemberPermissionAssignment', function () {
    cy.intercept('GET', '/api/project-member-permission-assignments/*').as('dialogDeleteRequest');
    cy.visit(projectMemberPermissionAssignmentPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('projectMemberPermissionAssignment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectMemberPermissionAssignmentPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
