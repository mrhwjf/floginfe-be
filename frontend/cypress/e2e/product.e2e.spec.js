/* global describe, it, cy */
import { productDashboardPage } from '../pages/ProductDashboardPage';
import {
	buildProductPayload,
	fetchPagedProducts,
} from '../support/e2e';

describe('Product dashboard CRUD happy paths', () => {
	it('lists backend products on initial load', () => {
		productDashboardPage.visit();

		fetchPagedProducts({ page: 0, size: 10 }).then((pagedResponse) => {
			const backendProducts = pagedResponse.items; // Array of products from backend
			console.log('Backend products:', backendProducts);
			const totalElements = pagedResponse.totalElements;
			const totalPages = pagedResponse.totalPages;

			if (totalElements > 0) {
				const expectedRowCount = Math.min(backendProducts.length, 10);
				productDashboardPage.expectRowCount(expectedRowCount);

				productDashboardPage.expectPagination({
					current: 1,
					total: totalPages
				});

				const firstBackendProduct = backendProducts[0];

				productDashboardPage.selectRowByName(firstBackendProduct.name).within(() => {
					cy.get('[data-testid="cell-price"]').should('contain', firstBackendProduct.price);
					cy.get('[data-testid="cell-quantity"]').should('contain', firstBackendProduct.quantity);
					cy.get('[data-testid="cell-category"]').should('contain', firstBackendProduct.category);
				});
			}
		});
	});

	it('creates a product via form submission and renders it in the table', () => {
		const newProduct = buildProductPayload({ category: 'LAPTOP', description: 'Created via Cypress happy path' });
		productDashboardPage.visit();
		productDashboardPage.fillForm(newProduct);
		productDashboardPage.submit();
		productDashboardPage.wait();
		productDashboardPage.searchByName(newProduct.name);
	});

	it('updates a product via form submission and shows new fields', () => {
		const originalProduct = buildProductPayload({ name: `E2E Update ${Date.now()}` });
		const updatedProduct = buildProductPayload({
			name: `E2E Updated ${Date.now()}`,
			price: 1999000,
			quantity: 10,
			category: 'SMARTPHONE',
			description: 'Updated via Cypress test'
		});

		productDashboardPage.visit();
		productDashboardPage.fillForm(originalProduct);
		productDashboardPage.submit();
		productDashboardPage.wait();

		productDashboardPage.searchByName(originalProduct.name);

		let productId;
		productDashboardPage.selectRowByName(originalProduct.name).within(() => {
			cy.get('[data-testid*="edit-"]').invoke('attr', 'data-testid').then((editButtonId) => {
				productId = editButtonId.replace('edit-', '');
				productDashboardPage.openEditById(productId);
			});
		});

		productDashboardPage.fillForm(updatedProduct);
		productDashboardPage.submit();
		productDashboardPage.wait();
		productDashboardPage.searchByName(updatedProduct.name);

		productDashboardPage.selectRowByName(updatedProduct.name).within(() => {
			cy.get('[data-testid="cell-price"]').should('contain', updatedProduct.price);
			cy.get('[data-testid="cell-quantity"]').should('contain', updatedProduct.quantity);
			cy.get('[data-testid="cell-category"]').should('contain', updatedProduct.category);
		});
	});

	it('deletes a product via button and removes it from the UI', () => {
		const productToDelete = buildProductPayload({ name: `E2E Delete ${Date.now()}` });

		productDashboardPage.visit();
		productDashboardPage.fillForm(productToDelete);
		productDashboardPage.submit();
		productDashboardPage.wait();
		productDashboardPage.searchByName(productToDelete.name);

		let productId;
		productDashboardPage.selectRowByName(productToDelete.name).within(() => {
			cy.get('[data-testid*="delete-"]').invoke('attr', 'data-testid').then((deleteButtonId) => {
				productId = deleteButtonId.replace('delete-', '');
				productDashboardPage.deleteById(productId);
			});
		});
		productDashboardPage.wait();
		productDashboardPage.searchByName(productToDelete.name);
		productDashboardPage.expectRowCount(0);
	});
});
