import React, { useState, useEffect } from 'react';
import { validateProduct, VALID_CATEGORIES } from '../utils/productValidation.js';
import { createProduct, updateProduct } from '../services/productService.js';
import './ProductUI.css';

const ProductForm = ({ onActionSuccess, editingProduct }) => {
  const initialState = {
    name: '',
    price: 0,
    quantity: 0,
    category: (Array.isArray(VALID_CATEGORIES) && VALID_CATEGORIES.length > 0) ? VALID_CATEGORIES[0] : '',
    description: ''
  };
  const [product, setProduct] = useState(initialState);
  const [errors, setErrors] = useState({});
  const [serverMessage, setServerMessage] = useState('');

  // Khi chuyển sang chế độ edit, preload dữ liệu
  useEffect(() => {
    if (editingProduct) {
      setProduct({
        name: editingProduct.name ?? '',
        price: editingProduct.price ?? 0,
        quantity: editingProduct.quantity ?? 0,
        category: editingProduct.category ?? '',
        description: editingProduct.description ?? '',
      });
    } else {
      setProduct(initialState);
    }
  }, [editingProduct]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerMessage('');
    const validationErrors = validateProduct(product);
    setErrors(validationErrors);
    if (Object.keys(validationErrors).length === 0) {
      try {
        let result;
        if (editingProduct) {
          // Chế độ cập nhật
          result = await updateProduct(editingProduct.id, product);
          setServerMessage(result?.message || 'Cập nhật sản phẩm thành công');
        } else {
          // Chế độ tạo mới
          result = await createProduct(product);
          setServerMessage(result?.message || 'Tạo sản phẩm thành công');
        }

        if (onActionSuccess) {
          onActionSuccess();
        }
      } catch (err) {
        setServerMessage(err.message || (editingProduct ? 'Lỗi cập nhật sản phẩm' : 'Lỗi tạo sản phẩm'));
      }
    }
  };

  return (
    <div className="card">
      <div className="card-header">{editingProduct ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm'}</div>
      <form onSubmit={handleSubmit} className="form-grid" data-testid="product-form">
        {/* Name */}
        <div className="form-control">
          <label>Tên sản phẩm</label>
          <input
            data-testid="name-input"
            value={product.name}
            onChange={(e) => setProduct({ ...product, name: e.target.value })}
            placeholder="Tên sản phẩm"
          />
          {errors.name && (
            <div data-testid="name-error" className="error-text">{errors.name}</div>
          )}
        </div>

        {/* Price */}
        <div className="form-control">
          <label>Giá</label>
          <input
            type="number"
            data-testid="price-input"
            value={product.price}
            onChange={(e) => setProduct({ ...product, price: Number(e.target.value) })}
          // placeholder="0"
          // min="0"
          />
          {errors.price && <div className="error-text">{errors.price}</div>}
        </div>

        {/* Quantity */}
        <div className="form-control">
          <label>Số lượng</label>
          <input
            type="number"
            data-testid="quantity-input"
            value={product.quantity}
            onChange={(e) => setProduct({ ...product, quantity: Number(e.target.value) })}
          // placeholder="0"
          // min="0"
          />
          {errors.quantity && <div className="error-text">{errors.quantity}</div>}
        </div>

        {/* Category */}
        <div className="form-control">
          <label>Category</label>
          <select
            data-testid="category-select"
            value={product.category}
            onChange={(e) => setProduct({ ...product, category: e.target.value })}
          >
            <option value="">-- Chọn Category --</option>
            {(Array.isArray(VALID_CATEGORIES) ? VALID_CATEGORIES : ['Máy tính xách tay', 'Máy tính để bàn', 'Điện thoại thông minh', 'Máy tính bảng',
              'Thiết bị đeo thông minh', 'Màn hình', 'Máy in', 'Phụ kiện', 'Thiết bị mạng']).map((c) => (
                <option key={c} value={c}>{c}</option>
              ))}
          </select>
          {errors.category && <div className="error-text">{errors.category}</div>}
        </div>

        {/* Description */}
        <div className="form-control form-control--full">
          <label>Mô tả</label>
          <textarea
            rows="3"
            data-testid="description-input"
            value={product.description}
            onChange={(e) => setProduct({ ...product, description: e.target.value })}
            placeholder="Mô tả sản phẩm (tối đa 500 ký tự)"
          />
          {errors.description && <div className="error-text">{errors.description}</div>}
        </div>

        {/* Submit */}
        <div className="form-actions">
          <button type="submit" className="btn btn-primary" data-testid="submit-product-btn">
            {editingProduct ? 'Cập nhật' : 'Lưu'}
          </button>
          {serverMessage && <div data-testid="server-message" className="server-message">{serverMessage}</div>}
        </div>
      </form>
    </div>
  );
};

export default ProductForm;