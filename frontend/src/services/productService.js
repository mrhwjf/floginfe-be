import axios from 'axios';

const API_URL = '/api/products'; // Giả định API endpoint

// Named exports để Jest mock dễ dàng
export async function createProduct(productData) {
    const response = await axios.post(API_URL, productData);
    return response.data;
}

export async function getProducts() {
    const response = await axios.get(API_URL);
    return response.data;
}

export async function updateProduct(id, productData) {
    const response = await axios.put(`${API_URL}/${id}`, productData);
    return response.data;
}

export async function deleteProduct(id) {
    const response = await axios.delete(`${API_URL}/${id}`);
    return response.data; // hoặc response.status nếu cần
}

// Giữ object tổng hợp nếu ở nơi khác dùng productService kiểu cũ
export const productService = {
    createProduct,
    getProducts,
    updateProduct,
    deleteProduct,
};
