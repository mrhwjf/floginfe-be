import axios from 'axios';

const API_URL = 'http://localhost:8080/api/products'; // Giả định API endpoint

// // Named exports để Jest mock dễ dàng
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
  return response.data; // hoặc response.status nếu cần
}

// Giữ object tổng hợp nếu ở nơi khác dùng productService kiểu cũ
export const productService = {
  createProduct,
  getProducts,
  updateProduct,
  deleteProduct,
};


// // =============== TEST MOCK LOCALSTORAGE =================
// const LS_KEY = 'mock_products_v1';

// //true để test, set false để dùng backend
// const USE_LS_MOCK = (() => {
//   // Check process.env (Node/Jest environment)
//   if (typeof process !== 'undefined' && process.env && process.env.VITE_USE_LS_MOCK === 'false') {
//     return false;
//   }
//   // Default: enable mock for development and testing
//   return true;
// })();
// const SIMULATED_DELAY_MS = 250;

// const SEED_DATA = [
//   {
//     id: 1,
//     name: 'Laptop Dell XPS',
//     price: 600000,
//     quantity: 20,
//     category: 'LAPTOP',
//     description: 'Hiệu năng cao, thiết kế mỏng nhẹ.'
//   },
//   {
//     id: 2,
//     name: 'Điện thoại Samsung Galaxy',
//     price: 800000,
//     quantity: 12,
//     category: 'SMARTPHONE',
//     description: 'Màn hình sắc nét, camera chất lượng.'
//   },
//   {
//     id: 3,
//     name: 'Máy In Canon',
//     price: 220000,
//     quantity: 30,
//     category: 'PRINTER',
//     description: 'In ấn nhanh chóng, tiết kiệm mực.'
//   }
// ];

// function ensureSeed() {
//   const raw = localStorage.getItem(LS_KEY);
//   if (!raw) {
//     localStorage.setItem(LS_KEY, JSON.stringify(SEED_DATA));
//     return SEED_DATA.slice();
//   }
//   try {
//     return JSON.parse(raw) || [];
//   } catch {
//     localStorage.setItem(LS_KEY, JSON.stringify(SEED_DATA));
//     return SEED_DATA.slice();
//   }
// }

// function save(list) {
//   localStorage.setItem(LS_KEY, JSON.stringify(list));
// }

// function delay(ms = SIMULATED_DELAY_MS) {
//   return new Promise(r => setTimeout(r, ms));
// }

// // CRUD mock. Now refactored to use async/await (for E2E)
// export async function getProducts() {
//   if (USE_LS_MOCK) {
//     await delay();
//     return ensureSeed();
//   } else {
//     const resp = await axios.get(API_URL);
//     return resp.data.data.items; // unwrap PagedResponse from ApiResponse
//   }
// }

// export async function createProduct(productData) {
//   if (USE_LS_MOCK) {
//     await delay();
//     const list = ensureSeed();
//     const newId = list.length ? Math.max(...list.map(p => p.id)) + 1 : 1;
//     const newItem = normalizeProduct({ ...productData, id: newId });
//     list.push(newItem);
//     save(list);
//     return newItem;
//   } else {
//     const resp = await axios.post(API_URL, productData);
//     return resp.data.data; // ProductDto
//   }
// }

// export async function updateProduct(id, productData) {
//   if (USE_LS_MOCK) {
//     await delay();
//     const list = ensureSeed();
//     const idx = list.findIndex(p => p.id === id);
//     if (idx === -1) throw new Error('Product không tồn tại.');
//     list[idx] = normalizeProduct({ ...list[idx], ...productData, id });
//     save(list);
//     return list[idx];
//   } else {
//     const resp = await axios.put(`${API_URL}/${id}`, productData);
//     return resp.data.data;
//   }
// }

// export async function deleteProduct(id) {
//   if (USE_LS_MOCK) {
//     await delay();
//     const list = ensureSeed();
//     const next = list.filter(p => p.id !== id);
//     save(next);
//     return { success: true };
//   } else {
//     const resp = await axios.delete(`${API_URL}/${id}`);
//     return resp.data.success;
//   }
// }


// Chuẩn hóa dữ liệu (giúp tránh undefined)
// function normalizeProduct(p) {
//   return {
//     id: p.id,
//     name: p.name?.trim() || 'No name',
//     price: Number(p.price) || 0,
//     quantity: Number(p.quantity) || 0,
//     category: p.category || 'Other',
//     description: p.description || ''
//   };
// }

// // Giữ object tổng hợp để không phá vỡ import kiểu cũ
// export const productService = {
//   createProduct,
//   getProducts,
//   updateProduct,
//   deleteProduct
// };
