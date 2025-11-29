/* global cy */

import { PRODUCT_DASHBOARD_PATH, DEFAULT_TIMEOUT } from '../support/e2e';

const selectors = {
	form: '[data-testid="product-form"]',
	nameInput: '[data-testid="name-input"]',
	priceInput: '[data-testid="price-input"]',
	quantityInput: '[data-testid="quantity-input"]',
	categorySelect: '[data-testid="category-select"]',
	descriptionInput: '[data-testid="description-input"]',
	submitButton: '[data-testid="submit-product-btn"]',
	serverMessage: '[data-testid="server-message"]',
	searchInput: '[data-testid="search-input"]',
	applyFiltersButton: '[data-testid="apply-filters-btn"]',
	paginationStatus: '[data-testid="pagination-status"]',
	table: '[data-testid="product-list"]',
	row: '[data-testid="product-item"]',
	rowNameCell: '[data-testid="cell-name"]',
};

const clearAndType = (selector, value) => {
	if (value === undefined || value === null) {
		return;
	}
	cy.get(selector).clear().type(String(value));
};

export const productDashboardPage = {
	visit() {
		cy.visit(PRODUCT_DASHBOARD_PATH);
		cy.get(selectors.table).should('be.visible');
	},

	expectRowCount(expected) {
		if (expected === 0) {
			cy.get(selectors.row).should('have.length', 0);
			return;
		}

		cy.get(selectors.row, { timeout: DEFAULT_TIMEOUT }).should('have.length', expected);
	},

	fillForm(product) {
		cy.get(selectors.form).should('be.visible');
		clearAndType(selectors.nameInput, product.name);
		clearAndType(selectors.priceInput, product.price);
		clearAndType(selectors.quantityInput, product.quantity);
		cy.get(selectors.categorySelect).select(product.category);
		clearAndType(selectors.descriptionInput, product.description);
	},

	setSearchTerm(value) {
		cy.get(selectors.searchInput).clear().type(value);
	},

	applyFilters() {
		cy.get(selectors.applyFiltersButton).click();
		cy.get(selectors.table).should('be.visible');
	},

	searchByName(name) {
		this.setSearchTerm(name);
		this.applyFilters();
	},

	submit() {
		cy.get(selectors.submitButton).click();
	},

	selectRowByName(name) {
		return cy
			.contains(selectors.rowNameCell, name, { timeout: DEFAULT_TIMEOUT })
			.parents(selectors.row);
	},

	expectPagination({ current, total }) {
		const normalizedTotal = total ?? 1;
		cy
			.get(selectors.paginationStatus)
			.should('contain.text', `Page ${current} of ${normalizedTotal}`);
	},

	openEditById(id) {
		cy.get(`[data-testid="edit-${id}"]`).click();
	},

	deleteById(id) {
		cy.get(`[data-testid="delete-${id}"]`).click();
	},
	wait() {
		cy.wait(DEFAULT_TIMEOUT);
	}
};
