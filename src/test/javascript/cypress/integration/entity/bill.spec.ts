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

describe('Bill e2e test', () => {
  const billPageUrl = '/bill';
  const billPageUrlPattern = new RegExp('/bill(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/bills').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bills+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bills').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bills/*').as('deleteEntityRequest');
  });

  it('should load Bills', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bill');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Bill').should('exist');
    cy.url().should('match', billPageUrlPattern);
  });

  it('should load details Bill page', function () {
    cy.visit(billPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('bill');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', billPageUrlPattern);
  });

  it('should load create Bill page', () => {
    cy.visit(billPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Bill');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', billPageUrlPattern);
  });

  it('should load edit Bill page', function () {
    cy.visit(billPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('Bill');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', billPageUrlPattern);
  });

  it.skip('should create an instance of Bill', () => {
    cy.visit(billPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Bill');

    cy.get(`[data-cy="title"]`).type('web-enabled').should('have.value', 'web-enabled');

    cy.get(`[data-cy="closedTimestamp"]`).type('2021-10-01T02:33').should('have.value', '2021-10-01T02:33');

    cy.get(`[data-cy="finalAmount"]`).type('17699').should('have.value', '17699');

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
    cy.url().should('match', billPageUrlPattern);
  });

  it.skip('should delete last instance of Bill', function () {
    cy.intercept('GET', '/api/bills/*').as('dialogDeleteRequest');
    cy.visit(billPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('bill').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', billPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
