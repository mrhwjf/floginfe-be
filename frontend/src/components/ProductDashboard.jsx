// frontend/src/components/ProductDashboard.jsx
import React, { useState, useEffect } from 'react';
import ProductList from './ProductList';
import ProductForm from './ProductForm';
import { getProducts, deleteProduct } from '../services/productService.js'; // named exports

const ProductDashboard = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingProduct, setEditingProduct] = useState(null);

    // Hàm fetch dữ liệu
    const fetchProducts = async () => {
        try {
            setLoading(true);
            const data = await getProducts();
            // Chuẩn hóa dữ liệu trả về: API có thể trả về array trực tiếp hoặc object chứa array
            let normalized = [];
            if (Array.isArray(data)) {
                normalized = data;
            } else if (Array.isArray(data?.data)) {
                normalized = data.data;
            } else if (Array.isArray(data?.products)) {
                normalized = data.products;
            } else {
                // Nếu không phải các dạng trên, log để debug
                console.warn('fetchProducts: unexpected data shape, default to empty array', data);
            }
            setProducts(normalized);
        } catch (error) {
            console.error("Error fetching products:", error);
        } finally {
            setLoading(false);
        }
    };

    // Hàm xử lý sau khi tạo/cập nhật thành công
    const handleProductActionSuccess = () => {
        // Sau khi tạo hoặc cập nhật thành công, tự động fetch lại danh sách để cập nhật
        fetchProducts();
        setEditingProduct(null); // Reset về chế độ tạo mới
    };
    const handleEditProduct = (product) => {
        setEditingProduct(product);
    };

    // Hàm xóa sản phẩm
    const handleDeleteProduct = async (id) => {
        try {
            await deleteProduct(id);
            // Sau khi xóa thành công, fetch lại danh sách
            fetchProducts();
        } catch (error) {
            console.error('Error deleting product:', error);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    // Giả định bạn truyền hàm callback xuống ProductForm
    return (
        <div>
            <h2>Product Management Dashboard</h2>
            <ProductForm onActionSuccess={handleProductActionSuccess} editingProduct={editingProduct} />
            {loading ? <div>Đang tải...</div> : <ProductList products={products} onDelete={handleDeleteProduct} onEdit={handleEditProduct} />}
        </div>
    );
};

export default ProductDashboard;