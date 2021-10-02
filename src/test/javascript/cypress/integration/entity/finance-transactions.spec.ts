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

describe('FinanceTransactions e2e test', () => {
  const financeTransactionsPageUrl = '/finance-transactions';
  const financeTransactionsPageUrlPattern = new RegExp('/finance-transactions(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  beforeEach(() => {
    cy.getOauth2Data();
    cy.get('@oauth2Data').then(oauth2Data => {
      cy.oauthLogin(oauth2Data, username, password);
    });
    cy.intercept('GET', '/api/finance-transactions').as('entitiesRequest');
    cy.visit('');
    cy.get(entityItemSelector).should('exist');
  });

  afterEach(() => {
    cy.oauthLogout();
    cy.clearCache();
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/finance-transactions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/finance-transactions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/finance-transactions/*').as('deleteEntityRequest');
  });

  it('should load FinanceTransactions', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('finance-transactions');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('FinanceTransactions').should('exist');
    cy.url().should('match', financeTransactionsPageUrlPattern);
  });

  it('should load details FinanceTransactions page', function () {
    cy.visit(financeTransactionsPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('financeTransactions');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', financeTransactionsPageUrlPattern);
  });

  it('should load create FinanceTransactions page', () => {
    cy.visit(financeTransactionsPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('FinanceTransactions');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', financeTransactionsPageUrlPattern);
  });

  it('should load edit FinanceTransactions page', function () {
    cy.visit(financeTransactionsPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('FinanceTransactions');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', financeTransactionsPageUrlPattern);
  });

  it.skip('should create an instance of FinanceTransactions', () => {
    cy.visit(financeTransactionsPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('FinanceTransactions');

    cy.get(`[data-cy="executionTimestamp"]`).type('2021-10-02T07:47').should('have.value', '2021-10-02T07:47');

    cy.get(`[data-cy="amountAddedToDestinationAccount"]`).type('56915').should('have.value', '56915');

    cy.get(`[data-cy="comment"]`).type('circuit technologies analyzing').should('have.value', 'circuit technologies analyzing');

    cy.setFieldSelectToLastOfEntity('destinationAccount');

    cy.setFieldSelectToLastOfEntity('referenceAccount');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', financeTransactionsPageUrlPattern);
  });

  it.skip('should delete last instance of FinanceTransactions', function () {
    cy.intercept('GET', '/api/finance-transactions/*').as('dialogDeleteRequest');
    cy.visit(financeTransactionsPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('financeTransactions').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', financeTransactionsPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
