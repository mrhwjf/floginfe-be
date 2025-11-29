// Cypress support file kept minimal for now.

const API_BASE_URL = Cypress.env('backendUrl') ?? 'http://localhost:8080';
const FETCH_PRODUCTS_API = `${API_BASE_URL}/api/products`;
const DEFAULT_CATEGORY = 'LAPTOP';

export const PRODUCT_DASHBOARD_PATH = '/dashboard';

export const DEFAULT_TIMEOUT = 3000;

const DEFAULT_PRICE = 999000;
const DEFAULT_QUANTITY = 5;

// Build product payload helper moved to productCrud.cy.js
export const buildProductPayload = (overrides = {}) => ({
	name: `E2E Product ${Date.now()}-${Math.floor(Math.random() * 1000)}`,
	price: DEFAULT_PRICE,
	quantity: DEFAULT_QUANTITY,
	category: DEFAULT_CATEGORY,
	description: 'E2E automation product',
	...overrides,
});

// Temporary debug version
export const fetchPagedProducts = ({ page = 0, size = 10, filters = {} } = {}) =>
	cy.request({
		method: 'GET',
		url: FETCH_PRODUCTS_API,
		qs: {
			page,
			size,
			search: filters.search,
			minPrice: filters.priceMin,
			maxPrice: filters.priceMax,
			minQuantity: filters.qtyMin,
			maxQuantity: filters.qtyMax,
			category: filters.category,
		},
	})
		.then((response) => {
			console.log('Full response:', response);
			console.log('Response body:', response.body);
			console.log('Response body.data:', response.body.data);
			return response.body.data; // Return full body for now
		});
