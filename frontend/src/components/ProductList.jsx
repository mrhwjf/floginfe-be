import React from 'react';

// Thêm nút Xóa & Edit cho mỗi sản phẩm để phục vụ TC-INT-02 & TC-INT-03
// onDelete: (id) => void
// onEdit: (product) => void
const ProductList = ({ products = [], onDelete, onEdit }) => {
  const items = Array.isArray(products) ? products : [];
  if (!Array.isArray(products)) {
    console.warn('ProductList: products prop is not an array, rendering empty list', products);
  }
  return (
    <ul data-testid="product-list">
      {items.map(p => (
        <li key={p.id} data-testid="product-item" style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          <span>{p.name}</span>
          <button
            type="button"
            data-testid={`edit-${p.id}`}
            onClick={() => onEdit && onEdit(p)}
          >
            Sửa
          </button>
          <button
            type="button"
            onClick={() => onDelete && onDelete(p.id)}
          >
            Xóa
          </button>
        </li>
      ))}
    </ul>
  );
};

export default ProductList;
