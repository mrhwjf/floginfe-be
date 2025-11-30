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

class ProductDashboardPage {
	constructor() {
		this.selectors = selectors;
	}

	visit() {
		cy.visit(PRODUCT_DASHBOARD_PATH);
		cy.get(this.selectors.table).should('be.visible');
	}

	expectRowCount(expected) {
		if (expected === 0) {
			cy.get(this.selectors.row).should('have.length', 0);
			return;
		}

		cy.get(this.selectors.row, { timeout: DEFAULT_TIMEOUT }).should('have.length', expected);
	}

	fillForm(product) {
		cy.get(this.selectors.form).should('be.visible');
		clearAndType(this.selectors.nameInput, product.name);
		clearAndType(this.selectors.priceInput, product.price);
		clearAndType(this.selectors.quantityInput, product.quantity);
		cy.get(this.selectors.categorySelect).select(product.category);
		clearAndType(this.selectors.descriptionInput, product.description);
	}

	setSearchTerm(value) {
		cy.get(this.selectors.searchInput).clear().type(value);
	}

	applyFilters() {
		cy.get(this.selectors.applyFiltersButton).click();
		cy.get(this.selectors.table).should('be.visible');
	}

	searchByName(name) {
		this.setSearchTerm(name);
		this.applyFilters();
	}

	submit() {
		cy.get(this.selectors.submitButton).click();
	}

	selectRowByName(name) {
		return cy
			.contains(this.selectors.rowNameCell, name, { timeout: DEFAULT_TIMEOUT })
			.parents(this.selectors.row);
	}

	expectPagination({ current, total }) {
		const normalizedTotal = total ?? 1;
		cy
			.get(this.selectors.paginationStatus)
			.should('contain.text', `Page ${current} of ${normalizedTotal}`);
	}

	openEditById(id) {
		cy.get(`[data-testid="edit-${id}"]`).click();
	}

	deleteById(id) {
		cy.get(`[data-testid="delete-${id}"]`).click();
	}

	wait() {
		cy.wait(DEFAULT_TIMEOUT);
	}
}

export const productDashboardPage = new ProductDashboardPage();
export default ProductDashboardPage;
