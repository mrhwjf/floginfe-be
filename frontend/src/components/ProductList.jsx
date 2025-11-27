import React from 'react';
import './ProductUI.css';

const ProductList = ({ products = [], onDelete, onEdit }) => {
  const items = Array.isArray(products) ? products : [];
  if (!Array.isArray(products)) {
    console.warn('ProductList: products prop is not an array, rendering empty list', products);
  }
  return (
    <div className="card">
      <div className="card-header">Danh sách sản phẩm</div>
      <div className="table-wrapper">
        <table className="product-table" data-testid="product-list">
          <thead>
            <tr>
              <th>Tên</th>
              <th>Giá</th>
              <th>Số lượng</th>
              <th>Category</th>
              <th>Mô tả</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {items.map((p) => (
              <tr key={p.id} data-testid="product-item">
                <td>{p?.name ?? '-'}</td>
                <td>{p?.price ?? '-'}</td>
                <td>{p?.quantity ?? '-'}</td>
                <td>{p?.category ?? '-'}</td>
                <td className="desc-cell">{p?.description ?? '-'}</td>
                <td className="actions">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    data-testid={`edit-${p.id}`}
                    onClick={() => onEdit && onEdit(p)}
                  >
                    Sửa
                  </button>
                  <button
                    type="button"
                    className="btn btn-danger"
                    onClick={() => onDelete && onDelete(p.id)}
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
            {items.length === 0 && (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
                  Không có sản phẩm phù hợp
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ProductList;
