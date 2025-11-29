import axios from 'axios';

const API_URL = 'http://localhost:8080/api/products'; // Giả định API endpoint

export async function createProduct(productData) {
  const response = await axios.post(API_URL, productData);
  return response.data;
}

export async function getProducts({ page, size, filters }) {
  const response = await axios.get(API_URL, {
    params: {
      page,
      size,
      search: filters.search,
      category: filters.category,
      minPrice: filters.priceMin,
      maxPrice: filters.priceMax,
      minQuantity: filters.qtyMin,
      maxQuantity: filters.qtyMax,
    }
  });

  return response.data;
}

export async function updateProduct(id, productData) {
  const response = await axios.put(`${API_URL}/${id}`, productData);
  return response.data;
}

export async function deleteProduct(id) {
  const response = await axios.delete(`${API_URL}/${id}`);
  return response.data;
}

export const productService = {
  createProduct,
  getProducts,
  updateProduct,
  deleteProduct,
};