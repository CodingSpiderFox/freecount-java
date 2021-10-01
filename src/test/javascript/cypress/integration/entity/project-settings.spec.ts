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

describe('ProjectSettings e2e test', () => {
  const projectSettingsPageUrl = '/project-settings';
  const projectSettingsPageUrlPattern = new RegExp('/project-settings(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/project-settings').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/project-settings+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/project-settings').as('postEntityRequest');
    cy.intercept('DELETE', '/api/project-settings/*').as('deleteEntityRequest');
  });

  it('should load ProjectSettings', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('project-settings');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ProjectSettings').should('exist');
    cy.url().should('match', projectSettingsPageUrlPattern);
  });

  it('should load details ProjectSettings page', function () {
    cy.visit(projectSettingsPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('projectSettings');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectSettingsPageUrlPattern);
  });

  it('should load create ProjectSettings page', () => {
    cy.visit(projectSettingsPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectSettings');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectSettingsPageUrlPattern);
  });

  it('should load edit ProjectSettings page', function () {
    cy.visit(projectSettingsPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectSettings');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectSettingsPageUrlPattern);
  });

  it.skip('should create an instance of ProjectSettings', () => {
    cy.visit(projectSettingsPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('ProjectSettings');

    cy.get(`[data-cy="mustProvideBillCopyByDefault"]`).should('not.be.checked');
    cy.get(`[data-cy="mustProvideBillCopyByDefault"]`).click().should('be.checked');

    cy.setFieldSelectToLastOfEntity('project');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', projectSettingsPageUrlPattern);
  });

  it.skip('should delete last instance of ProjectSettings', function () {
    cy.intercept('GET', '/api/project-settings/*').as('dialogDeleteRequest');
    cy.visit(projectSettingsPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('projectSettings').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', projectSettingsPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
