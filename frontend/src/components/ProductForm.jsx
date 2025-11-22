import React, { useState, useEffect } from 'react';
import { validateProduct } from '../utils/productValidation.js';
import { createProduct, updateProduct } from '../services/productService.js';

const ProductForm = ({ onActionSuccess, editingProduct }) => {
  const initialState = { name: '', price: 0, quantity: 0, category: '' };
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
    <form onSubmit={handleSubmit}>
      {/* Input Name */}
      <input 
        data-testid="name-input" 
        value={product.name} 
        onChange={(e) => setProduct({...product, name: e.target.value})} 
        placeholder="Tên sản phẩm"
      />
      {/* Hiển thị lỗi Name nếu tồn tại */}
      {errors.name && <div data-testid="name-error" style={{ color: 'red' }}>{errors.name}</div>}
      
      <button type="submit" data-testid="submit-product-btn">{editingProduct ? 'Cập nhật' : 'Lưu'}</button>
      {serverMessage && <div data-testid="server-message">{serverMessage}</div>}
    </form>
  );
};

export default ProductForm;