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

describe('Stock e2e test', () => {
  const stockPageUrl = '/stock';
  const stockPageUrlPattern = new RegExp('/stock(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/stocks').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/stocks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stocks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stocks/*').as('deleteEntityRequest');
  });

  it('should load Stocks', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Stock').should('exist');
    cy.url().should('match', stockPageUrlPattern);
  });

  it('should load details Stock page', function () {
    cy.visit(stockPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('stock');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', stockPageUrlPattern);
  });

  it('should load create Stock page', () => {
    cy.visit(stockPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Stock');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', stockPageUrlPattern);
  });

  it('should load edit Stock page', function () {
    cy.visit(stockPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('Stock');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', stockPageUrlPattern);
  });

  it.skip('should create an instance of Stock', () => {
    cy.visit(stockPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Stock');

    cy.get(`[data-cy="addedTimestamp"]`).type('2021-10-01T09:25').should('have.value', '2021-10-01T09:25');

    cy.get(`[data-cy="storageLocation"]`).type('Centers').should('have.value', 'Centers');

    cy.get(`[data-cy="calculatedExpiryTimestamp"]`).type('2021-10-01T11:49').should('have.value', '2021-10-01T11:49');

    cy.get(`[data-cy="manualSetExpiryTimestamp"]`).type('2021-10-01T10:17').should('have.value', '2021-10-01T10:17');

    cy.setFieldSelectToLastOfEntity('product');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', stockPageUrlPattern);
  });

  it.skip('should delete last instance of Stock', function () {
    cy.intercept('GET', '/api/stocks/*').as('dialogDeleteRequest');
    cy.visit(stockPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('stock').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
