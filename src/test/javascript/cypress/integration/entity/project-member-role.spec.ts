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

describe('ProjectMemberRole e2e test', () => {
  const projectMemberRolePageUrl = '/project-member-role';
  const projectMemberRolePageUrlPattern = new RegExp('/project-member-role(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/project-member-roles').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-member-roles+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-member-roles').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-member-roles/*').as('deleteEntityRequest');
  });

  it('should load ProjectMemberRoles', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-member-role');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectMemberRole').should('exist');
    cy.url().should('match', projectMemberRolePageUrlPattern);
  });

  it('should load details ProjectMemberRole page', function () {
    cy.visit(projectMemberRolePageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('projectMemberRole');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRolePageUrlPattern);
  });

  it('should load create ProjectMemberRole page', () => {
    cy.visit(projectMemberRolePageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberRole');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRolePageUrlPattern);
  });

  it('should load edit ProjectMemberRole page', function () {
    cy.visit(projectMemberRolePageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberRole');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRolePageUrlPattern);
  });

  it('should create an instance of ProjectMemberRole', () => {
    cy.visit(projectMemberRolePageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectMemberRole');

    cy.get(`[data-cy="createdTimestamp"]`).type('2021-10-01T21:01').should('have.value', '2021-10-01T21:01');

    cy.get(`[data-cy="projectMemberRole"]`).select('Y');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectMemberRolePageUrlPattern);
  });

  it('should delete last instance of ProjectMemberRole', function () {
    cy.intercept('GET', '/api/project-member-roles/*').as('dialogDeleteRequest');
    cy.visit(projectMemberRolePageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('projectMemberRole').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectMemberRolePageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
