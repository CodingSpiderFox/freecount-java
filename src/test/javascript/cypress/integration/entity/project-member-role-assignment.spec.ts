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

describe('ProjectMemberRoleAssignment e2e test', () => {
  const projectMemberRoleAssignmentPageUrl = '/project-member-role-assignment';
  const projectMemberRoleAssignmentPageUrlPattern = new RegExp('/project-member-role-assignment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/project-member-role-assignments').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-member-role-assignments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-member-role-assignments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-member-role-assignments/*').as('deleteEntityRequest');
  });

  it('should load ProjectMemberRoleAssignments', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-member-role-assignment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectMemberRoleAssignment').should('exist');
    cy.url().should('match', projectMemberRoleAssignmentPageUrlPattern);
  });

  it('should load details ProjectMemberRoleAssignment page', function () {
    cy.visit(projectMemberRoleAssignmentPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('projectMemberRoleAssignment');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRoleAssignmentPageUrlPattern);
  });

  it('should load create ProjectMemberRoleAssignment page', () => {
    cy.visit(projectMemberRoleAssignmentPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberRoleAssignment');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRoleAssignmentPageUrlPattern);
  });

  it('should load edit ProjectMemberRoleAssignment page', function () {
    cy.visit(projectMemberRoleAssignmentPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberRoleAssignment');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRoleAssignmentPageUrlPattern);
  });

  it.skip('should create an instance of ProjectMemberRoleAssignment', () => {
    cy.visit(projectMemberRoleAssignmentPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberRoleAssignment');

    cy.get(`[data-cy="assignmentTimestamp"]`).type('2021-10-01T19:54').should('have.value', '2021-10-01T19:54');

    cy.setFieldSelectToLastOfEntity('projectMember');

    cy.setFieldSelectToLastOfEntity('projectMemberRole');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRoleAssignmentPageUrlPattern);
  });

  it.skip('should delete last instance of ProjectMemberRoleAssignment', function () {
    cy.intercept('GET', '/api/project-member-role-assignments/*').as('dialogDeleteRequest');
    cy.visit(projectMemberRoleAssignmentPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('projectMemberRoleAssignment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectMemberRoleAssignmentPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
