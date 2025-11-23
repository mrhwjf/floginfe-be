import axios from 'axios';

const API_URL = '/api/products'; // Giả định API endpoint

// Named exports để Jest mock dễ dàng
// export async function createProduct(productData) {
//     const response = await axios.post(API_URL, productData);
//     return response.data;
// }

// export async function getProducts() {
//     const response = await axios.get(API_URL);
//     return response.data;
// }

// export async function updateProduct(id, productData) {
//     const response = await axios.put(`${API_URL}/${id}`, productData);
//     return response.data;
// }

// export async function deleteProduct(id) {
//     const response = await axios.delete(`${API_URL}/${id}`);
//     return response.data; // hoặc response.status nếu cần
// }

// // Giữ object tổng hợp nếu ở nơi khác dùng productService kiểu cũ
// export const productService = {
//     createProduct,
//     getProducts,
//     updateProduct,
//     deleteProduct,
// };


// =============== TEST MOCK LOCALSTORAGE =================
const LS_KEY = 'mock_products_v1';
// Determine whether to use LocalStorage mock.
// Use Node/Jest env var `VITE_USE_LS_MOCK=true` to enable; default false.
const USE_LS_MOCK = (typeof process !== 'undefined' && process.env && process.env.VITE_USE_LS_MOCK === 'true');
const SIMULATED_DELAY_MS = 250;

const SEED_DATA = [
  {
    id: 1,
    name: 'Laptop Dell XPS',
    price: 600000,
    quantity: 20,
    category: 'Máy tính xách tay',
    description: 'Hiệu năng cao, thiết kế mỏng nhẹ.'
  },
  {
    id: 2,
    name: 'Điện thoại Samsung Galaxy',
    price: 800000,
    quantity: 12,
    category: 'Điện thoại thông minh',
    description: 'Màn hình sắc nét, camera chất lượng.'
  },
  {
    id: 3,
    name: 'Máy In Canon',
    price: 220000,
    quantity: 30,
    category: 'Máy in',
    description: 'In ấn nhanh chóng, tiết kiệm mực.'
  }
];

function ensureSeed() {
  const raw = localStorage.getItem(LS_KEY);
  if (!raw) {
    localStorage.setItem(LS_KEY, JSON.stringify(SEED_DATA));
    return SEED_DATA.slice();
  }
  try {
    return JSON.parse(raw) || [];
  } catch {
    localStorage.setItem(LS_KEY, JSON.stringify(SEED_DATA));
    return SEED_DATA.slice();
  }
}

function save(list) {
  localStorage.setItem(LS_KEY, JSON.stringify(list));
}

function delay(ms = SIMULATED_DELAY_MS) {
  return new Promise(r => setTimeout(r, ms));
}

// CRUD mock
export async function getProducts() {
  if (USE_LS_MOCK) {
    await delay();
    return ensureSeed();
  }
  throw new Error('Backend API chưa cấu hình (USE_LS_MOCK=false).');
}

export async function createProduct(productData) {
  if (USE_LS_MOCK) {
    await delay();
    const list = ensureSeed();
    const newId = list.length ? Math.max(...list.map(p => p.id)) + 1 : 1;
    const newItem = normalizeProduct({ ...productData, id: newId });
    list.push(newItem);
    save(list);
    return newItem;
  }
  throw new Error('Backend API chưa cấu hình (USE_LS_MOCK=false).');
}

export async function updateProduct(id, productData) {
  if (USE_LS_MOCK) {
    await delay();
    const list = ensureSeed();
    const idx = list.findIndex(p => p.id === id);
    if (idx === -1) throw new Error('Product không tồn tại.');
    list[idx] = normalizeProduct({ ...list[idx], ...productData, id });
    save(list);
    return list[idx];
  }
  throw new Error('Backend API chưa cấu hình (USE_LS_MOCK=false).');
}

export async function deleteProduct(id) {
  if (USE_LS_MOCK) {
    await delay();
    const list = ensureSeed();
    const next = list.filter(p => p.id !== id);
    save(next);
    return { success: true };
  }
  throw new Error('Backend API chưa cấu hình (USE_LS_MOCK=false).');
}

// Chuẩn hóa dữ liệu (giúp tránh undefined)
function normalizeProduct(p) {
  return {
    id: p.id,
    name: p.name?.trim() || 'No name',
    price: Number(p.price) || 0,
    quantity: Number(p.quantity) || 0,
    category: p.category || 'Other',
    description: p.description || ''
  };
}

// Giữ object tổng hợp để không phá vỡ import kiểu cũ
export const productService = {
  createProduct,
  getProducts,
  updateProduct,
  deleteProduct
};
