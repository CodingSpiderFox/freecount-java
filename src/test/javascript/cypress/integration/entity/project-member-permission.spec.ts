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

describe('ProjectMemberPermission e2e test', () => {
  const projectMemberPermissionPageUrl = '/project-member-permission';
  const projectMemberPermissionPageUrlPattern = new RegExp('/project-member-permission(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/project-member-permissions').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-member-permissions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-member-permissions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-member-permissions/*').as('deleteEntityRequest');
  });

  it('should load ProjectMemberPermissions', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-member-permission');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectMemberPermission').should('exist');
    cy.url().should('match', projectMemberPermissionPageUrlPattern);
  });

  it('should load details ProjectMemberPermission page', function () {
    cy.visit(projectMemberPermissionPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('projectMemberPermission');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionPageUrlPattern);
  });

  it('should load create ProjectMemberPermission page', () => {
    cy.visit(projectMemberPermissionPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberPermission');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionPageUrlPattern);
  });

  it('should load edit ProjectMemberPermission page', function () {
    cy.visit(projectMemberPermissionPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberPermission');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionPageUrlPattern);
  });

  it('should create an instance of ProjectMemberPermission', () => {
    cy.visit(projectMemberPermissionPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberPermission');

    cy.get(`[data-cy="createdTimestamp"]`).type('2021-10-02T09:24').should('have.value', '2021-10-02T09:24');

    cy.get(`[data-cy="projectMemberPermission"]`).select('ADD_MEMBER');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberPermissionPageUrlPattern);
  });

  it('should delete last instance of ProjectMemberPermission', function () {
    cy.intercept('GET', '/api/project-member-permissions/*').as('dialogDeleteRequest');
    cy.visit(projectMemberPermissionPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('projectMemberPermission').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectMemberPermissionPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
